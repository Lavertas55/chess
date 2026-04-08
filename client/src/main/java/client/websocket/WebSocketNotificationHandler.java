package client.websocket;

import chess.ChessGame;
import client.UIEngine;
import ui.BoardDrawer;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class WebSocketNotificationHandler implements NotificationHandler {
    private final UIEngine engine;
    private final BoardDrawer boardDrawer = new BoardDrawer();

    public WebSocketNotificationHandler(UIEngine engine) {
        this.engine = engine;
    }

    @Override
    public void handleNotification(ServerMessage notification) {
        switch (notification.getServerMessageType()) {
            case NOTIFICATION -> notifyUser(notification.getMessage());
            case ERROR -> notifyError(notification.getMessage());
            case LOAD_GAME -> loadGame(notification.getMessage());
        }
    }

    private void notifyUser(String msg) {
        System.out.println("\n" + SET_TEXT_COLOR_BLUE + msg + SET_TEXT_COLOR_GREEN);
        engine.setWaiting(false);
    }

    private void notifyError(String msg) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + msg + SET_TEXT_COLOR_GREEN);
        engine.setWaiting(false);
    }

    private void loadGame(String msg) {
        ChessGame game = ChessGame.fromJson(msg);
        engine.setGame(game);

        ChessGame.TeamColor teamColor = engine.getTeamColor();
        if (teamColor == null) {
            teamColor = ChessGame.TeamColor.WHITE;
        }

        boardDrawer.drawBoard(System.out, game.getBoard(), teamColor);
        engine.setWaiting(false);
    }
}
