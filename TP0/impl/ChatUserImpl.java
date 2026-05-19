package impl;

import interfaces.*;
import java.rmi.*;
import java.rmi.server.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatUserImpl extends UnicastRemoteObject implements ChatUser {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    
    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    
    private ChatRoom chatRoom = null;
    
    public ChatUserImpl(String pseudo) throws RemoteException {
        super();
        this.pseudo = pseudo;
        this.createIHM();
    }
    
    public ChatUserImpl() throws RemoteException {
        super();
        this.createIHM();
        this.requestPseudo();
    }
    
    public void createIHM() {
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
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
        
        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }
    
    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title, JOptionPane.OK_OPTION
        );
        if (this.pseudo == null) System.exit(0);
    }
    
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    public void window_windowClosing(WindowEvent e) {
        try {
            if (chatRoom != null) {
                chatRoom.unsubscribe(pseudo);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    
    public void btnSend_actionPerformed(ActionEvent e) {
        String message = this.txtMessage.getText().trim();
        if (!message.isEmpty() && chatRoom != null) {
            try {
                chatRoom.postMessage(pseudo, message);
                this.txtMessage.setText("");
                this.txtMessage.requestFocus();
            } catch (RemoteException ex) {
                txtOutput.append("Erreur d'envoi: " + ex.getMessage() + "\n");
            }
        }
    }
    
    @Override
    public void displayMessage(String message) throws RemoteException {
        // Remplacer le pseudo par "Vous" si c'est notre propre message
        if (message.startsWith(pseudo + ":")) {
            message = message.replaceFirst("^" + pseudo + ":", "Vous:");
        }
        txtOutput.append(message + "\n");
        // Auto-scroll vers le bas
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }
    
    public String getPseudo() {
        return pseudo;
    }
}
