package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArrayList<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(Session session) {
        for (int gameID : connections.keySet()) {
            ArrayList<Session> sessionList = connections.get(gameID);

            if (sessionList.contains(session)) {
                sessionList.remove(session);

                if (sessionList.isEmpty()) {
                    connections.remove(gameID);
                }
            }
        }
    }

    public boolean contains(int gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            return false;
        }

        ArrayList<Session> sessions = connections.get(gameID);
        return sessions.contains(session);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        String msg = message.toString();

        var sessionList = connections.get(gameID);
        for (Session c : sessionList) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
