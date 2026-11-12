package com.mycompany.alias.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Scans history, ranks commands, and appends the aliases the user approves.
public class AliasEngine {
    private static final String BANNER = "=".repeat(72);
    private static final String SEPARATOR = "-".repeat(72);

    private final HistoryScanner scanner;
    private final CommandTrieNode rootNode;
    private final List<AliasCandidate> candidates = new ArrayList<>();
    private int scannedCount;

    public AliasEngine() {
        this(new HistoryScanner());
    }

    public AliasEngine(HistoryScanner scanner) {
        this.scanner = scanner;
        this.rootNode = new CommandTrieNode();
    }

    public void runPipeline() {
        prepare();
        renderTUI();
        handleUserInput();
    }

    // Builds the candidate list and gives every candidate a unique alias name.
    private void prepare() {
        scanner.loadExistingAliases();
        scannedCount = scanner.readHistory(rootNode);

        candidates.clear();
        rootNode.collectCandidates("", candidates);

        for (AliasCandidate candidate : candidates) {
            candidate.setProposedAlias(generateAliasName(candidate.getOriginalCommand()));
        }

        candidates.sort((a, b) -> Integer.compare(b.getPainScore(), a.getPainScore()));
    }

    // Prints the scan summary, the ranked table, and the available commands.
    private void renderTUI() {
        System.out.println(BANNER);
        System.out.println("                      ALIAS-GEN : SHELL OPTIMIZER");
        System.out.println(BANNER);
        System.out.println("[Config] Ingested " + scanner.getAliasNames().size()
                + " existing aliases from " + scanner.getBashAliasesPath());
        System.out.println("[Scan]   Parsed " + String.format("%,d", scannedCount)
                + " commands from " + scanner.getHistoryPath());
        System.out.println("[Filter] " + candidates.size()
                + " distinct candidate commands qualified by Pain Score");
        System.out.println(SEPARATOR);

        if (candidates.isEmpty()) {
            System.out.println("[i] No alias candidates found. Nothing to do.");
            return;
        }

        System.out.printf("%-4s %5s %6s   %-30s %s%n",
                "#", "FREQ", "PAIN", "PROPOSED ALIAS", "ORIGINAL COMMAND");
        System.out.println(SEPARATOR);

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

        System.out.println(SEPARATOR);
        System.out.println("[Actions]");
        System.out.println("  • Type index numbers separated by commas to append (e.g., \"1, 3\")");
        System.out.println("  • Type \"e <num>\" to rename an alias (e.g., \"e 1\")");
        System.out.println("  • Type \"a\" or \"all\" to append all suggestions");
        System.out.println("  • Type \"q\" to quit without modifying files");
        System.out.println(SEPARATOR);
    }

    // Shortens a table cell so long commands do not break the column layout.
    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }

    // Builds an alias from the first letters of two words plus the last word.
    private String generateAliasName(String command) {
        List<String> words = new ArrayList<>();

        for (String token : command.split("\\s+")) {
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
            String acronym = (words.get(0).charAt(0) + "" + words.get(1).charAt(0))
                    .toLowerCase();
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

    // Checks existing aliases and names already proposed during this run.
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

    // Reads commands until the user appends aliases or quits.
    private void handleUserInput() {
        if (candidates.isEmpty()) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (true) {
                System.out.print("Select: ");
                String input = reader.readLine();

                if (input == null) {
                    return;
                }

                input = input.trim();

                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("q")) {
                    System.out.println("[i] Aborted by user. No files were modified.");
                    return;
                }

                if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("all")) {
                    appendAndReport(candidates);
                    return;
                }

                if (input.toLowerCase().startsWith("e ")) {
                    renameAlias(input.substring(2).trim(), reader);
                    continue;
                }

                List<AliasCandidate> selected = parseSelection(input);

                if (selected == null) {
                    System.out.println("[!] Unrecognized input. "
                            + "Enter index numbers, 'e <num>', 'a', or 'q'.");
                    continue;
                }

                appendAndReport(selected);
                return;
            }
        } catch (IOException e) {
            System.err.println("[warn] Could not read input: " + e.getMessage());
        }
    }

    // Parses "1, 3" into candidates, or returns null if any index is invalid.
    private List<AliasCandidate> parseSelection(String input) {
        List<AliasCandidate> selected = new ArrayList<>();

        for (String part : input.split(",")) {
            try {
                int index = Integer.parseInt(part.trim());

                if (index < 1 || index > candidates.size()) {
                    return null;
                }

                selected.add(candidates.get(index - 1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return selected;
    }

    // Renames one candidate before it is appended.
    private void renameAlias(String argument, BufferedReader reader) throws IOException {
        int index;

        try {
            index = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            System.out.println("[!] Usage: e <number>");
            return;
        }

        if (index < 1 || index > candidates.size()) {
            System.out.println("[!] There is no suggestion [" + index + "].");
            return;
        }

        AliasCandidate candidate = candidates.get(index - 1);
        System.out.print("Enter new alias name for [" + index + "] (currently '"
                + candidate.getProposedAlias() + "'): ");

        String newName = reader.readLine();

        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("[i] Name unchanged.");
            return;
        }

        candidate.setProposedAlias(newName.trim());
        System.out.println("[✓] Updated [" + index + "] to: alias "
                + candidate.getProposedAlias() + "='" + candidate.getOriginalCommand() + "'");
    }

    // Writes the selected aliases and prints exactly what was added.
    private void appendAndReport(List<AliasCandidate> selected) {
        scanner.appendAliases(selected);

        System.out.println("[✓] Successfully appended " + selected.size()
                + (selected.size() == 1 ? " alias" : " aliases")
                + " to " + scanner.getBashAliasesPath() + ":");

        for (AliasCandidate candidate : selected) {
            System.out.println("    + alias " + candidate.getProposedAlias()
                    + "='" + candidate.getOriginalCommand() + "'");
        }

        System.out.println();
        System.out.println("[i] To activate immediately in current session, run:");
        System.out.println("    source ~/.bash_aliases");
    }

    // Real-dotfile entry point used by personal runs; demo mode uses AliasGenerator.
    public static void main(String[] args) {
        new AliasEngine().runPipeline();
    }
}
