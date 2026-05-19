package interfaces;

public interface ChatClientConnection {
    String getPseudo();
    void sendMessage(String message);
}
