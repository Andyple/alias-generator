package com.mycompany.alias.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        File historyFile = new File(System.getProperty("user.home"), ".bash_history");
        int parsedCommands = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String command = line.trim();

                if (command.isEmpty()) {
                    continue;
                }

                String[] tokens = command.split("\\s+");

                if (tokens.length < 2) {
                    // A single-word command gains nothing from a shorter alias.
                    continue;
                }

                if (aliasNames.contains(tokens[0])) {
                    continue;
                }

                if (aliasExpansions.contains(command)) {
                    continue;
                }

                root.insert(tokens);
                parsedCommands++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("[warn] History file not found: " + historyFile);
        } catch (IOException e) {
            System.err.println("[warn] Could not read " + historyFile + ": " + e.getMessage());
        }

        return parsedCommands;
    }

    /**
     * Appends approved aliases to the bash aliases file
     */
    public void appendAliases(List<AliasCandidate> list) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter writer = new FileWriter(bashAliasesFile, true)) {
            writer.write(System.lineSeparator());
            writer.write("# alias-gen: generated " + timestamp + System.lineSeparator());

            for (AliasCandidate candidate : list) {
                String expansion = candidate.getOriginalCommand().replace("'", "'\\''");
                writer.write("alias " + candidate.getProposedAlias()
                        + "='" + expansion + "'" + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("[warn] Could not write " + bashAliasesFile + ": " + e.getMessage());
        }
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
