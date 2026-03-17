import client.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");

        String serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }

        try {
            new ChessClient(serverURL).run();
        }
        catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
