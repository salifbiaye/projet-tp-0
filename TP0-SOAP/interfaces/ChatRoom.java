package interfaces;

public interface ChatRoom {
    String subscribe(String pseudo);
    String unsubscribe(String pseudo);
    String postMessage(String pseudo, String message);
    String[] getMessages(int lastIndex);
    int getMessageCount();
}
