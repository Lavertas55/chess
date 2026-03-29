package websocket;

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
    public void handleMessage(WsMessageContext ctx) throws ResponseException {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action.getAuthToken(), action.getGameID(), ctx.session);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException, ResponseException {
        // Use authToken to get userID (Through authService)
        // Use userID to get username (through userService) and with gameID get teamColor (Through gameService)

        // If userID not in valid sessions -> Unauthorized
        // Using gameID lookup connections
        // Send message to each user that <USERNAME> joined as <TEAM COLOR> (Except joining user)

        authService.verifySession(authToken);
        int userID = authService.getUserID(authToken);
        String username = userService.getUsername(userID);

        GameData game = gameService.getGame(gameID);

        String teamColor = "OBSERVER";
        if (game.whiteUserID().equals(userID)) {
            teamColor = "WHITE";
        }
        else if (game.blackUserID().equals(userID)) {
            teamColor = "BLACK";
        }

        ServerMessage notificationMessage = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                null,
                String.format("%s joined as %s", username, teamColor)
        );

        ServerMessage gameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game.gameString(),
                null
        );

        connectionManager.add(gameID, session);
        connectionManager.broadcast(gameID, session, notificationMessage);
        session.getRemote().sendString(gameMessage.toString());
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connectionManager.remove(ctx.session);
        System.out.println("Websocket closed...");
    }
}
