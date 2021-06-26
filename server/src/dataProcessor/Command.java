package dataProcessor;

import command.*;
import element.Message;

import java.nio.charset.StandardCharsets;

/**
 * Получает обхект типа Message и выполняет команду,
 * Возвращает ответ в виде строки.
 */

public class Command {
    static int id;
    static element.Movie movie;

    public static byte[] execute (Message message) {
        try {
            String commandName = message.getCommand();
            switch(commandName) {
                case "sign_in":
                    SignIn signIn = new SignIn();
                    return signIn.run(message).getBytes(StandardCharsets.UTF_8);
                case "register":
                    Register register = new Register();
                    return register.run(message).getBytes(StandardCharsets.UTF_8);
                case "show":
                    Show show = new Show();
                    return show.run().getBytes(StandardCharsets.UTF_8);
                case "add":
                    Add add = new Add ();
                    return add.run(message).getBytes(StandardCharsets.UTF_8);
                case "help":
                    Help help = new Help();
                    return help.run().getBytes(StandardCharsets.UTF_8);
                case "info":
                    Info info = new Info();
                    return info.run().getBytes(StandardCharsets.UTF_8);
                case "update":
                    id = Integer.parseInt(message.getArgument());
                    movie = message.getMovie();
                    Update update = new Update();
                    return update.run(message).getBytes(StandardCharsets.UTF_8);
                case "remove":
                    RemoveId removeId = new RemoveId();
                    return removeId.run(message).getBytes(StandardCharsets.UTF_8);
                case "clear":
                    Clear clear = new Clear();
                    return clear.run(message).getBytes(StandardCharsets.UTF_8);
                case "remove_head":
                    RemoveHead removeHead = new RemoveHead();
                    return removeHead.run(message).getBytes(StandardCharsets.UTF_8);
                case "add_if_min":
                    AddIfMin addIfMin = new AddIfMin();
                    return addIfMin.run(message).getBytes(StandardCharsets.UTF_8);
                case "remove_greater":
                    RemoveGreater removeGreater = new RemoveGreater();
                    return removeGreater.run(message).getBytes(StandardCharsets.UTF_8);
                case "filter_starts_with_name":
                    NameFilter nameFilter = new NameFilter();
                    return nameFilter.run(message).getBytes(StandardCharsets.UTF_8);
                case "print_ascending":
                    PrintAsceending printAsceending = new PrintAsceending();
                    return printAsceending.run().getBytes(StandardCharsets.UTF_8);
                case "print_field_ascending_director":
                    PrintAscenDir printAscenDir = new PrintAscenDir();
                    return printAscenDir.run().getBytes(StandardCharsets.UTF_8);
                default: {
                    return "Неверная команда Server ввод".getBytes(StandardCharsets.UTF_8);
                }
            }
        } catch (NullPointerException e) {
            return "Введите команду заново, Ошибка на сервере".getBytes(StandardCharsets.UTF_8);
        }
    }
}
