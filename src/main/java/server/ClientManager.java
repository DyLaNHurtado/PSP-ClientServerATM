package server;

import controller.MovementController;
import controller.UserController;
import dto.MovementDTO;
import dto.UserDTO;
import org.bson.types.ObjectId;
import service.AccessService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;

public class ClientManager extends Thread {

    private Socket socketClient;
    private boolean exit = false;
    private long TOKEN = -99;

    // Options
    private static final int LOGIN = 0;
    private static final int WITHDRAW_CASH = 1;
    private static final int DEPOSIT_CASH = 2;
    private static final int CONSULT_CASH = 3;
    private static final int CONSULT_MOVEMENTS = 4;
    private static final int EXIT = 5;

    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    public ClientManager(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public void run() {
        if (!exit) {
            createBuffers();
            controlConnection();
            closeBuffers();
        } else {
            this.interrupt();
        }
    }

    private void exit() {
        closeBuffers();
        System.out.println("ServerCM -> Exiting");
        exit = true;
        this.interrupt();
    }


    private void createBuffers() {
        try {
            dataInputStream = new DataInputStream(socketClient.getInputStream());
            dataOutputStream = new DataOutputStream(socketClient.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: Create input and output buffers " + ex.getMessage());
            exit = true;
        }
    }

    /**
     * Cerramos lso flujos de intercambio
     */
    private void closeBuffers() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: Close input and output buffers " + ex.getMessage());
        }
    }

    private void controlConnection() {
        // Escuchamos hasta aburrirnos, es decir, hasta que salgamos
        try {
            // Procesamos la información
            // Por defecto leo mensajes cortos
            int opcion = dataInputStream.readInt();
            // según la pción
            switch (opcion) {
                case LOGIN:
                    logIn();
                    break;
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
                    disconnect();
                    break;
            }

        } catch (IOException ex) {
            System.err.println("ServidorGC -> ERROR: optener tipo de opcion " + ex.getMessage());
        }
    }


    private void disconnect() {
        this.exit = true;
        System.out.println("ServidorGC -> Recibido Salir");
    }

    /**
     * Indetificamos al usuario
     */
    private void logIn() {
        try {
            System.out.println("ServerCM -> Processing log In");
            String email = dataInputStream.readUTF();
            String pin = dataInputStream.readUTF();

            AccessService a = AccessService.getInstance();
            if (a.indetificarUsuario(email, pin)) {
                dataOutputStream.writeBoolean(true);
                TOKEN = Instant.now().getEpochSecond();
                dataOutputStream.writeLong(TOKEN);
            } else {
                System.out.println("ServerCM -> Incorrect Email or PIN");
                dataOutputStream.writeBoolean(false);
            }
            controlConnection();

        } catch (IOException ex) {
            System.err.println("ServerCM -> ERROR: process identify client " + ex.getMessage());
        }
    }


    private void withdrawCash() throws IOException {
        System.out.println("ServerCM -> Process the withdraw operation...");
        int value = dataInputStream.readInt();
        UserController userController = UserController.getInstance();
        MovementController movementController = MovementController.getInstance();


        String email = dataInputStream.readUTF();
        UserDTO userDTO = userController.getUserByEmail(email);
        if (userDTO != null && userDTO.getCash()>0) {
            userDTO.setCash(userDTO.getCash() - value);
            userController.updateUser(userDTO);
            dataOutputStream.writeBoolean(true);
            movementController.postMovement(new MovementDTO(new ObjectId(), Instant.now(), "WITHDRAW_CASH", value, userDTO));
        } else {
            dataOutputStream.writeBoolean(false);
            System.out.println("ServerCM: -> Not found user or the cash is 0");
        }
        controlConnection();
    }

    /**
     * Obtenemos el número de suspensos
     */
    private void depositCash() throws IOException {
        System.out.println("ServerCM -> Process the deposit operation...");

        MovementController movementController = MovementController.getInstance();
        UserController userController = UserController.getInstance();
        String email = dataInputStream.readUTF();
        UserDTO userDTO = userController.getUserByEmail(email);
        int value = dataInputStream.readInt();
        if (value < 1000 && value > 0) {

            if (userDTO != null) {
                userDTO.setCash(userDTO.getCash() + value);
                userController.updateUser(userDTO);
                dataOutputStream.writeBoolean(true);
                movementController.postMovement(new MovementDTO(new ObjectId(), Instant.now(), "DEPOSIT_CASH", value, userDTO));
            } else {
                dataOutputStream.writeBoolean(false);
                System.out.println("ServerCM: -> Not found user");

            }
        } else {
            dataOutputStream.writeBoolean(false);
            System.out.println("ServerCM-> Incorrect value for deposit the range is 0-1000");
        }
        controlConnection();
    }


    private void consultCash() throws IOException {
        System.out.println("ServidorGC->Process consult cash");

        UserController userController = UserController.getInstance();
        String email = dataInputStream.readUTF();
        UserDTO userDTO = userController.getUserByEmail(email);

        if (userDTO != null) {
            float cash = userDTO.getCash();
            try {
                dataOutputStream.writeFloat(cash);
                MovementController movementController = MovementController.getInstance();
                movementController.postMovement(new MovementDTO(new ObjectId(),Instant.now(),"CONSULT_CASH",0,userDTO));
            } catch (IOException ex) {
                System.err.println("ServidorGC->ERROR: consultCash " + ex.getMessage());
            }
        } else {
            try {
                dataOutputStream.writeFloat(-1);
            } catch (IOException ex) {
                System.err.println("ServidorGC->ERROR: consultCash could find user" + ex.getMessage());
            }
        }
        controlConnection();
    }


    private void consultMovements() {
        System.out.println("ServidorGC->Processing consult Movements");
        try {
            UserController userController = UserController.getInstance();
            String email = dataInputStream.readUTF();
            UserDTO userDTO = userController.getUserByEmail(email);

            MovementController movementController = MovementController.getInstance();
            String movements = movementController.getAllMovementsJSON();
            dataOutputStream.writeUTF(movements);
            if(!(movements.equals("error")) && userDTO!=null){
                movementController.postMovement(new MovementDTO(new ObjectId(), Instant.now(), "CONSULT_MOVEMENTS", 0, userDTO));
            }

        } catch (IOException ex) {
            System.err.println("ServidorGC->ERROR: Could not process consult movements " + ex.getMessage());
        }
        controlConnection();
    }
}
