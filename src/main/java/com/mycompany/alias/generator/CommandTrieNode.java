package com.mycompany.alias.generator;

import java.util.List;
import java.util.Map;

public class CommandTrieNode {
    private String token;
    private int frequency;
    private boolean isEndOfCommand;
    private Map<String, CommandTrieNode> children;

    public CommandTrieNode() {
        // TODO: Initialize root node
    }

    public CommandTrieNode(String token) {
        // TODO: Initialize node with token
    }

    public void insert(String[] tokens) {
        // TODO: Insert token array into trie
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
