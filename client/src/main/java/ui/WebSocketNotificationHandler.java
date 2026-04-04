package ui;

import websocket.messages.ServerMessage;

public interface WebSocketNotificationHandler {
    void handleNotification(ServerMessage notification);
}
