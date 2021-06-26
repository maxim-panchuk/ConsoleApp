import element.Movie;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ClientConsole implements Runnable {
    Scanner scanner = new Scanner(System.in);
    public static Movie movie;
    public void run() {
        while (true) {
            try {
                String line = scanner.nextLine();
                String[] command = line.trim().split(" ");
                if (line.equals("exit")) {
                    System.out.println("Клиент завершил работу");
                    System.exit(0);
                } else if (line.equals("add")) {
                    movie = new Movie(scanner, 0);
                } else if (command[0].equals("update")) {
                    movie = new Movie(scanner, 0);
                } else if (command[0].equals("register")) {
                    System.out.println("Придумайте пароль: ");
                    String psw = this.returnPassword();
                    System.out.println("Повторите пароль: ");
                    String psw1 = this.returnPassword();
                    line += " " + psw + " " + psw1;
                } else if (command[0].equals("sign_in")) {
                    System.out.println("Введите пароль: ");
                    String psw = this.returnPassword();
                    line += " " + psw;
                } else if (command[0].equals("add_if_min")) {
                    try {
                        movie = new Movie(scanner, 0);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                    }
                }
                /*else if (command[0].equals("execute_script")) {
                    try {
                        String filepath = command[1];
                        if (Client.validFile(filepath).equals("Указанный файл прошел проверку на валидность, " +
                                "можно приступать к работе")) {
                            File file = new File(filepath);
                            BufferedReader bufferedReader = new BufferedReader(
                                    new FileReader(file));
                            String redLine;
                            while ((redLine = bufferedReader.readLine()) != null) {
                                try {
                                    Client.queue.put(redLine);
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (ArrayIndexOutOfBoundsException | FileNotFoundException ignored) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                try {
                    synchronized (Client.queue) {
                        Client.queue.put(line);
                        Client.queue.notifyAll();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchElementException
                    m) {
                System.out.println("Введите строку");
                System.exit(0);
            }
        }
    }

    private String returnPassword () {
        Console console = System.console();
        char[] chars = console.readPassword();
        return new String(chars);
    }
}
