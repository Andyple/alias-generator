package com.mycompany.alias.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AliasEngine {
    private HistoryScanner scanner;
    private CommandTrieNode rootNode;
    private List<AliasCandidate> candidates;
    private int scannedCount;

    public AliasEngine() {
        this(new HistoryScanner());
    }

    public AliasEngine(HistoryScanner scanner) {
        this.scanner = scanner;
        this.rootNode = new CommandTrieNode();
        this.candidates = new ArrayList<>();
    }

    public void runPipeline() {
        prepare();
        renderTUI();
        handleUserInput();
    }

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

    public void runDemo() {
        prepare();
        renderTUI();

        if (candidates.isEmpty()) {
            return;
        }

        System.out.print("Select: ");
        System.out.println("e 1");
        System.out.print(renamePrompt(1));
        System.out.println("my-db");
        applyRename(1, "my-db");

        System.out.print("Select: ");
        System.out.println("a");
        appendAndReport(candidates);

        System.out.println();
        System.out.println("[Verify] New aliases in a fresh bash shell:");
        verifyAliases(candidates);
    }

    public void renderTUI() {
        System.out.println("========================================================================");
        System.out.println("                      ALIAS-GEN : SHELL OPTIMIZER");
        System.out.println("========================================================================");
        System.out.println("[Config] Ingested " + scanner.getAliasNames().size()
                + " existing aliases from " + scanner.getBashAliasesPath());
        System.out.println("[Scan]   Parsed " + String.format("%,d", scannedCount)
                + " commands from " + scanner.getHistoryPath());
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
                    System.out.println("[!] Unrecognized input. Enter index numbers, 'e <num>', 'a', or 'q'.");
                    continue;
                }

                appendAndReport(selected);
                return;
            }
        } catch (IOException e) {
            System.err.println("[warn] Could not read input: " + e.getMessage());
        }
    }

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

        return selected.isEmpty() ? null : selected;
    }

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

        System.out.print(renamePrompt(index));

        String newName = reader.readLine();
        applyRename(index, newName);
    }

    private String renamePrompt(int index) {
        AliasCandidate candidate = candidates.get(index - 1);

        return "Enter new alias name for [" + index + "] (currently '"
                + candidate.getProposedAlias() + "'): ";
    }

    private void applyRename(int index, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("[i] Name unchanged.");
            return;
        }

        AliasCandidate candidate = candidates.get(index - 1);
        candidate.setProposedAlias(newName.trim());
        System.out.println("[✓] Updated [" + index + "] to: alias "
                + candidate.getProposedAlias() + "='" + candidate.getOriginalCommand() + "'");
    }

    private void appendAndReport(List<AliasCandidate> selected) {
        scanner.appendAliases(selected);

        System.out.println("[✓] Successfully appended " + selected.size()
                + (selected.size() == 1 ? " alias" : " aliases")
                + " to " + scanner.getBashAliasesFile() + ":");

        for (AliasCandidate candidate : selected) {
            System.out.println("    + alias " + candidate.getProposedAlias()
                    + "='" + candidate.getOriginalCommand() + "'");
        }

        System.out.println();
        System.out.println("[i] To activate immediately in current session, run:");
        System.out.println("    source ~/.bash_aliases");
    }

    private void verifyAliases(List<AliasCandidate> selected) {
        File aliasesFile = scanner.getBashAliasesFile();
        File home = aliasesFile.getParentFile();
        Path script = null;

        try {
            script = Files.createTempFile("alias-gen-verify-", ".sh");

            StringBuilder content = new StringBuilder();
            content.append("source '").append(aliasesFile.getAbsolutePath())
                    .append("'").append(System.lineSeparator());

            for (AliasCandidate candidate : selected) {
                content.append("type '").append(candidate.getProposedAlias())
                        .append("'").append(System.lineSeparator());
            }

            Files.writeString(script, content.toString());

            ProcessBuilder builder = new ProcessBuilder("bash", "-i", script.toString());
            builder.environment().put("HOME", home.getAbsolutePath());
            builder.redirectErrorStream(true);

            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(" is aliased to ")) {
                        System.out.println("    " + line);
                    }
                }
            }

            process.waitFor();
        } catch (IOException e) {
            System.err.println("[warn] Could not verify aliases: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (script != null) {
                try {
                    Files.deleteIfExists(script);
                } catch (IOException e) {
                    // A leftover temp script is harmless.
                }
            }
        }
    }

    public static void main(String[] args) {
        new AliasEngine().runPipeline();
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
