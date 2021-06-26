package serverWorker;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private String strSshUser = "s311737";
    private String strSshPassword = "zev617";
    private String strSshHost = "se.ifmo.ru";
    private int nSshPort = 2222;
    private String strRemoteHost = "pg";


    private int nRemotePort = 5432;
    private int nLocalPort = 5430;
    private String strDbUser = "s311737";
    private String strDbPassword = "zev617";



    private static Connection con;

    public void connect() {
        this.doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort, strRemoteHost, nLocalPort, nRemotePort);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.con = DriverManager.getConnection("jdbc:postgresql://localhost:"+nLocalPort+"/studs", strDbUser, strDbPassword);
        } catch (SQLException throwables) {
            System.out.println("Error in SSH tunnel)");
            throwables.printStackTrace();
        }
    }

    private void doSshTunnel( String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort )
    {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(strSshUser, strSshHost, nSshPort);
            session.setPassword(strSshPassword);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
        }catch (JSchException e){e.printStackTrace();}
    }

    public Connection getCon(){
        return this.con;
    }
}
