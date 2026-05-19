package server;

import impl.ChatRoomImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class ChatServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        try {
            System.out.println("Démarrage du serveur XML-RPC sur le port " + PORT + "...");
            
            WebServer webServer = new WebServer(PORT);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("ChatRoom", ChatRoomImpl.class);
            xmlRpcServer.setHandlerMapping(phm);
            
            XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);
            
            webServer.start();
            
            System.out.println("Serveur de chat XML-RPC démarré et prêt.");
            System.out.println("En attente de connexions sur http://localhost:" + PORT + "/xmlrpc");
            
        } catch (Exception e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
