package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import client.websocket.WebSocketNotificationHandler;
import exception.ResponseException;
import ui.*;

import java.util.HashMap;

public class ChessClient implements Client, UIEngine {
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;
    private final NotificationHandler notificationHandler;

    private State state = State.SIGNED_OUT;
    private String authToken = null;
    private Integer gameID = null;
    private ChessGame game = null;
    private ChessGame.TeamColor teamColor;
    private HashMap<Integer, Integer> games;

    public ChessClient(String serverURL) throws ResponseException {
        serverFacade = new ServerFacade(serverURL);
        notificationHandler = new WebSocketNotificationHandler(this);
        webSocketFacade = new WebSocketFacade(serverURL, notificationHandler);
    }

    @Override
    public void run() {
        System.out.println("♕ Welcome to Chess ♕");

        Displayable menu = getMenuFromState();
        while (!state.equals(State.QUIT)) {
            menu
                .display()
                .ifPresent(value -> state = value);

            menu = getMenuFromState();
        }
    }

    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public void setGames(HashMap<Integer, Integer> games) {
        this.games = games;
    }

    @Override
    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    private Displayable getMenuFromState() {
        return switch (state) {
            case SIGNED_OUT -> new LoginMenu(this, serverFacade);
            case SIGNED_IN -> new UserMenu(this, serverFacade, authToken, games);
            case IN_GAME -> new PlayerMenu(this, serverFacade, webSocketFacade, authToken, game, teamColor);
            case OBSERVING -> new ObservingMenu(this, serverFacade, webSocketFacade, authToken, game);
            case QUIT -> new QuitMenu();
        };
    }
}
