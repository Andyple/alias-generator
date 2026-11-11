package com.mycompany.alias.generator;

import java.util.ArrayList;
import java.util.List;

public class AliasEngine {
    private HistoryScanner scanner;
    private CommandTrieNode rootNode;
    private List<AliasCandidate> candidates;
    private int scannedCount;

    public AliasEngine() {
        this.scanner = new HistoryScanner();
        this.rootNode = new CommandTrieNode();
        this.candidates = new ArrayList<>();
    }

    public void runPipeline() {
        scanner.loadExistingAliases();
        scannedCount = scanner.readHistory(rootNode);

        candidates.clear();
        rootNode.collectCandidates("", candidates);

        for (AliasCandidate candidate : candidates) {
            candidate.setProposedAlias(generateAliasName(candidate.getOriginalCommand()));
        }

        candidates.sort((a, b) -> Integer.compare(b.getPainScore(), a.getPainScore()));

        renderTUI();
        handleUserInput();
    }

    public void renderTUI() {
        System.out.println("========================================================================");
        System.out.println("                      ALIAS-GEN : SHELL OPTIMIZER");
        System.out.println("========================================================================");
        System.out.println("[Config] Ingested " + scanner.getAliasNames().size()
                + " existing aliases from ~/.bash_aliases");
        System.out.println("[Scan]   Parsed " + String.format("%,d", scannedCount)
                + " commands from ~/.bash_history");
        System.out.println("[Filter] " + candidates.size()
                + " distinct candidate commands qualified by Pain Score");
        System.out.println("------------------------------------------------------------------------");

        if (candidates.isEmpty()) {
            System.out.println("[i] No alias candidates found. Nothing to do.");
            return;
        }

        System.out.printf("%-4s %5s %6s   %-30s %s%n",
                "#", "FREQ", "PAIN", "PROPOSED ALIAS", "ORIGINAL COMMAND");
        System.out.println("------------------------------------------------------------------------");

        for (int i = 0; i < candidates.size(); i++) {
            AliasCandidate candidate = candidates.get(i);
            String aliasText = "alias " + candidate.getProposedAlias()
                    + "='" + candidate.getOriginalCommand() + "'";

            System.out.printf("[%d] %4dx %6d   %-30s %s%n",
                    i + 1,
                    candidate.getFrequency(),
                    candidate.getPainScore(),
                    truncate(aliasText, 30),
                    truncate(candidate.getOriginalCommand(), 40));
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.println("[Actions]");
        System.out.println("  • Type index numbers separated by commas to append (e.g., \"1, 3\")");
        System.out.println("  • Type \"e <num>\" to rename an alias (e.g., \"e 1\")");
        System.out.println("  • Type \"a\" or \"all\" to append all suggestions");
        System.out.println("  • Type \"q\" to quit without modifying files");
        System.out.println("------------------------------------------------------------------------");
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
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
