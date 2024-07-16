package io.github.vimalai;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.Stack;

/**
 * A utility class for marshalling and unmarshalling XML using the StAX (Streaming API for XML) parser.
 */
public class HyperXML {

    /**
     * Marshalls an XMLNode object into its corresponding XML string representation.
     * @param xmlNode The XMLNode object to marshal.
     * @return The XML string representation of the XMLNode.
     */
    public String marshall(XMLNode xmlNode) {
        StringBuilder builder = new StringBuilder();
        String fullName = xmlNode.getPrefix().isEmpty() ? xmlNode.getName() : xmlNode.getPrefix() + ":" + xmlNode.getName();
        builder.append("<").append(fullName);

        // Add namespace and other variables if any
        xmlNode.getArguments().forEach(argument -> builder.append(" ").append(argument.getKey()).append("=\"").append(argument.getValue()).append("\""));

        // If there are no children and the value is null, close the tag immediately
        if (xmlNode.getChildren().isEmpty() && (xmlNode.getValue() == null || xmlNode.getValue().isEmpty())) {
            builder.append("/>\n");
        } else {
            builder.append(">");
            if (xmlNode.getValue() != null && !xmlNode.getValue().isEmpty()) {
                builder.append(xmlNode.getValue());
            }

            // Recursively add children
            for (XMLNode child : xmlNode.getChildren()) {
                builder.append("\n").append(marshall(child));
            }

            builder.append("</").append(fullName).append(">\n");
        }
        return builder.toString();
    }

    /**
     * Unmarshalls an XML string into an XMLNode object.
     * @param xml The XML string to unmarshal.
     * @return The root XMLNode object representing the parsed XML structure.
     * @throws XMLStreamException If an error occurs during XML parsing.
     */
    public XMLNode unmarshall(String xml) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Stack<XMLNode> stack = new Stack<>();
        XMLNode root = null;  // Initialize root node reference

        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));

        while (reader.hasNext()) {
            int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    String localName = reader.getLocalName();
                    String prefix = reader.getPrefix();
                    XMLNode newNode = new XMLNode(prefix, localName);

                    for (int i = 0; i < reader.getNamespaceCount(); i++) {
                        newNode.addArgument("xmlns:" + reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                    }

                    // Capture other attributes
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String attrName = reader.getAttributeLocalName(i);
                        String attrValue = reader.getAttributeValue(i);
                        newNode.addArgument(attrName, attrValue);
                    }

                    if (!stack.isEmpty()) {
                        stack.peek().addNode(newNode);
                    }
                    stack.push(newNode);
                    break;

                case XMLStreamReader.CHARACTERS:
                    if (!stack.isEmpty() && !reader.isWhiteSpace()) {
                        stack.peek().setValue(reader.getText().trim());
                    }
                    break;

                case XMLStreamReader.END_ELEMENT:
                    root = stack.pop();  // Update the last popped node as root
                    break;
            }
        }
        return root;  // Return the root node of the parsed XML
    }

}
