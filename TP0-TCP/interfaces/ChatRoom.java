package interfaces;

public interface ChatRoom {
    String subscribe(ChatClientConnection client);
    void announceJoin(String pseudo);
    void unsubscribe(String pseudo);
    void postMessage(String pseudo, String message);
}
