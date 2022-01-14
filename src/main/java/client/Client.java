package client;

import dto.UserDTO;
import utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final int port = 8888;
    private InetAddress address;
    private Socket socket;
    private boolean exit = false;
    private boolean isConnected = false;
    private long TOKEN = -99;
    private UserDTO userDTO;

    // Options
    private static final int LOGIN = 0;
    private static final int WITHDRAW_CASH = 1;
    private static final int DEPOSIT_CASH = 2;
    private static final int CONSULT_CASH = 3;
    private static final int CONSULT_MOVEMENTS = 4;
    private static final int EXIT = 5;

    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    public void startClient() {
        try {
            checkAddress();
            logIn();
            mainMenu();
        } catch (ClassNotFoundException e) {
            System.err.println("Client -> ERROR: Could not logIn");
        }

    }

    /**
     * Check the address to the server
     */
    private void checkAddress() {
        try {
            // Consigo la dirección
            address = InetAddress.getLocalHost(); // dirección local (localhost)
        } catch (java.net.UnknownHostException ex) {
            System.err.println("Cliente->ERROR: Didnt find the server address " + ex.getMessage());
            isConnected = false;
            exit = true;
            System.exit(-1);
        }
    }

    /**
     * Connect to Main Menu
     */
    private void connectServer() {
        try {
            socket = new Socket(address, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Client -> Connecting to server...");
            this.isConnected = true;
        } catch (IOException ex) {
            System.err.println("Client -> ERROR: Could not connect to server " + ex.getMessage());
            isConnected = false;
            exit = true;
            System.exit(-1);
        }
    }

    /**
     * Disconect the server(only the socket and this client, the server is open)
     */
    private void disconnectServer() {
        try {
            // Me desconecto
            socket.close();
            dataInputStream.close();
            dataOutputStream.close();
            System.out.println("Client -> Disconnected");
            isConnected = false;
            exit = true;
        } catch (IOException ex) {
            System.err.println("Client -> ERROR: When disconnecting the server " + ex.getMessage());
            isConnected = false;
            exit = true;
            System.exit(-1);
        }
    }

    /**
     * Main Menu
     */
    private void mainMenu() {
        System.out.println("Welcome to your bank !");
        System.out.println("-----------------------------");
        System.out.println("1.- Withdraw cash");
        System.out.println("2.- Deposit cash");
        System.out.println("3.- Consult cash");
        System.out.println("4.- Consult movements");
        System.out.println("5.- Exit");

        int option;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("What do you do ? (1 - 5) ");
            option = sc.nextInt();
        } while (option < 1 || option > 5);

        selectOptionMenu(option);

    }


    private void selectOptionMenu(int option) {
        switch (option) {
            case WITHDRAW_CASH:
                withdrawCash();
                break;
            case DEPOSIT_CASH:
                depositCash();
                break;

            case CONSULT_CASH:
                consultCash();
                break;

            case CONSULT_MOVEMENTS:

                consultMovements();
                break;

            case EXIT:
                exit();
                break;

        }
    }

    private void withdrawCash() {
        Scanner sc = new Scanner(System.in);

        System.out.println("How much cash do you want to withdraw ? : ");
        if (userDTO.getCash() == 0) {
            System.out.println("Bad news... you have run out of cash...");
            System.out.println("Backing to menu...");
            mainMenu();
        }
        int value = sc.nextInt();
        if (userDTO.getCash() < value) {
            System.out.println("You can't withdraw that amount because you don't have it");
            System.out.println("Backing to menu...");
            mainMenu();
        } else {
            userDTO.setCash(userDTO.getCash() - value);
            //TODO Mandar al servidor el nuevo usuario
        }


    }

    private void depositCash() {
        Scanner sc = new Scanner(System.in);
        System.out.println("How much you want to deposit ? (0-1000)");
        int value = sc.nextInt();
        if (value < 0 || value > 1000) {
            System.out.println("You can not deposit that amount make sure it is between 0 and 1000");
            System.out.println("Backing to menu...");
            mainMenu();
        }
        userDTO.setCash(userDTO.getCash() + value);
        //TODO Mandar al servidor el nuevo usuario

    }

    private void consultCash() {
        //TODO Mandar al servidor que quieres hacer a traves de un movement
        System.out.println("You have " + userDTO.getCash() + " €");
    }

    private void consultMovements() {
        System.out.println("Your lastest movements " + userDTO.getCash() + " €");
    }


    private void exit() {
        this.exit = true;
        if (TOKEN > 0) {
            // Send to the server to disconnect
            System.out.println("Cliente -> Requesting exit");
            // Connect to the server
            this.connectServer();
            try {
                dataOutputStream.writeInt(EXIT);
                close(0);
            } catch (IOException ex) {
                System.err.println("Client -> ERROR: Sending exit request " + ex.getMessage());
                close(1);
            }
        }
    }


    private void close(int option) {
        if (isConnected) {
            this.disconnectServer();
        }
        System.out.println("Cliente: *** END ***");
        System.exit(option);
    }


    private void logIn() throws ClassNotFoundException {

        // "email":"admin@admin.org","pin":1234
        // "email":"joseluisgs@users.org","pin":2222
        // "email":"dylanhurtado@users.org","pin":5432

        Scanner sc = new Scanner(System.in);
        System.out.println("Input your email:");
        String email = sc.nextLine();
        if (!Utils.getInstance().validateEmail(String.valueOf(email))) {
            System.out.println("Incorrect email pattern, try again");
            logIn();
        }
        System.out.println("Input your PIN:");
        String pin = Utils.getInstance().toSHA512(sc.nextLine()); // Encripting

        this.connectServer();
        // If connect go to identify
        if (this.isConnected) {
            System.out.println("Cliente -> Requesting identify...");
            try {

                // Indico la opción
                dataOutputStream.writeInt(LOGIN);

                // Send to server the email and PIN
                dataOutputStream.writeUTF(email);
                dataOutputStream.writeUTF(pin);

                // Recibimos la respuesta
                boolean isCorrect = dataInputStream.readBoolean();
                if (isCorrect) {
                    // Recibimos el token de conexion
                    TOKEN = dataInputStream.readLong();
                    mainMenu();
                } else {
                    System.out.println("Client: Could not be identified");
                    //cerramos la conexion
                    this.logIn();
                }
            } catch (IOException ex) {
                System.err.println("Client->ERROR: Cannot identify " + ex.getMessage());
            }
        } else {
            System.out.println("Client: Could not be identified");
            //cerramos la conexion
            this.logIn();
        }
        this.disconnectServer();

    }
}
