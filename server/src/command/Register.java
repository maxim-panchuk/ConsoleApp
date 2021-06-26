package command;

import database.MovieCollectionManager;
import element.Message;
import element.User;
import serverWorker.Server;


public class Register {
    MovieCollectionManager movieCollectionManager = Server.movieCollectionManager;
    public String run(Message message) {
        String[] arguments = message.getArgument().split(" ");
        String log = arguments[0];
        String pass = arguments[1];
        if (arguments[1].equals(arguments[2])) {
            User user = new User(log, pass);
            message.setUser(user);
            if (movieCollectionManager.isLoginUsed(message)) {
                return "Пользователь с таким логином уже зарегестрирован";
            } else if (movieCollectionManager.isPasswordUsed(message)) {
                return "Такой пароль уже используется, придумайте другой";
            } else {
                movieCollectionManager.insertUser(message);
                String login = message.getUser().getLogin();
                String password = message.getUser().getPassword();
                String encryptedPassword = MovieCollectionManager.encryptPassword(password);
                movieCollectionManager.insertUserToDb(login, encryptedPassword);
                return "Новый пользователь успешно зарегестриован";
            }
        } else {
            return "Пароли не совпадают";
        }

    }
}