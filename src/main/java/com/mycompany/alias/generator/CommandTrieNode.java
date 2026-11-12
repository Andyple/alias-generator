package com.mycompany.alias.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// One word in a command; its children are the words that can follow it.
public class CommandTrieNode {
    private final String token;
    private int frequency;
    private boolean isEndOfCommand;
    private final Map<String, CommandTrieNode> children = new HashMap<>();

    public CommandTrieNode() {
        this("");
    }

    public CommandTrieNode(String token) {
        this.token = token;
    }

    // Adds one command, counting a run at its final word.
    public void insert(String[] tokens) {
        CommandTrieNode node = this;

        for (String token : tokens) {
            node = node.children.computeIfAbsent(token, CommandTrieNode::new);
        }

        node.isEndOfCommand = true;
        node.frequency++;
    }

    // Recursively walks every branch and collects commands that meet the pain threshold.
    public void collectCandidates(String currentPath, List<AliasCandidate> results) {
        if (isEndOfCommand && qualifies(currentPath)) {
            int painScore = currentPath.length() * frequency;
            results.add(new AliasCandidate(currentPath, frequency, painScore));
        }

        for (CommandTrieNode child : children.values()) {
            String childPath = currentPath.isEmpty()
                    ? child.token
                    : currentPath + " " + child.token;

            child.collectCandidates(childPath, results);
        }
    }

    // Short commands need more runs; long one-liners are worth aliasing sooner.
    private boolean qualifies(String command) {
        int runsRequired;

        if (command.length() < 20) {
            runsRequired = 6;
        } else if (command.length() <= 40) {
            runsRequired = 4;
        } else {
            runsRequired = 2;
        }

        return frequency >= runsRequired;
    }
}
