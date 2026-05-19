package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatClient extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 8083;

    private final String title = "Logiciel de discussion en ligne (TCP Sockets)";
    private String pseudo = null;

    private final JTextArea txtOutput = new JTextArea();
    private final JTextField txtMessage = new JTextField();
    private final JButton btnSend = new JButton("Envoyer");

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public ChatClient() {
        super();
        setTitle(title);
        createIHM();
        requestPseudo();
        connectToServer();
        startMessageReader();
    }

    public void createIHM() {
        JPanel panel = (JPanel) this.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                window_windowClosing(e);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == '\n') {
                    btnSend_actionPerformed(null);
                }
            }
        });

        this.txtOutput.setBackground(new Color(220, 220, 220));
        this.txtOutput.setEditable(false);
        this.setSize(500, 400);
        this.setVisible(true);
        this.txtMessage.requestFocus();
    }

    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(this, "Entrez votre pseudo : ", this.title, JOptionPane.OK_OPTION);
        if (this.pseudo == null || this.pseudo.trim().isEmpty()) {
            System.exit(0);
        }
        this.pseudo = this.pseudo.trim();
        setTitle(title + " - " + pseudo);
    }

    public void connectToServer() {
        try {
            socket = new Socket(HOST, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

            output.println(pseudo);
            String response = input.readLine();
            if (response == null || response.startsWith("ERROR")) {
                JOptionPane.showMessageDialog(this, response == null ? "Serveur indisponible" : response, "Erreur", JOptionPane.ERROR_MESSAGE);
                closeSocket();
                System.exit(1);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void startMessageReader() {
        Thread reader = new Thread(new Runnable() {
            public void run() {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        displayMessage(message);
                    }
                } catch (IOException e) {
                    displayMessage("Connexion avec le serveur terminee.");
                }
            }
        });
        reader.start();
    }

    public void window_windowClosing(WindowEvent e) {
        closeSocket();
        System.exit(0);
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        String message = this.txtMessage.getText().trim();
        if (!message.isEmpty() && output != null) {
            output.println(message);
            this.txtMessage.setText("");
            this.txtMessage.requestFocus();
        }
    }

    private void displayMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String displayedMessage = message;
                if (displayedMessage.startsWith(pseudo + ":")) {
                    displayedMessage = displayedMessage.replaceFirst("^" + pseudo + ":", "Vous:");
                }
                txtOutput.append(displayedMessage + "\n");
                txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
            }
        });
    }

    private void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur fermeture socket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
