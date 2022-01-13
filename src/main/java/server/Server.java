package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // Datos de servidor
    private final int port = 8888;
    private ServerSocket servidorControl = null;
    private Socket socketClient = null;

    private boolean exit = false;

    // Singleton
    private static Server instance;

    private Server() {

    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void startServer() {
        connectionPrepare();
        controlConnection();
        closeConnection();
    }

    private void connectionPrepare() {
        try {
            servidorControl = new ServerSocket(this.port);
            System.out.println("Server -> Ready. Waiting client...");
        } catch (IOException ex) {
            System.err.println("Server -> ERROR: opening the port " + ex.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Cerramos las conexión del servidor
     */
    private void closeConnection() {
        try {
            // Cerramos el cliente y el servidorControl
            socketClient.close();
            servidorControl.close();
            System.out.println("Server -> Closing the connection");
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Server -> ERROR: Close connections " + ex.getMessage());
        }
    }

    /**
     * tratamos la conexión con el cliente
     */
    private void controlConnection() {

        while (!exit) {
            acceptConnection();
            processClient();
        }
    }

    private void processClient() {
        System.out.println("Server -> Initialize the Client Manager");
        ClientManager gc = new ClientManager(socketClient);
        gc.start();
    }


    private void acceptConnection() {
        try {
            socketClient = servidorControl.accept();

            System.out.println("Server -> Client accepted");
        } catch (IOException ex) {
            System.err.println("Servidor->ERROR: Client accept " + ex.getMessage());
        }
    }


    private void exitServer() {
        this.exit = true;
        close(0);
    }


    private void close(int status) {
        System.exit(status);
    }

}
