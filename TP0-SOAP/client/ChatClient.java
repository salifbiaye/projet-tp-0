package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.SoapUtil;

public class ChatClient extends JFrame {
    private static final String SERVER_URL = "http://localhost:8081/chat";

    private final String title = "Logiciel de discussion en ligne (SOAP)";
    private String pseudo = null;

    private final JTextArea txtOutput = new JTextArea();
    private final JTextField txtMessage = new JTextField();
    private final JButton btnSend = new JButton("Envoyer");

    private int lastMessageIndex = 0;
    private javax.swing.Timer messageTimer;

    public ChatClient() {
        super();
        setTitle(title);
        createIHM();
        requestPseudo();
        subscribe();
        startMessagePolling();
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

    public void subscribe() {
        try {
            String result = statusOperation("subscribe", "pseudo", pseudo);
            if (result.startsWith("ERROR")) {
                JOptionPane.showMessageDialog(this, result, "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            fetchNewMessages();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void startMessagePolling() {
        messageTimer = new javax.swing.Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchNewMessages();
            }
        });
        messageTimer.start();
    }

    public void fetchNewMessages() {
        try {
            String request = SoapUtil.operationRequest("getMessages", "lastIndex", String.valueOf(lastMessageIndex));
            Document document = SoapUtil.parse(sendSoap("getMessages", request));
            String[] messages = SoapUtil.messageArray(document);
            Element nextIndex = SoapUtil.firstElementByLocalName(document.getDocumentElement(), "nextIndex");
            lastMessageIndex = nextIndex == null ? lastMessageIndex + messages.length : Integer.parseInt(nextIndex.getTextContent());
            for (String message : messages) {
                if (!message.startsWith(pseudo + ":")) {
                    txtOutput.append(message + "\n");
                }
            }

            if (messages.length > 0) {
                txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
            }
        } catch (Exception e) {
            // Le timer reessaiera au prochain cycle.
        }
    }

    public void window_windowClosing(WindowEvent e) {
        try {
            if (messageTimer != null) {
                messageTimer.stop();
            }
            statusOperation("unsubscribe", "pseudo", pseudo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        String message = this.txtMessage.getText().trim();
        if (!message.isEmpty()) {
            try {
                String result = statusOperation("postMessage", "pseudo", pseudo, "message", message);
                if (result.startsWith("ERROR")) {
                    txtOutput.append("Erreur d'envoi: " + result + "\n");
                    return;
                }

                txtOutput.append("Vous: " + message + "\n");
                txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
                this.txtMessage.setText("");
                this.txtMessage.requestFocus();
            } catch (Exception ex) {
                txtOutput.append("Erreur d'envoi: " + ex.getMessage() + "\n");
            }
        }
    }

    private String statusOperation(String operation, String... nameValuePairs) throws Exception {
        String request = SoapUtil.operationRequest(operation, nameValuePairs);
        Document document = SoapUtil.parse(sendSoap(operation, request));
        Element result = SoapUtil.firstElementByLocalName(document.getDocumentElement(), "result");
        return result == null ? "" : result.getTextContent();
    }

    private String sendSoap(String soapAction, String body) throws IOException {
        URL url = new URL(SERVER_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setDoOutput(true);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(body.getBytes(StandardCharsets.UTF_8));
        }

        InputStream input = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (input == null) {
            return "";
        }
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
