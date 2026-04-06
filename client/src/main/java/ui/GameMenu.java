package ui;

import chess.ChessGame;
import client.*;
import client.websocket.WebSocketFacade;

import java.util.Optional;

public abstract class GameMenu extends UIMenu {
    final String authToken;
    final ChessGame game;
    final ChessGame.TeamColor teamColor;
    final WebSocketFacade webSocketFacade;
    final BoardDrawer boardDrawer = new BoardDrawer();

    public GameMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            ChessGame game,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade);
        this.webSocketFacade = webSocketFacade;
        this.authToken = authToken;
        this.game = game;
        this.teamColor = teamColor;
    }

    @Override
    Optional<State> run() {
        help();
        return super.run();
    }

    void draw() {
        boardDrawer.drawBoard(System.out, game.getBoard(), teamColor);
    }

    State exit() {
        notify("Exiting...\n");

        return State.SIGNED_IN;
    }
}
