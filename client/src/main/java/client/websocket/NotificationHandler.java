package client.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void handleNotification(ServerMessage notification);
}
