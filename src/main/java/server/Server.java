package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        int numClientes = 0; // Contador de clientes
        ServerSocket servidor;
        Socket cliente;
        int puerto = 5555;
        boolean salir = false;

        System.out.println("Servidor arrancado y escuchando...");
        try {
            servidor = new ServerSocket(puerto);
            while (!salir) {
                System.out.println("Esperando conexiones...");
                cliente = servidor.accept();//equivalente al reicve
                numClientes ++;
                // Pasamos el control al hilo correspondiente
                System.out.println("Peticion -> " + cliente.getInetAddress() + " --- " + cliente.getPort());
                ClientManager gc = new ClientManager(cliente, numClientes);
                gc.start();
            }
            System.out.println("Servidor finalizado...");
            servidor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
