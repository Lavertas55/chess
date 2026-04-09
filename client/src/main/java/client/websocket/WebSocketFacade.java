package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    final Session session;
    final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.handleNotification(notification);
            }
        });
    }

    public void joinGame(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);

        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        UserGameCommand command = new UserGameCommand(authToken, gameID, move);

        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);

        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }
}
