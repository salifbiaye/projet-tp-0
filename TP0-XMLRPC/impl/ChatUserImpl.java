package impl;

import interfaces.ChatUser;

public class ChatUserImpl implements ChatUser {
    private String pseudo;
    
    public ChatUserImpl(String pseudo) {
        this.pseudo = pseudo;
    }
    
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }
    
    @Override
    public String getPseudo() {
        return pseudo;
    }
}
