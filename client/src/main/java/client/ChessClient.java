package client;

import chess.ChessGame;
import exception.ResponseException;
import ui.*;

import java.util.HashMap;

public class ChessClient implements Client, UIEngine {
    private final ServerFacade serverFacade;

    private State state = State.SIGNED_OUT;
    private String authToken = null;
    private ChessGame.TeamColor teamColor;
    private HashMap<Integer, Integer> games;

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
    }

    @Override
    public void run() throws ResponseException {
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

    private Displayable getMenuFromState() throws ResponseException {
        return switch (state) {
            case SIGNED_OUT -> new LoginMenu(this, serverFacade);
            case SIGNED_IN -> new UserMenu(this, serverFacade, authToken, games);
            case IN_GAME -> new PlayerMenu(this, serverFacade, authToken, new ChessGame(), teamColor);
            case OBSERVING -> new ObservingMenu(this, serverFacade, authToken, new ChessGame());
            case QUIT -> new QuitMenu();
        };
    }
}
