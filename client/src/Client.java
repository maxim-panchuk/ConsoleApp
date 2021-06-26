import element.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client {

    public static User user;
    static final int PORT = 1777;
    static final String ADDRESS = "127.0.0.1";
    private final ByteBuffer buffer = ByteBuffer.allocate(8192);
    public static final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
    public static DataInputStream dataInputStream;
    public static boolean isSignedIn = false;
    public Socket socket;




    public void run () throws IOException, InterruptedException {
        System.out.println("Клиент начал работу");
        System.out.println("Введите \"register\" [login] - если вы не зарегестрированы. \n" +
                "Введите \"sign_in\" [login] - если зарегестрированы");
        socket = new Socket(ADDRESS, PORT);
        dataInputStream = new DataInputStream(socket.getInputStream());


        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    queue.wait();
                }
                String line = queue.remove();
                byte[] messageBytes = Serializer
                        .serializeMessage(ExactlyCommand.defineCommand(line));
                this.sendBytes(messageBytes);
            }


            String serverAnswer = this.readBytes();

            if (serverAnswer.equals("Вы успешно авторизованы")) isSignedIn = true;
            System.out.println(serverAnswer);
        }
    }

    public static void main(String[] args) throws  Exception {
        (new Thread(new ClientConsole())).start();
        Client client = new Client();
        client.run();




//        printWriter.println(str);
//
//        while ((str = bufferedReader.readLine()) != null) {
//            if (str.equals("bye")) {
//                break;
//            }
//            System.out.println(str);
//            printWriter.println("bye");
//        }
//
//        bufferedReader.close();
//        printWriter.close();
//        socket.close();
    }

    public String readBytes () {
        try {
            String serverAnswer = "pfff";
            int len = dataInputStream.readInt();
            byte[] data = new byte[len];
            if (len > 0) {

                dataInputStream.readFully(data);
                serverAnswer = new String(data, StandardCharsets.UTF_8);
            }
            return serverAnswer;
        } catch (IOException e) {
            //System.out.println("Сервер прекратил работу, клиент был отключен");
        }
        return "Сервер прекратил работу, клиент был отключен";
    }


    public void sendBytes(byte[] myByteArray) throws IOException {
        this.sendBytes(myByteArray, 0, myByteArray.length);
    }

    public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        try {
            dos.writeInt(len);


            if (len > 0) {
                dos.write(myByteArray, start, len);
            }
        } catch (SocketException e) {
            ///System.out.println("Сервер прекртаил работу, клиент был отключен");
        }
    }
}
