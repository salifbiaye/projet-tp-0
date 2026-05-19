package util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class SoapUtil {
    public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String CHAT_NS = "http://chatuser.example/soap";

    private SoapUtil() {
    }

    public static String envelope(String body) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\" xmlns:chat=\"" + CHAT_NS + "\">"
                + "<soap:Body>" + body + "</soap:Body>"
                + "</soap:Envelope>";
    }

    public static String operationRequest(String operation, String... nameValuePairs) {
        StringBuilder body = new StringBuilder();
        body.append("<chat:").append(operation).append(">");
        for (int i = 0; i + 1 < nameValuePairs.length; i += 2) {
            body.append('<').append(nameValuePairs[i]).append('>')
                    .append(escape(nameValuePairs[i + 1]))
                    .append("</").append(nameValuePairs[i]).append('>');
        }
        body.append("</chat:").append(operation).append(">");
        return envelope(body.toString());
    }

    public static String statusResponse(String operation, String result) {
        return envelope("<chat:" + operation + "Response><result>" + escape(result)
                + "</result></chat:" + operation + "Response>");
    }

    public static String messagesResponse(String[] messages, int nextIndex) {
        StringBuilder body = new StringBuilder();
        body.append("<chat:getMessagesResponse><nextIndex>").append(nextIndex).append("</nextIndex><messages>");
        for (String message : messages) {
            body.append("<message>").append(escape(message)).append("</message>");
        }
        body.append("</messages></chat:getMessagesResponse>");
        return envelope(body.toString());
    }

    public static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    public static Element getSoapOperation(Document document) {
        Element body = firstElementByLocalName(document.getDocumentElement(), "Body");
        if (body == null) {
            return null;
        }

        NodeList children = body.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                return (Element) node;
            }
        }
        return null;
    }

    public static String childText(Element element, String childName) {
        Element child = firstElementByLocalName(element, childName);
        return child == null ? "" : child.getTextContent();
    }

    public static String[] messageArray(Document document) {
        NodeList nodes = document.getElementsByTagName("message");
        if (nodes.getLength() == 0) {
            nodes = document.getElementsByTagNameNS("*", "message");
        }

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            messages.add(nodes.item(i).getTextContent());
        }
        return messages.toArray(new String[0]);
    }

    public static Element firstElementByLocalName(Node node, String localName) {
        if (node instanceof Element && localName.equals(localName(node))) {
            return (Element) node;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Element found = firstElementByLocalName(children.item(i), localName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public static String localName(Node node) {
        String localName = node.getLocalName();
        if (localName != null) {
            return localName;
        }

        String nodeName = node.getNodeName();
        int separator = nodeName.indexOf(':');
        return separator >= 0 ? nodeName.substring(separator + 1) : nodeName;
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
