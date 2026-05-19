package client;

import interfaces.*;
import impl.*;
import java.rmi.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Récupérer la référence de la salle de discussion
            ChatRoom chatRoom = (ChatRoom) Naming.lookup("rmi://localhost/ChatRoom");
            
            // Créer l'objet client distant avec interface graphique
            ChatUserImpl user = new ChatUserImpl();
            
            // Associer la salle de discussion au client
            user.setChatRoom(chatRoom);
            
            // S'inscrire à la salle de discussion
            chatRoom.subscribe(user, user.getPseudo());
            
            System.out.println("Connecté à la salle de discussion en tant que: " + user.getPseudo());
            
        } catch (Exception e) {
            System.err.println("Erreur client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
