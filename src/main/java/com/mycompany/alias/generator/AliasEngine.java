package com.mycompany.alias.generator;

import java.util.ArrayList;
import java.util.List;

public class AliasEngine {
    private HistoryScanner scanner;
    private CommandTrieNode rootNode;
    private List<AliasCandidate> candidates;

    public AliasEngine() {
        this.scanner = new HistoryScanner();
        this.rootNode = new CommandTrieNode();
        this.candidates = new ArrayList<>();
    }

    public void runPipeline() {
        // TODO: Orchestrate ingestion -> scanning -> ranking -> TUI interaction
    }

    public void renderTUI() {
        // TODO: Render ASCII/ANSI terminal table and action prompts
    }

    /**
     * "Acronym + Target" rule: the initials of the first two meaningful words,
     * followed by the last non-flag word (docker run ... postgres -> dr-postgres).
     * Numeric suffixes resolve collisions with existing or already proposed names.
     */
    public String generateAliasName(String cmd) {
        List<String> words = new ArrayList<>();

        for (String token : cmd.split("\\s+")) {
            if (token.startsWith("-")) {
                continue;
            }

            String word = token.replaceAll("[^A-Za-z0-9]", "");

            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        if (words.isEmpty()) {
            return "cmd";
        }

        String target = words.get(words.size() - 1).toLowerCase();
        String base;

        if (words.size() == 1) {
            base = target;
        } else {
            String acronym = (words.get(0).charAt(0) + "" + words.get(1).charAt(0)).toLowerCase();
            base = acronym + "-" + target;
        }

        String name = base;
        int suffix = 2;

        while (isNameTaken(name)) {
            name = base + suffix;
            suffix++;
        }

        return name;
    }

    private boolean isNameTaken(String name) {
        if (scanner.getAliasNames().contains(name)) {
            return true;
        }

        for (AliasCandidate candidate : candidates) {
            if (name.equals(candidate.getProposedAlias())) {
                return true;
            }
        }

        return false;
    }

    public void handleUserInput() {
        // TODO: Handle user selection loop (append, edit, quit)
    }

    public static void main(String[] args) {
        // TODO: Driver execution entry point
    }

    public HistoryScanner getScanner() {
        return scanner;
    }

    public void setScanner(HistoryScanner scanner) {
        this.scanner = scanner;
    }

    public CommandTrieNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(CommandTrieNode rootNode) {
        this.rootNode = rootNode;
    }

    public List<AliasCandidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<AliasCandidate> candidates) {
        this.candidates = candidates;
    }
}
