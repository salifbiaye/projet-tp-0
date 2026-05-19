package server;

import impl.ChatRoomImpl;
import interfaces.ChatClientConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatServer {
    private static final int PORT = 8083;
    private static final ChatRoomImpl chatRoom = new ChatRoomImpl();

    public static void main(String[] args) {
        System.out.println("Demarrage du serveur TCP sur le port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de chat TCP demarre et pret.");
            System.out.println("En attente de connexions sur localhost:" + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable, ChatClientConnection {
        private final Socket socket;
        private PrintWriter output;
        private String pseudo;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
            ) {
                output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

                pseudo = cleanLine(input.readLine());
                String result = chatRoom.subscribe(this);
                output.println(result);

                if (result.startsWith("ERROR")) {
                    return;
                }

                chatRoom.announceJoin(pseudo);

                String message;
                while ((message = input.readLine()) != null) {
                    chatRoom.postMessage(pseudo, cleanLine(message));
                }
            } catch (IOException e) {
                System.out.println("Connexion terminee pour " + pseudo);
            } finally {
                closeConnection();
            }
        }

        @Override
        public String getPseudo() {
            return pseudo;
        }

        @Override
        public void sendMessage(String message) {
            output.println(message);
        }

        private void closeConnection() {
            chatRoom.unsubscribe(pseudo);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erreur fermeture socket: " + e.getMessage());
            }
        }

        private String cleanLine(String value) {
            if (value == null) {
                return null;
            }
            if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
                value = value.substring(1);
            }
            return value.trim();
        }
    }
}
