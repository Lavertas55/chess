package client;

import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import ui.WebSocketNotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    WebSocketNotificationHandler webSocketNotificationHandler;

    public WebSocketFacade(String url, WebSocketNotificationHandler webSocketNotificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.webSocketNotificationHandler = webSocketNotificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                webSocketNotificationHandler.handleNotification(notification);
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
