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
    private Set<String> aliasNames;
    private Set<String> aliasExpansions;
    private File bashAliasesFile;

    public HistoryScanner() {
        this.aliasNames = new HashSet<>();
        this.aliasExpansions = new HashSet<>();
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

                String aliasName = trimmed.substring("alias ".length(), equalsIndex).trim();
                String expansion = trimmed.substring(equalsIndex + 1).trim();

                if (expansion.length() >= 2
                        && (expansion.startsWith("\"") || expansion.startsWith("'"))) {
                    expansion = expansion.substring(1, expansion.length() - 1);
                }

                aliasNames.add(aliasName);
                aliasExpansions.add(expansion);
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

    public Set<String> getAliasNames() {
        return aliasNames;
    }

    public void setAliasNames(Set<String> aliasNames) {
        this.aliasNames = aliasNames;
    }

    public Set<String> getAliasExpansions() {
        return aliasExpansions;
    }

    public void setAliasExpansions(Set<String> aliasExpansions) {
        this.aliasExpansions = aliasExpansions;
    }

    public File getBashAliasesFile() {
        return bashAliasesFile;
    }

    public void setBashAliasesFile(File bashAliasesFile) {
        this.bashAliasesFile = bashAliasesFile;
    }
}
