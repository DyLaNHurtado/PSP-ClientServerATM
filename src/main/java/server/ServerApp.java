package server;

public class ServerApp {
    public static void main(String[] args){
        Server server = Server.getInstance();
        server.startServer();
    }
}
