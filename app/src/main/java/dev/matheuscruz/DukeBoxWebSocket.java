package dev.matheuscruz;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = "/ws/{username}")
public class DukeBoxWebSocket {

    public static Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Optional<Session> getSession(String username) {
        return Optional.ofNullable(sessions.get(username));
    }

    @OnOpen()
    public void onOpen(Session session, @PathParam("username") String username) {
        Log.info("username joined: " + username);
        sessions.put(username, session);
    }
}
