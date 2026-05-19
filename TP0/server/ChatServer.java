package server;

import impl.*;
import java.rmi.*;
import java.rmi.registry.*;

public class ChatServer {
    public static void main(String[] args) {
        try {

            ChatRoomImpl chatRoom = new ChatRoomImpl();
            

            LocateRegistry.createRegistry(1099);
            

            Naming.rebind("rmi://localhost/ChatRoom", chatRoom);
            
            System.out.println("Serveur de chat démarré et prêt.");
            System.out.println("En attente de connexions...");
            
        } catch (Exception e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
