package impl;

import interfaces.ChatRoom;
import java.util.*;

public class ChatRoomImpl implements ChatRoom {
    private static ChatRoomImpl instance = null;
    
    private Map<String, String> users = new HashMap<>();
    private List<String> messages = new ArrayList<>();
    
    public ChatRoomImpl() {
    }
    
    public static synchronized ChatRoomImpl getInstance() {
        if (instance == null) {
            instance = new ChatRoomImpl();
        }
        return instance;
    }
    
    @Override
    public synchronized String subscribe(String pseudo) {
        ChatRoomImpl room = getInstance();
        if (room.users.containsKey(pseudo)) {
            return "ERROR: Pseudo déjà utilisé";
        }
        room.users.put(pseudo, "connected");
        String message = "*** " + pseudo + " a rejoint la salle de discussion ***";
        room.messages.add(message);
        System.out.println(message);
        return "OK";
    }
    
    @Override
    public synchronized String unsubscribe(String pseudo) {
        ChatRoomImpl room = getInstance();
        room.users.remove(pseudo);
        String message = "*** " + pseudo + " a quitté la salle de discussion ***";
        room.messages.add(message);
        System.out.println(message);
        return "OK";
    }
    
    @Override
    public synchronized String postMessage(String pseudo, String message) {
        ChatRoomImpl room = getInstance();
        String fullMessage = pseudo + ": " + message;
        room.messages.add(fullMessage);
        System.out.println(fullMessage);
        return "OK";
    }
    
    @Override
    public synchronized String[] getMessages(int lastIndex) {
        ChatRoomImpl room = getInstance();
        if (lastIndex >= room.messages.size()) {
            return new String[0];
        }
        List<String> newMessages = room.messages.subList(lastIndex, room.messages.size());
        return newMessages.toArray(new String[0]);
    }
}
