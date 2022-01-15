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
    private final int PORT = 8888;
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
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Client -> ERROR: Could not logIn");
        }

    }

    /**
     * Check the address to the server
     */
    private void checkAddress() {
        try {
            // Get Address
            address = InetAddress.getLocalHost(); // local address (localhost)
        } catch (java.net.UnknownHostException ex) {
            System.err.println("Client -> ERROR: Not found the server address " + ex.getMessage());
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
            socket = new Socket(address, PORT);
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
     * Disconnect the server(only the socket and this client, the server is open)
     */
    private void disconnectServer() {
        try {
            // Disconnect
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
    private void mainMenu() throws IOException, ClassNotFoundException {
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
            System.out.println("What do you do ? (1 - 5) \n>");
            option = sc.nextInt();
        } while (option < 1 || option > 5);

        selectOptionMenu(option);

    }


    private void selectOptionMenu(int option) throws IOException, ClassNotFoundException {
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

    private void withdrawCash() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        dataOutputStream.writeInt(WITHDRAW_CASH);
        System.out.println("How much cash do you want to withdraw ? : \n>");
        if (userDTO.getCash() == 0) {
            System.out.println("Bad news... you have run out of cash...");
            System.out.println("Backing to menu...");
            mainMenu();
        }
        int value = sc.nextInt();
        dataOutputStream.writeInt(value);
        if (userDTO.getCash() < value) {
            System.out.println("You can't withdraw that amount because you don't have it");
            System.out.println("Backing to menu...");
            mainMenu();
        }
        if(dataInputStream.readBoolean()){
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            userDTO = (UserDTO) objectInputStream.readObject();
            System.out.println("Operation Complete! Your cash now : "+userDTO.getCash() +" €"+
                    "\nBacking to menu...");
        }else{
            mainMenu();
        }
        mainMenu();
    }

    private void depositCash() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        dataOutputStream.writeInt(DEPOSIT_CASH);
        System.out.println("How much you want to deposit ? (0-1000): \n>");
        int value = sc.nextInt();
        if (value <=0 || value > 1000) {
            System.out.println("You can not deposit that amount make sure it is between 0 and 1000");
            System.out.println("Backing to menu...");
            mainMenu();
        }
        dataOutputStream.writeInt(value);
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        userDTO = (UserDTO) objectInputStream.readObject();
        System.out.println("Operation Complete! Your cash now : "+userDTO.getCash() +" €"+
                "\nBacking to menu...");
        mainMenu();

    }

    private void consultCash() throws IOException, ClassNotFoundException {
        dataOutputStream.writeInt(CONSULT_CASH);
        System.out.println("You have " + userDTO.getCash() + " €");
        System.out.println("Backing to menu...");
        mainMenu();
    }

    private void consultMovements() throws IOException, ClassNotFoundException {
        dataOutputStream.writeInt(CONSULT_MOVEMENTS);

        System.out.println("Your latest movements : ");
        String movements = dataInputStream.readUTF();
        if(!movements.equals("error")){
            System.out.println(movements);
            System.out.println("Backing to menu...");
            mainMenu();
        }
        System.out.println("ERROR: Could not consult the movements, try again!");
        System.out.println("Backing to menu...");
        mainMenu();
    }


    private void exit() {
        this.exit = true;
        if (TOKEN > 0) {
            System.out.println("Client -> Requesting exit");
            try {
                dataOutputStream.writeInt(EXIT);
                close(0);
            } catch (IOException ex) {
                System.err.println("Client -> ERROR: Sending exit request " + ex.getMessage());
                close(1);
            }
            this.disconnectServer();
        }
    }


    private void close(int option) {
        if (isConnected) {
            this.disconnectServer();
        }
        System.out.println("Client: *** END ***");
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
        String pin = Utils.getInstance().toSHA512(sc.nextLine()); // Encrypting

        this.connectServer();
        // If connect go to identify
        if (this.isConnected) {
            System.out.println("Client -> Requesting identify...");
            try {

                // Put the option
                dataOutputStream.writeInt(LOGIN);

                // Send to server the email and PIN
                dataOutputStream.writeUTF(email);
                dataOutputStream.writeUTF(pin);

                // receive the response
                boolean isCorrect = dataInputStream.readBoolean();
                if (isCorrect) {
                    // receive the connection token
                    TOKEN = dataInputStream.readLong();
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    userDTO = (UserDTO) objectInputStream.readObject();
                    mainMenu();
                } else {
                    System.out.println("Client: Could not be identified");
                    this.logIn();
                }
            } catch (IOException ex) {
                System.err.println("Client->ERROR: Cannot identify " + ex.getMessage());
            }
        } else {
            System.out.println("Client: Could not be identified");
            this.logIn();
        }

    }
}
