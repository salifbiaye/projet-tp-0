package impl;

import interfaces.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoom {
    private Map<String, ChatUser> users;
    
    public ChatRoomImpl() throws RemoteException {
        super();
        users = new HashMap<>();
    }
    
    @Override
    public synchronized void subscribe(ChatUser user, String pseudo) throws RemoteException {
        users.put(pseudo, user);
        String message = "*** " + pseudo + " a rejoint la salle de discussion ***";
        System.out.println(message);
        broadcastMessage(message);
    }
    
    @Override
    public synchronized void unsubscribe(String pseudo) throws RemoteException {
        users.remove(pseudo);
        String message = "*** " + pseudo + " a quitté la salle de discussion ***";
        System.out.println(message);
        broadcastMessage(message);
    }
    
    @Override
    public synchronized void postMessage(String pseudo, String message) throws RemoteException {
        
        String fullMessage = pseudo + ": " + message;
        System.out.println(fullMessage);
        broadcastMessage(fullMessage);
    }
    
    private void broadcastMessage(String message) {
        List<String> disconnectedUsers = new ArrayList<>();
        
        for (Map.Entry<String, ChatUser> entry : users.entrySet()) {
            try {
                entry.getValue().displayMessage(message);
            } catch (RemoteException e) {
                System.out.println("Erreur lors de l'envoi à " + entry.getKey());
                disconnectedUsers.add(entry.getKey());
            }
        }
        
        // Nettoyer les utilisateurs déconnectés
        for (String pseudo : disconnectedUsers) {
            users.remove(pseudo);
        }
    }
}
