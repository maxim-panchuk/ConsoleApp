package command;

import database.MovieCollectionManager;
import element.Message;
import serverWorker.Server;
import serverWorker.SocketThread;

import java.util.Iterator;
import java.util.Map;

public class SignIn {
    MovieCollectionManager movieCollectionManager = Server.movieCollectionManager;
    public String run (Message message) {
        if (movieCollectionManager.isLoginUsed(message)) {
            if (movieCollectionManager.doesPasswordMatch(message)) {
                Iterator<Map.Entry<Integer,String>> iter = Server.connectedUsers.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Integer,String> entry = iter.next();
                    if(message.getUser().getLogin().equalsIgnoreCase(entry.getValue())){
                        return "Сессия этого пользователя уже занята";
                    }
                }
                Server.connectedUsers.replace(SocketThread.socketName, " ", message.getUser().getLogin());
                return "Вы успешно авторизованы";
            } else {
                return "Введен неверный пароль";
            }
        } else {
            return "Пользователь с таким логином не зарегестрирован";
        }
    }
}
