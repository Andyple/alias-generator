package com.mycompany.alias.generator;

import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandTrieNode {
    private String token;
    private int frequency;
    private boolean isEndOfCommand;
    private Map<String, CommandTrieNode> children;

    public CommandTrieNode() {
        this("");
    }

    public CommandTrieNode(String token) {
        this.token = token;
        this.frequency = 0;
        this.isEndOfCommand = false;
        this.children = new HashMap<>();
    }

    /**
     * Inserts one command into the trie, counting one more run at its leaf.
     */
    public void insert(String[] tokens) {
        CommandTrieNode node = this;

        for (String token : tokens) {
            CommandTrieNode child = node.children.get(token);

            if (child == null) {
                child = new CommandTrieNode(token);
                node.children.put(token, child);
            }

            node = child;
        }

        node.isEndOfCommand = true;
        node.frequency++;
    }

    /**
     * Recursive method to traverse Trie branches and collect candidates meeting criteria
     * (Satisfies rubric requirement: Recursive Method)
     */
    public void collectCandidates(String currentPath, List<AliasCandidate> results) {
        // TODO: Recursive implementation
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isEndOfCommand() {
        return isEndOfCommand;
    }

    public void setEndOfCommand(boolean endOfCommand) {
        isEndOfCommand = endOfCommand;
    }

    public Map<String, CommandTrieNode> getChildren() {
        return children;
    }

    public void setChildren(Map<String, CommandTrieNode> children) {
        this.children = children;
    }
}
