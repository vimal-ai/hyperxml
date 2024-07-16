package io.github.vimalai;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an XML node.
 */
public class XMLNode {
    /**
     * The prefix of the XML node.
     */
    @Getter
    private final String prefix;

    /**
     * The name of the XML node.
     */
    @Getter
    private final String name;

    /**
     * The value of the XML node.
     */
    @Getter
    @Setter
    private String value;

    /**
     * The list of child XML nodes.
     */
    @Getter
    private final List<XMLNode> children;

    /**
     * The list of arguments associated with the XML node.
     */
    @Getter
    private final List<Pair<String, String>> arguments;

    /**
     * Mapping of child XML nodes by their names.
     */
    private final Map<String, List<XMLNode>> childrenMap;

    /**
     * Mapping of arguments by their keys.
     */
    private final Map<String, String> argumentsMap;

    /**
     * Constructs an XML node with the specified name.
     * @param name The name of the XML node.
     */
    public XMLNode(String name) {
        this.prefix = null;
        this.name = name;
        this.value = null;
        this.children = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.argumentsMap = new HashMap<>();
        this.childrenMap = new HashMap<>();
    }

    /**
     * Constructs an XML node with the specified prefix and name.
     * @param prefix The prefix of the XML node.
     * @param name The name of the XML node.
     */
    public XMLNode(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
        this.value = null;
        this.children = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.argumentsMap = new HashMap<>();
        this.childrenMap = new HashMap<>();
    }

    /**
     * Constructs an XML node with the specified prefix, name, and value.
     * @param prefix The prefix of the XML node.
     * @param name The name of the XML node.
     * @param value The value of the XML node.
     */
    public XMLNode(String prefix, String name, String value) {
        this.prefix = prefix;
        this.name = name;
        this.value = value;
        this.children = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.argumentsMap = new HashMap<>();
        this.childrenMap = new HashMap<>();
    }

    /**
     * Retrieves the first child XML node with the specified name.
     * @param name The name of the child XML node to retrieve.
     * @return The first child XML node with the specified name, or null if not found.
     */
    public XMLNode getNode(String name){
        List<XMLNode> xmlNodes = childrenMap.get(name);
        if(CollectionUtils.isEmpty(xmlNodes)){
            return null;
        }
        return xmlNodes.get(0);
    }

    /**
     * Adds a child XML node to this XML node.
     * @param xmlNode The XML node to add as a child.
     * @return
     */
    public void addNode(XMLNode xmlNode){
        children.add(xmlNode);
        String name = xmlNode.getName();
        List<XMLNode> xmlNodes = childrenMap.get(name);
        if(CollectionUtils.isEmpty(xmlNodes)){
            xmlNodes = new ArrayList<>();
        }
        xmlNodes.add(xmlNode);
        childrenMap.put(xmlNode.getName(), xmlNodes);
    }

    /**
     * Retrieves a list of child XML nodes with the specified name.
     * @param name The name of the child XML nodes to retrieve.
     * @return A list of child XML nodes with the specified name, or null if none found.
     */
    public List<XMLNode> getNodeList(String name) {
        return childrenMap.get(name);
    }

    /**
     * Retrieves the value of the argument with the specified key.
     * @param key The key of the argument.
     * @return The value of the argument with the specified key, or null if not found.
     */
    public String getArgument(String key){
        return argumentsMap.get(key);
    }

    /**
     * Adds an argument to this XML node.
     * @param key The key of the argument.
     * @param value The value of the argument.
     * @return true if the argument was added successfully, false if an argument with the same key already exists.
     */
    public boolean addArgument(String key, String value){
        if(argumentsMap.containsKey(key)){
            return false;
        }
        arguments.add(new Pair<>(key, value));
        argumentsMap.put(key, value);
        return true;
    }

    /**
     * Retrieves the XML node located at the specified path.
     * @param path The path specifying the location of the XML node.
     * @return The XML node at the specified path, or null if not found or path is invalid.
     */
    public XMLNode getNodeAtPath(String path){
        if (path==null || path=="") {
            return null; // Invalid input handling
        }

        String[] parts = path.split("/");
        XMLNode currentNode = this;

        // Traverse down to the node specified by the path
        for (String part : parts) {
            if (currentNode != null) {
                currentNode = currentNode.getNode(part);
            } else {
                return null; // Path is invalid, or node does not exist
            }
        }
        return currentNode;
    }

    /**
     * Adds a child XML node at the specified path.
     * @param path The path specifying the location where the new XML node should be added.
     * @param newNode The XML node to add.
     * @return true if the XML node was added successfully, false if the specified path does not exist.
     */
    public boolean addNodeAtPath(String path, XMLNode newNode){
        XMLNode xmlNode = getNodeAtPath(path);
        if(xmlNode != null){
            xmlNode.addNode(newNode);
            return true;
        }
        return false; // Node not found
    }

    /**
     * Retrieves the value of the XML node located at the specified path.
     * @param path The path specifying the location of the XML node.
     * @return The value of the XML node at the specified path, or null if not found or path is invalid.
     */
    public String getValueAtPath(String path){
        XMLNode xmlNode = getNodeAtPath(path);
        if(xmlNode != null){
            return xmlNode.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the XML node located at the specified path.
     * @param path The path specifying the location of the XML node.
     * @param value The value to set.
     * @return true if the value was set successfully, false if the specified path does not exist.
     */
    public boolean setValueAtPath(String path, String value){
        XMLNode xmlNode = getNodeAtPath(path);
        if(xmlNode != null){
            xmlNode.setValue(value);
            return true;
        }
        return false; // Node not found
    }

    /**
     * Retrieves the value of the argument with the specified key located at the specified path.
     * @param path The path specifying the location of the XML node.
     * @param key The key of the argument.
     * @return The value of the argument with the specified key, located at the specified path,
     *         or null if not found or path is invalid.
     */
    public String getArgumentAtPath(String path, String key){
        XMLNode xmlNode = getNodeAtPath(path);
        if(xmlNode != null){
            return xmlNode.getArgument(key);
        }
        return null;

    }

    /**
     * Adds an argument to the XML node located at the specified path.
     * @param path The path specifying the location of the XML node.
     * @param key The key of the argument.
     * @param value The value of the argument.
     * @return true if the argument was added successfully, false if the specified path does not exist.
     */
    boolean addArgumentAtPath(String path, String key, String value){
        XMLNode xmlNode = getNodeAtPath(path);
        if(xmlNode != null){
            xmlNode.addArgument(key, value);
            return true;
        }
        return false; // Node not found
    }

}
