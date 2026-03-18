package client;

public class ChessClient {
    private final ServerFacade serverFacade;

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess ♕");
        // TODO: print help menu
    }
}
