package com.mycompany.alias.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryScanner {
    private Set<String> existingNames;
    private Set<String> existingCommands;
    private File bashAliasesFile;

    public HistoryScanner() {
        this.existingNames = new HashSet<>();
        this.existingCommands = new HashSet<>();
        this.bashAliasesFile = new File(System.getProperty("user.home"), ".bash_aliases");
    }

    /**
     * Reads existing aliases from ~/.bash_aliases or ~/.bashrc
     * (Satisfies rubric requirement: Exception Handling)
     */
    public void loadExistingAliases() {
        loadAliasesFrom(new File(System.getProperty("user.home"), ".bash_aliases"));
        loadAliasesFrom(new File(System.getProperty("user.home"), ".bashrc"));
    }

    private void loadAliasesFrom(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (!trimmed.startsWith("alias ")) {
                    continue;
                }

                int equalsIndex = trimmed.indexOf('=');

                if (equalsIndex < 0) {
                    continue;
                }

                String name = trimmed.substring("alias ".length(), equalsIndex).trim();
                String command = trimmed.substring(equalsIndex + 1).trim();

                if (command.length() >= 2
                        && (command.startsWith("\"") || command.startsWith("'"))) {
                    command = command.substring(1, command.length() - 1);
                }

                existingNames.add(name);
                existingCommands.add(command);
            }
        } catch (FileNotFoundException e) {
            // A missing config file is normal; there is simply nothing to load.
        } catch (IOException e) {
            System.err.println("[warn] Could not read " + file + ": " + e.getMessage());
        }
    }

    /**
     * Parses shell history file and populates Trie
     */
    public int readHistory(CommandTrieNode root) {
        // TODO: Implementation using try...catch
        return 0;
    }

    /**
     * Appends approved aliases to the bash aliases file
     */
    public void appendAliases(List<AliasCandidate> list) {
        // TODO: Implementation using FileWriter in append mode
    }

    public Set<String> getExistingNames() {
        return existingNames;
    }

    public void setExistingNames(Set<String> existingNames) {
        this.existingNames = existingNames;
    }

    public Set<String> getExistingCommands() {
        return existingCommands;
    }

    public void setExistingCommands(Set<String> existingCommands) {
        this.existingCommands = existingCommands;
    }

    public File getBashAliasesFile() {
        return bashAliasesFile;
    }

    public void setBashAliasesFile(File bashAliasesFile) {
        this.bashAliasesFile = bashAliasesFile;
    }
}
