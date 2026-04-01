package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final AuthService authService;
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(AuthService authService, UserService userService, GameService gameService) {
        this.authService = authService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected...");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            try {
                UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                switch (action.getCommandType()) {
                    case CONNECT -> connect(action.getAuthToken(), action.getGameID(), ctx.session);
                    case MAKE_MOVE -> makeMove(
                            action.getAuthToken(),
                            action.getGameID(),
                            action.getMove(),
                            ctx.session
                    );
                }
            } catch (ResponseException ex) {
                ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
                ctx.session.getRemote().sendString(message.toString());
            }
        }
        catch (IOException ex) {
            System.out.printf("Failed to send message: %s%n", ex.getMessage());
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException, ResponseException {
        authService.verifySession(authToken);
        int userID = authService.getUserID(authToken);
        String username = userService.getUsername(userID);

        GameData game = gameService.getGame(gameID);

        String teamColor = "OBSERVER";
        if (game.whiteUserID().equals(userID)) {
            teamColor = "WHITE";
        } else if (game.blackUserID().equals(userID)) {
            teamColor = "BLACK";
        }

        ServerMessage notificationMessage = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s joined as %s", username, teamColor)
        );

        ServerMessage gameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game.gameString()
        );

        connectionManager.add(gameID, session);
        connectionManager.broadcast(gameID, session, notificationMessage);

        session.getRemote().sendString(gameMessage.toString());
    }

    private void makeMove(
            String authToken,
            int gameID,
            ChessMove move,
            Session session
    ) throws IOException, ResponseException {
        authService.verifySession(authToken);
        int userID = authService.getUserID(authToken);

        GameData gameData = gameService.getGame(gameID);

        ChessGame.TeamColor userColor = getTeamColor(gameData, userID);
        ChessGame.TeamColor opponentColor = userColor.equals(ChessGame.TeamColor.WHITE) ?
                ChessGame.TeamColor.BLACK :
                ChessGame.TeamColor.WHITE;

        ChessGame game = ChessGame.fromJson(gameData.gameString());
        validateTurn(userColor, game);

        String stateString = null;
        try {
            game.makeMove(move);

            if (game.isInCheckmate(opponentColor)) {
                stateString = "CHECKMATE";
                game.endGame();
            }
            else if (game.isInStalemate(opponentColor)) {
                stateString = "STALEMATE";
                game.endGame();
            }
            else if (game.isInCheck(opponentColor)) {
                stateString = "CHECK";
            }
        }
        catch (InvalidMoveException ex) {
            throw new ResponseException(ResponseException.Code.FORBIDDEN, ex.getMessage());
        }

        gameService.updateGame(gameID, game.toJson());

        String username = userService.getUsername(userID);

        ServerMessage gameResponse = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game.toJson()
        );

        ServerMessage notificationResponse = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s moved %s to %s", username, move.getStartPosition(), move.getEndPosition())
        );

        if (stateString != null) {
            String opponentUsername = switch (userColor) {
                case WHITE -> userService.getUsername(gameData.blackUserID());
                case BLACK -> userService.getUsername(gameData.whiteUserID());
            };

            ServerMessage stateResponse = new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in %s", opponentUsername, stateString)
            );

            connectionManager.broadcast(gameID, null, stateResponse);
        }

        connectionManager.broadcast(gameID, null, gameResponse);
        connectionManager.broadcast(gameID, session, notificationResponse);
    }

    private ChessGame.TeamColor getTeamColor(GameData gameData, int userID) throws ResponseException {
        if (gameData.whiteUserID().equals(userID)) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (gameData.blackUserID().equals(userID)) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            throw new ResponseException(
                    ResponseException.Code.FORBIDDEN,
                    String.format("You are not a player in game %d", gameData.gameID())
            );
        }
    }

    private void validateTurn(ChessGame.TeamColor userTeam, ChessGame game) throws ResponseException {
        if (game.isGameDone()) {
            throw new ResponseException(
                    ResponseException.Code.FORBIDDEN,
                    "Game is over"
            );
        }

        if (!userTeam.equals(game.getTeamTurn())) {
            throw new ResponseException(
                    ResponseException.Code.FORBIDDEN,
                    "It is not your turn"
            );
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connectionManager.remove(ctx.session);
        System.out.println("Websocket closed...");
    }
}
