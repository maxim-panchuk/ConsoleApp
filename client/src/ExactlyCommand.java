import element.Message;
import element.Movie;
import element.User;

public class ExactlyCommand {
    private static Movie movie;
    public static Message defineCommand (String line) {
        if (!Client.isSignedIn) {
            String[] command = line.trim().split(" ");
            Message message;
            switch (command[0]) {
                case "sign_in":
                    try {
                        String login = command[1];
                        String password = command[2];
                        Client.user = new User(command[1], command[2]);
                        message = new Message("sign_in", null, null, Client.user);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректный ввод");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                    break;
                case "register":
                    try {
                        String login = command[1];
                        String password1 = command[2];
                        String password2 = command[3];
                        String stringBuilder = command[1] + " " +
                                command[2] + " " +
                                command[3];
                        message = new Message("register", stringBuilder, null, null);
                        return message;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректный ввод");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                default:
                    System.out.println("Не авторизованным пользователям не доступна работа с БД, либо вы ввели неправильную команду");
                    message = new Message(null, null, null, null);
                    break;
            }
            return  message;
        }
        else {
            String[] command = line.trim().split(" ");
            Message message;
            switch (command[0]) {
                case "help":
                    message = new Message("help", null, null, null);
                    return message;
                case "info":
                    message = new Message("info", null, null, null);
                    return message;
                case "show":
                    message = new Message("show", null, null, null);
                    return message;
                case "clear":
                    message = new Message("clear", null, null, Client.user);
                    return message;
                case "remove_head":
                    message = new Message("remove_head", null, null, Client.user);
                    return message;
                case "print_ascending":
                    message = new Message("print_ascending", null, null, null);
                    return message;
                case "print_field_ascending_director":
                    message = new Message("print_field_ascending_director",
                            null, null, null);
                    return message;
                case "add":
                    movie = ClientConsole.movie;
                    message = new Message("add", null, movie, Client.user);
                    return message;
                case "add_if_min":
                    try {
                        message = new Message("add_if_min", null, ClientConsole.movie, Client.user);
                        return message;
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введено значение,");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                case "filter_starts_with_name":

                    try {
                        String regex = command[1];
                        message = new Message("filter_starts_with_name", command[1], null, null);
                        return message;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введено значение,");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                case "update":
                    try {
                        int num = Integer.parseInt(command[1]);
                        message = new Message("update", command[1], ClientConsole.movie, Client.user);
                        return message;
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        System.out.println("Некорректно введено значение,");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                case "remove":
                    try {
                        int num = Integer.parseInt(command[1]);
                        message = new Message("remove", command[1], null, Client.user);
                        return message;
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введено значение,");
                        message = new Message(null, null, null, null);
                        return message;
                    }
                case "remove_greater":
                    try {
                        int num = Integer.parseInt(command[1]);
                        message = new Message("remove_greater", command[1], null, Client.user);
                        return message;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(
                                "Некорректно введено значение,"
                        );
                        message = new Message(null, null, null, null);
                        return message;
                    }
                default:
                    message = new Message(null, null, null, null);
                    return message;
            }
        }
    }
}
