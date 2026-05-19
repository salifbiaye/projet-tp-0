package client;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient extends JFrame {
    private String title = "Logiciel de discussion en ligne (XML-RPC)";
    private String pseudo = null;
    
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    
    private XmlRpcClient client;
    private int lastMessageIndex = 0;
    private javax.swing.Timer messageTimer;
    
    public ChatClient() {
        super();
        setTitle(title);
        
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://localhost:8080/xmlrpc"));
            client = new XmlRpcClient();
            client.setConfig(config);
            
            this.createIHM();
            this.requestPseudo();
            this.subscribe();
            this.startMessagePolling();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de connexion au serveur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    public void createIHM() {
        JPanel panel = (JPanel)this.getContentPane();
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
                if (event.getKeyChar() == '\n')
                    btnSend_actionPerformed(null);
            }
        });
        
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
        this.setSize(500,400);
        this.setVisible(true);
        this.txtMessage.requestFocus();
    }
    
    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this, "Entrez votre pseudo : ",
                this.title, JOptionPane.OK_OPTION
        );
        if (this.pseudo == null || this.pseudo.trim().isEmpty()) {
            System.exit(0);
        }
        setTitle(title + " - " + pseudo);
    }
    
    public void subscribe() {
        try {
            Object[] params = new Object[]{pseudo};
            String result = (String) client.execute("ChatRoom.subscribe", params);
            if (result.startsWith("ERROR")) {
                JOptionPane.showMessageDialog(this, result, "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            fetchNewMessages();
        } catch (Exception e) {
            e.printStackTrace();
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
            Object[] params = new Object[]{lastMessageIndex};
            Object[] messages = (Object[]) client.execute("ChatRoom.getMessages", params);
            
            for (Object msg : messages) {
                String message = (String) msg;
                if (!message.startsWith(pseudo + ":")) {
                    txtOutput.append(message + "\n");
                }
                lastMessageIndex++;
            }
            
            if (messages.length > 0) {
                txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
            }
            
        } catch (Exception e) {
        }
    }
    
    public void window_windowClosing(WindowEvent e) {
        try {
            if (messageTimer != null) {
                messageTimer.stop();
            }
            Object[] params = new Object[]{pseudo};
            client.execute("ChatRoom.unsubscribe", params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    
    public void btnSend_actionPerformed(ActionEvent e) {
        String message = this.txtMessage.getText().trim();
        if (!message.isEmpty()) {
            try {
                Object[] params = new Object[]{pseudo, message};
                client.execute("ChatRoom.postMessage", params);
                
                txtOutput.append("Vous: " + message + "\n");
                txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
                
                this.txtMessage.setText("");
                this.txtMessage.requestFocus();
            } catch (Exception ex) {
                txtOutput.append("Erreur d'envoi: " + ex.getMessage() + "\n");
            }
        }
    }
    
    public static void main(String[] args) {
        new ChatClient();
    }
}
