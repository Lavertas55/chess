package ui;

import chess.ChessGame;
import client.*;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import java.util.Optional;

public abstract class GameMenu extends UIMenu {
    final String authToken;
    final int gameID;
    final ChessGame.TeamColor teamColor;
    final WebSocketFacade webSocketFacade;
    final BoardDrawer boardDrawer = new BoardDrawer();

    public GameMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade);
        this.webSocketFacade = webSocketFacade;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;

        try {
            webSocketFacade.joinGame(authToken, gameID);
            engine.setWaiting(true);
        }
        catch (ResponseException ex) {
            alert(ex.getMessage());
        }
    }

    @Override
    public Optional<State> run() {
        if (engine.isWaiting()) {
            return Optional.empty();
        }

        return super.run();
    }

    State draw() {
        boardDrawer.drawBoard(System.out, engine.getGame().getBoard(), teamColor);
        return null;
    }

    State exit() throws ResponseException {
        notify("Exiting...\n");

        webSocketFacade.leaveGame(authToken, gameID);

        return State.SIGNED_IN;
    }
}
