package serverWorker;

import com.jcraft.jsch.IO;
import database.MovieCollectionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static MovieCollectionManager movieCollectionManager;
    public static Connection connection;
    public static int PORT;
    //public static Map<Integer, String> connectedUsers = new HashMap<>();

    public Server () throws SQLException {
        PORT = 1777;
        DatabaseConnector databaseConnector = new DatabaseConnector();
        databaseConnector.connect();
        connection = databaseConnector.getCon();
        movieCollectionManager = new MovieCollectionManager();
    }

    public static void main(String[] args) throws SQLException {
        Server server = new Server();

        server.run();

    }
    private void run() {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Ожидание подключения на порту " + PORT);
                Socket clientSocket = serverSocket.accept();
                Runnable socketThread = new SocketThread(clientSocket);
                executor.execute(socketThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
