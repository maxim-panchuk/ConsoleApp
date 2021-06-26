package element;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Movie implements Comparable<Movie>, Serializable {

    private final String regex1 = "[0-9]+";

    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0,
    // Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private final int oscarsCount; //Значение поля должно быть больше 0
    private final int goldenPalmCount; //Значение поля должно быть больше 0
    private final MovieGenre genre; //Поле может быть null
    private final element.MpaaRating mpaaRating; //Поле не может быть null
    private final element.Person director; //Поле не может быть null
    private final LocalDateTime time;

    public Movie (Scanner scanner, int id) {
        this.id = id;
        name = setName(scanner);
        coordinates = setCoordinates(scanner);
        oscarsCount = setOscarsCount(scanner);
        goldenPalmCount = setGoldenPalmCount(scanner);
        genre = setMovieGenre(scanner);
        mpaaRating = setMpaaRating(scanner);
        director = setDirector(scanner);
        time = setTime();
    }

    public void setId (int id) {
        //return Math.toIntExact((java.util.UUID.randomUUID().getMostSignificantBits() & Integer.MAX_VALUE));
        this.id = id;
    }
    public String setName (Scanner scanner) {
        String enteredName;
        while (true) {
            System.out.println("Название фильма: ");
            enteredName = scanner.nextLine().trim();
            if (enteredName.isEmpty()) {
                System.out.println("Название не должно быть пустым, повторите ввод.");
            } else {
                break;
            }
        }
        return enteredName;
    }
    public Coordinates setCoordinates(Scanner scanner) {
        return new Coordinates(scanner);
    }
    public int setOscarsCount (Scanner scanner) {
        int oscars;
        while (true) {
            try {
                System.out.println("Количество оскаров: " + getName() + " (не меньше 1) ?");
                String enterMovieOscars = scanner.nextLine().trim();
                if (!enterMovieOscars.matches(regex1)) {
                    System.out.println("Введи число в натуральных числах");
                } else {
                    oscars = Integer.parseInt(enterMovieOscars);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Слишком большое число, повторите ввод");
            }
        }
        return oscars;
    }

    public int setGoldenPalmCount (Scanner scanner) {
        int goldenPalmCount;
        while (true) {
            try {
                System.out.println("Количество наград: (не менее 1) :   ");
                String enterGoldenPalmCount = scanner.nextLine().trim();
                if (!enterGoldenPalmCount.matches(regex1))
                    System.out.println("Введите число в натуральных числах");
                else {
                    goldenPalmCount = Integer.parseInt(enterGoldenPalmCount);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Слишком большое число, повторите ввод");
            }
        }
        return goldenPalmCount;
    }

    public MovieGenre setMovieGenre (Scanner scanner) {
        System.out.println("Жанр : COMEDY ; ADVENTURE ; THRILLER ");
        MovieGenre newMovieGenre;
        switch (scanner.nextLine()) {                                                      // Возможно улучшение с while
            case "COMEDY": {
                newMovieGenre = MovieGenre.COMEDY;
                break;
            }
            case "ADVENTURE": {
                newMovieGenre = MovieGenre.ADVENTURE;
                break;
            }
            case "THRILLER": {
                newMovieGenre = MovieGenre.THRILLER;
                break;
            }
            default: {
                //System.out.println("Некорректный ввод жанра, жанр будет установлен по умолчанию");
                newMovieGenre = MovieGenre.ADVENTURE;
            }
        }
        return newMovieGenre;
    }

    public element.MpaaRating setMpaaRating (Scanner scanner) {
        System.out.println("Возрастной рейтинг : G ; PG ; PG_13 ; R ; NC_17 ");
        element.MpaaRating newMpaaRating;
        switch (scanner.nextLine()) {                                                      // Возможно улучшение с while
            case "G": {
                newMpaaRating = element.MpaaRating.G;
                break;
            }
            case "PG": {
                newMpaaRating = element.MpaaRating.PG;
                break;
            }
            case "PG_13": {
                newMpaaRating = element.MpaaRating.PG_13;
                break;
            }
            case "NC_17": {
                newMpaaRating = element.MpaaRating.NC_17;
                break;
            }
            default: {
                //System.out.println("Некорректный ввод возрастной категории, категория будет установлена по умолчанию");
                newMpaaRating = element.MpaaRating.G;
            }
        }
        return newMpaaRating;
    }

    public element.Person setDirector (Scanner scanner) {
        return new element.Person(scanner);
    }

    public LocalDateTime setTime () {
        return LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public element.Person getDirector() {
        return director;
    }

    @Override
    public int compareTo(Movie p) {
        return this.name.length() - p.name.length();
    }

    @Override
    public String toString() {
        return "element.Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", oscarsCount=" + oscarsCount +
                ", goldenPalmCount=" + goldenPalmCount +
                ", genre=" + genre +
                ", mpaaRating=" + mpaaRating +
                ", director=" + director +
                ", time='" + time + '\'' +
                '}';
    }
}