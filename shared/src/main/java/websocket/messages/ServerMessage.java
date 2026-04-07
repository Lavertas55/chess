package websocket.messages;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String game;
    String message;
    String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;

        if (this.serverMessageType.equals(ServerMessageType.LOAD_GAME)) {
            this.game = message;
        }
        else if (this.serverMessageType.equals(ServerMessageType.ERROR)) {
            this.errorMessage = message;
        }
        else if (this.serverMessageType.equals(ServerMessageType.NOTIFICATION)) {
            this.message = message;
        }
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return switch(this.serverMessageType) {
            case NOTIFICATION -> this.message;
            case ERROR -> this.errorMessage;
            case LOAD_GAME -> this.game;
        };
    }

    public String getGame() {
        return this.game;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
