package server;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientManager extends Thread{
    private int numClient;
    private Socket client;

    public ClientManager(Socket socket, int client){
        this.client = socket;
        this.numClient = client;

    }

    @Override
    public void run(){

        ObjectInputStream bufferObjetosEntrada = null;
        try {
            System.out.println("Hilo de atención al Cliente: "+numClient+" de " + client.getInetAddress());
            // setSoLinger() a true hace que el cierre del socket espere a que
            // el cliente lea los datos, hasta un máximo de 10 segundos de espera.
            // Si no ponemos esto, el socket se cierra inmediatamente y si el
            // cliente no ha tenido tiempo de leerlos, los datos se pierden.
            //cliente.setSoLinger (true, 10);
            // Recibimos del cliente, y como nos entra datos es por el input
            bufferObjetosEntrada = new ObjectInputStream(client.getInputStream());
            Ejemplo datoEntrada = (Ejemplo) bufferObjetosEntrada.readObject();
            datoEntrada.mostrar();
            System.out.println("Recibido del Cliente '" + datoEntrada.toString() + "'");
            // Se prepara un flujo de salida para objetos y un objeto para enviar al output del cliente
            Ejemplo datoSalida = new Ejemplo();
            ObjectOutputStream bufferObjetosSalida = new ObjectOutputStream(cliente.getOutputStream());
            // Se envia el objeto
            bufferObjetosSalida.writeObject(datoSalida);
            System.out.println("Enviado al Cliente '" + datoSalida.toString() + "'");
        } catch (IOException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferObjetosEntrada.close();
            } catch (IOException ex) {
                Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
