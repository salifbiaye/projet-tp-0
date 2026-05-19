package impl;

import interfaces.ChatRoom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomImpl implements ChatRoom {
    private static ChatRoomImpl instance = null;

    private final Map<String, String> users = new HashMap<>();
    private final List<String> messages = new ArrayList<>();

    private ChatRoomImpl() {
    }

    public static synchronized ChatRoomImpl getInstance() {
        if (instance == null) {
            instance = new ChatRoomImpl();
        }
        return instance;
    }

    @Override
    public synchronized String subscribe(String pseudo) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            return "ERROR: Pseudo obligatoire";
        }
        if (users.containsKey(pseudo)) {
            return "ERROR: Pseudo deja utilise";
        }

        users.put(pseudo, "connected");
        String message = "*** " + pseudo + " a rejoint la salle de discussion ***";
        messages.add(message);
        System.out.println(message);
        return "OK";
    }

    @Override
    public synchronized String unsubscribe(String pseudo) {
        users.remove(pseudo);
        String message = "*** " + pseudo + " a quitte la salle de discussion ***";
        messages.add(message);
        System.out.println(message);
        return "OK";
    }

    @Override
    public synchronized String postMessage(String pseudo, String message) {
        if (!users.containsKey(pseudo)) {
            return "ERROR: Utilisateur non connecte";
        }
        if (message == null || message.trim().isEmpty()) {
            return "ERROR: Message vide";
        }

        String fullMessage = pseudo + ": " + message;
        messages.add(fullMessage);
        System.out.println(fullMessage);
        return "OK";
    }

    @Override
    public synchronized String[] getMessages(int lastIndex) {
        if (lastIndex < 0) {
            lastIndex = 0;
        }
        if (lastIndex >= messages.size()) {
            return new String[0];
        }

        List<String> newMessages = messages.subList(lastIndex, messages.size());
        return newMessages.toArray(new String[0]);
    }

    @Override
    public synchronized int getMessageCount() {
        return messages.size();
    }
}
