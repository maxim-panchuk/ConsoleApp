package serverWorker;

import dataProcessor.Command;
import dataProcessor.Deserializer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketThread implements Runnable {

    private final Socket clientSocket;
    public static int socketName;

    public SocketThread (Socket clientSocket) {
        this.clientSocket = clientSocket;
        socketName = this.clientSocket.getPort();
        //Server.connectedUsers.putIfAbsent(socketName, " ");
    }

    @Override
    public void run () {
        System.out.println(this.clientSocket.toString());

        ExecutorService executor = Executors.newCachedThreadPool();
        //ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        executor.execute(new ProcessingRequest());


    }
    private class ProcessingRequest implements Runnable {
        public void run () {
            try {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                while (true) {
                    int len = dataInputStream.readInt();
                    byte[] data = new byte[len];
                    if (len > 0) {
                        dataInputStream.readFully(data);
                    }
                    System.out.println("Получена команда от клиента");
                    this.sendBytes(Command.execute(Deserializer.deserializer(data)));
                }
            } catch (IOException e) {
                System.out.println("Клиент " + socketName + " разорвал соединение");
                //Server.connectedUsers.entrySet().removeIf(entry -> socketName == (entry.getKey()));

            }
        }
        public void sendBytes (byte[] myByteArray) throws IOException {
            this.sendBytes(myByteArray, 0, myByteArray.length);
        }

        public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
            if (len < 0)
                throw new IllegalArgumentException("Negative length not allowed");
            if (start < 0 || start >= myByteArray.length)
                throw new IndexOutOfBoundsException("Out of bounds: " + start);
            OutputStream out = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeInt(len);
            if (len > 0) {
                dos.write(myByteArray, start, len);
            }
            //System.out.println(Arrays.toString(myByteArray));
        }
    }
}
