package impl;

import interfaces.ChatClientConnection;
import interfaces.ChatRoom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomImpl implements ChatRoom {
    private final Map<String, ChatClientConnection> clients = new HashMap<>();

    @Override
    public synchronized String subscribe(ChatClientConnection client) {
        String pseudo = client.getPseudo();
        if (pseudo == null || pseudo.trim().isEmpty()) {
            return "ERROR: Pseudo obligatoire";
        }
        if (clients.containsKey(pseudo)) {
            return "ERROR: Pseudo deja utilise";
        }

        clients.put(pseudo, client);
        return "OK";
    }

    @Override
    public synchronized void announceJoin(String pseudo) {
        if (clients.containsKey(pseudo)) {
            broadcast("*** " + pseudo + " a rejoint la salle de discussion ***");
        }
    }

    @Override
    public synchronized void unsubscribe(String pseudo) {
        if (pseudo == null || clients.remove(pseudo) == null) {
            return;
        }
        broadcast("*** " + pseudo + " a quitte la salle de discussion ***");
    }

    @Override
    public synchronized void postMessage(String pseudo, String message) {
        if (!clients.containsKey(pseudo) || message == null || message.trim().isEmpty()) {
            return;
        }
        broadcast(pseudo + ": " + message);
    }

    private void broadcast(String message) {
        System.out.println(message);
        List<String> disconnectedClients = new ArrayList<>();

        for (Map.Entry<String, ChatClientConnection> entry : clients.entrySet()) {
            try {
                entry.getValue().sendMessage(message);
            } catch (Exception e) {
                disconnectedClients.add(entry.getKey());
            }
        }

        for (String pseudo : disconnectedClients) {
            clients.remove(pseudo);
        }
    }
}
