package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import element.Message;
import element.Movie;
import element.Person;
import element.User;
import serverWorker.Server;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Класс для работы с коллекцией в памяти
public class MovieCollectionManager {

    // ========================== Инициализация коллекции в памяти, скачка с БД ========================================================================================
    Connection connection = Server.connection;
    public HashSet<Integer> idSet;                                      // ID всех элементов
    public LocalDateTime creationDate;
    public Map<String, String> users;                                   // Пользователи
    public PriorityQueue<Movie> dataSet;                                // Объекты коллекции
    public Map<String, ArrayList<Integer>> owners;                      // Наличность пользователя
    ReadWriteLock lock = new ReentrantReadWriteLock();

    public MovieCollectionManager() throws SQLException {
        this.users = setUsers();
        this.dataSet = setDataSet();
        this.owners = setOwners();
        this.idSet = setIdSet();
        this.creationDate = setCreationDate();
    }

    public Map<String, String> setUsers() throws SQLException {
        users = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

        while(resultSet.next()) {
            String login = resultSet.getString(1);
            String hashedPassword = resultSet.getString(2);
            users.put(login, hashedPassword);
        }
        resultSet.close();
        statement.close();

        return users;
    }

    public Map<String, ArrayList<Integer>> setOwners() throws SQLException {
        owners = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movie_table");

        while (resultSet.next()) {
            String login = resultSet.getString(3);
            int id = resultSet.getInt(1);
            if (!owners.containsKey(login)) {
                owners.putIfAbsent(login, new ArrayList<>());
            }
            ArrayList<Integer> value = owners.get(login);
            value.add(id);
            owners.replace(login, value);
        }
        resultSet.close();
        statement.close();

        return owners;
    }

    public PriorityQueue<Movie> setDataSet() throws SQLException {
        dataSet = new PriorityQueue<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movie_table");
        while (resultSet.next()) {
            String element = resultSet.getString(2);
            int id = resultSet.getInt(1);
            Movie movie = gson.fromJson(element, Movie.class);
            movie.setId(id);
            dataSet.add(movie);
        }
        resultSet.close();
        statement.close();

        return dataSet;
    }

    public HashSet<Integer> setIdSet() {
        idSet = new HashSet<>();
        for (Movie movie : dataSet) {
            idSet.add(movie.getId());
        }

        return idSet;
    }

    public LocalDateTime setCreationDate() {
        creationDate = LocalDateTime.now();

        return creationDate;
    }

    // ================================  Работа с пользователем ==================================================================================

    public boolean isLoginUsed (Message message) {
        User user = message.getUser();
        String login = user.getLogin();
        return users.containsKey(login);
    }

    public boolean doesPasswordMatch (Message message) {
        String login = message.getUser().getLogin();
        String encryptedPassword = encryptPassword(message.getUser().getPassword());
        String passwordToMatch = users.get(login);
        return passwordToMatch.equals(encryptedPassword);
    }

    public boolean isPasswordUsed (Message message) {
        User user = message.getUser();
        String password = user.getPassword();
        String encryptedPassword = encryptPassword(password);
        return users.containsValue(encryptedPassword);
    }

    public void insertUser (Message message) {
        User user = message.getUser();
        String login = user.getLogin();
        String password = user.getPassword();
        String encryptedPassword = encryptPassword(password);
        users.putIfAbsent(login, encryptedPassword);

    }

    public void insertUserToDb (String login, String encryptedPassword) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (login, password) VALUES (?, ?)"
            );
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, encryptedPassword);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String encryptPassword (String password) {
        try {
            byte[] bitPassword = password.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha256 = MessageDigest.getInstance("SHA-512");
            sha256.update(bitPassword);
            byte[] digest = sha256.digest(bitPassword);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Не удалось зашифровать пароль");
            return "123Э";
        }
    }

    //==========================================================================================================================================================================

    public String insertMovieToDb (Message message) {

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Movie movie = message.getMovie();
            String login = message.getUser().getLogin();
            String convertedMovie = gson.toJson(movie);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO movie_table " +
                            "(ELEMENT, LOGIN) VALUES (?, ?)"
            );
            preparedStatement.setString(1, convertedMovie);
            preparedStatement.setString(2, login);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return this.insertMovieToCollection(convertedMovie);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Ошибка обращения к БД";
        }
    }
    ////--------------------
    private String insertMovieToCollection (String convertedMovie) {
        lock.writeLock().lock();
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM movie_table WHERE element = ?");
            preparedStatement.setString(1, convertedMovie);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String line = resultSet.getString(2);
                Movie movie = gson.fromJson(line, Movie.class);
                movie.setId(resultSet.getInt(1));
                String login = resultSet.getString(3);
                if (!this.owners.containsKey(login)) {
                    this.owners.put(login, (new ArrayList<>()));
                }
                this.owners.get(login).add(resultSet.getInt(1));
                this.dataSet.add(movie);
            }
            resultSet.close();
            preparedStatement.close();
            return "Элемент успешно добавлен";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Ошибка обращения к БД";
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String deleteMovieById (Message message) {
        lock.writeLock().lock();
        try {
            User user = message.getUser();
            String login = user.getLogin();
            String arguments = message.getArgument();
            int Id = Integer.parseInt((arguments.trim().split(" "))[0]);
            if (owners.containsKey(login)) {
                if (loginHasMovie(message)) {
                    Iterator<Movie> movieIterator = dataSet.iterator();
                    Movie movie;
                    while (movieIterator.hasNext()) {
                        movie = movieIterator.next();
                        if (movie.getId() == Id) {
                            try {
                                dataSet.remove(movie);
                                ArrayList<Integer> ids = owners.get(login);
                                for (int i = 0; i < ids.size(); i++) {
                                    if (ids.get(i) == Id) {
                                        owners.get(login).remove(i);
                                    }
                                }
                                PreparedStatement preparedStatement = connection.prepareStatement(
                                        "DELETE FROM movie_table WHERE movie_id = ?"
                                );
                                preparedStatement.setInt(1, Id);
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                return "Элемент успешно удален";
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                                return "Возникла ошибка при обращении к БД";
                            }
                        }
                    }
                    return "Такого элемента нет в коллекции";
                } else {

                    return "Текущий пользователь не может работать с этим элементом \n" +
                            "Доступные вам элементы: " + owners.get(login).toString();
                }
            } return "У вас еще нет ни одного элемента";
        } finally {
            lock.writeLock().unlock();
        }
    }


    private boolean loginHasMovie (Message message) {
        String login = message.getUser().getLogin();
        int id = Integer.parseInt(message.getArgument());
        if (this.owners.containsKey(login)) {
            return this.owners.get(login).contains(id);
        } else return false;
    }


    public String clear (Message message) {
        lock
                .writeLock()
                .lock();
        try {
            User user = message.getUser();
            String login = user.getLogin();
            if (owners.containsKey(login)) {
                ArrayList<Integer> ownerMovies = owners.get(login);
                Iterator<Movie> movieIterator = dataSet.iterator();
                while (movieIterator.hasNext()) {
                    Movie movie = movieIterator.next();
                    try {
                        for (Integer ownerMovie : ownerMovies) {
                            if (movie.getId().equals(ownerMovie)) {
                                movieIterator.remove();
                            }
                        }
                    } catch (NullPointerException e) {
                        return "Ваших элементов в базе данных нет";
                    }

                }
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "DELETE FROM movie_table WHERE login = ?"
                    );
                    preparedStatement.setString(1, login);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    owners.get(login).clear();
                    return "Все ваши элементы успешно удалены";
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return "Ошибка обращения к БД";
                }
            } return "Ваша коллекция пуста";
        } finally {
            lock.writeLock().unlock();
        }


    }

    public String updateElement(Message message) {
        lock.writeLock().lock();
        try {
            User user = message.getUser();
            String login = user.getLogin();
            Movie movie = message.getMovie();
            String argument = message.getArgument();
            int id = Integer.parseInt(argument);
            if (owners.containsKey(login)) {
                if (loginHasMovie(message)) {
                    Movie movieToUpdate = null;
                    for (Movie mov : dataSet) {
                        if (mov.getId().equals(id)) {
                            movieToUpdate = mov;
                            break;
                        }
                    }
                    dataSet.remove(movieToUpdate);
                    movie.setId(id);
                    dataSet.add(movie);
                    try {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String jsonedMovie = gson.toJson(movie);
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "UPDATE movie_table SET element=? WHERE movie_id=?"
                        );
                        preparedStatement.setString(1, jsonedMovie);
                        preparedStatement.setInt(2, id);
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        return "Ошибка обращения к БД";
                    }
                    return "Элемент успешно обновлен";
                }
                return "Текущий пользователь не может работать с этим элементом \n" +
                        "Доступные вам элементы: " + owners.get(login).toString();
            } return "У вас еще нет ни одного элемента";
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String deleteGreater (Message message) {
        lock.writeLock().lock();
        try {
            int id = Integer.parseInt(message.getArgument());
            User user = message.getUser();
            String login = user.getLogin();

            if (moreIds(message)) {
                ArrayList<Integer> idsToDelete = owners.get(login);
                dataSet.removeIf(mov -> mov.getId() > id && idsToDelete.contains(mov.getId()));
                owners.get(login).removeIf(idd -> idd > id);
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "DELETE FROM movie_table WHERE movie_id > ? AND login = ?"
                    );
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, login);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else return "В вашем распоряжении нет элементов, с id больше, чем " + id;
            return "Элементы, с id большие, чем " + id + " успешно удалены";
        } finally {
            lock.writeLock().unlock();
        }
    }
    //==========================================================================================================================================================================

    private boolean moreIds (Message message) {
        User user = message.getUser();
        String login = user.getLogin();
        int id = Integer.parseInt(message.getArgument());
        if (owners.containsKey(login)) {
            ArrayList<Integer> ids = owners.get(login);
            for (Integer idArr : ids) {
                if (idArr > id) {
                    return true;
                }
            }
        }
        return false;
    }

    public String deleteHead (Message message) {
        lock.writeLock().lock();
        try {
            String login = message.getUser().getLogin();
            if (owners.containsKey(login)) {
                ArrayList<Integer> ids = owners.get(login);
                int minId = 99999;
                for (Integer id : ids) {
                    if (id < minId) {
                        minId = id;
                    }
                }
                Iterator<Movie> movieIterator = dataSet.iterator();
                Movie movie;
                while (movieIterator.hasNext()) {
                    movie = movieIterator.next();
                    if (movie.getId() == minId) {
                        try {
                            for (int i = 0; i < ids.size(); i++) {
                                if (ids.get(i) == minId) {
                                    owners.get(login).remove(i);
                                }
                            }
                            PreparedStatement preparedStatement = connection.prepareStatement(
                                    "DELETE FROM movie_table WHERE movie_id = ?"
                            );
                            preparedStatement.setInt(1, minId);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            return "Элемент успешно удален";
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            return "Возникла ошибка при обращении к БД";
                        }
                    }
                }
                return "Такого элемента нет в коллекции";
            } return "Ваша коллекция пуста";
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String insertIfMin(Message message) {
        lock.writeLock().lock();
        try {
            Movie newMovie = message.getMovie();
            for (Movie movie : dataSet) {
                if (movie.getName().length() < newMovie.getName().length()) {
                    return "Ваш элемент не является минимальным";
                }
            }
            this.insertMovieToDb(message);
            return "Элемент успешно добавлен";
        } finally {
            lock.writeLock().unlock();
        }

    }

    public String outInfo() {
        lock.readLock().lock();
        try {
            return "Database init time: " + creationDate + "\n" +
                    "Amount of elements: " + dataSet.size() + "\n" +
                    "Collection type:    " + dataSet.getClass().getSimpleName();
        } finally {
            lock.readLock().unlock();
        }

    }

    public String show() {
        lock.readLock().lock();
        try {
            return defineAnswer(dataSet);
        } finally {
            lock.readLock().unlock();
        }


    }

    public String printAscending () {
        lock.readLock().lock();
        try {
            ArrayList<Integer> list = new ArrayList<>();
            dataSet.forEach(movie -> list.add(movie.getId()));
            class MovieComparator implements Comparator<Integer> {
                @Override
                public int compare(Integer id1, Integer id2) {
                    return id1.compareTo(id2);
                }
            }

            PriorityQueue<Movie> arr = new PriorityQueue<>();
            Collections.sort(list);
            Collections.reverse(list);
            for (Integer id : list) {
                for (Movie movie : dataSet) {
                    if (id.equals(movie.getId())) {
                        arr.add(movie);
                    }
                }
            }
            return defineAnswer(arr);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String printAscendingDirector () {
        lock.readLock().lock();
        try {
            ArrayList<Person> dirNames = new ArrayList<>();
            dataSet.forEach(movie -> dirNames.add(movie.getDirector()));
            class DirNamesComparator implements Comparator<Person> {
                @Override
                public int compare (Person dirName1, Person dirName2) {
                    return dirName1.getName().length() - dirName2.getName().length();
                }
            }
            Comparator<Person> comparator = new DirNamesComparator();
            dirNames.sort(comparator);
            ArrayList<String> arr = new ArrayList<>();
            for (Person name : dirNames) {
                arr.add(name.getName());
            }
            return Arrays.toString(arr.toArray());
        } finally {
            lock.readLock().unlock();
        }
    }

    public String nameFilter (String regex) {
        lock.readLock().lock();
        try {
            ArrayList<String> arr = new ArrayList<>();
            for (Movie movie : dataSet) {
                if (movie.getName().contains(regex)) {
                    arr.add(movie.toString());
                }
            }
            return Arrays.toString(arr.toArray());
        } finally {
            lock.readLock().unlock();
        }

    }

    public String help () {
        return
                          "help                             : справочник команд" + "\n"
                        + "info                             : информация о коллекции " + "\n"
                        + "show                             : вывести элементы коллекции " + "\n"
                        + "add                              : добавить элемент " + "\n"
                        + "update {id}                      : обновить значения элемента " + "\n"
                        + "remove {id}                      : удалить элемент " + "\n"
                        + "clear                            : очистить коллекцию" + "\n"
                        + "exit                             : выход " + "\n"
                        + "remove_head                      : удалить первый элемент " + "\n"
                        + "add_if_min {element}             : добавить, если минимальный " + "\n"
                        + "remove_greater {id}              : удалить большие элементы " + "\n"
                        + "filter_starts_with_name {name}   : отфильтровать элементы " + "\n"
                        + "print_ascending                  : сортировать по возрастанию " + "\n"
                        + "print_field_ascending_director   : вывести \"director\" по возрастанию ";

    }

    private static String defineAnswer (PriorityQueue<Movie> dataSet) {
        ArrayList<String> answer = new ArrayList<>();
        for (Movie movie : dataSet) {
            answer.add(movie.toString() + "\n");
        }
        return Arrays.toString(answer.toArray());
    }
    ///--------------------------

    public static void sout_users () {
        try {
            Statement statement = Server.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String str = "";
                for (int i = 1; i <= 2; i++) {
                    str += resultSet.getString(i) + " : ";
                }
                System.out.println("User: " + str);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("");
        }

    }

    private String returnPassword () {
        Console console = System.console();
        char[] chars = console.readPassword();
        return new String(chars);
    }
}
