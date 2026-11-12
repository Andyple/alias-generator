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

// Reads dotfiles and writes approved aliases back to ~/.bash_aliases.
public class HistoryScanner {
    private final Set<String> aliasNames = new HashSet<>();
    private final Set<String> aliasExpansions = new HashSet<>();
    private final File historyFile;
    private final File bashAliasesFile;
    private final File bashRcFile;

    public HistoryScanner() {
        this(defaultFile(".bash_history"), defaultFile(".bash_aliases"), defaultFile(".bashrc"));
    }

    public HistoryScanner(File historyFile, File bashAliasesFile, File bashRcFile) {
        this.historyFile = historyFile;
        this.bashAliasesFile = bashAliasesFile;
        this.bashRcFile = bashRcFile;
    }

    // Loads aliases from ~/.bash_aliases and ~/.bashrc so they are never suggested again.
    public void loadExistingAliases() {
        loadAliasesFrom(bashAliasesFile);
        loadAliasesFrom(bashRcFile);
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

    // Reads history into the trie, skipping commands that cannot benefit from an alias.
    public int readHistory(CommandTrieNode root) {
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

    // Appends approved aliases under a timestamp, escaping single quotes for Bash.
    public void appendAliases(List<AliasCandidate> aliases) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter writer = new FileWriter(bashAliasesFile, true)) {
            writer.write(System.lineSeparator());
            writer.write("# alias-gen: generated " + timestamp + System.lineSeparator());

            for (AliasCandidate candidate : aliases) {
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

    public String getHistoryPath() {
        return displayPath(historyFile);
    }

    public String getBashAliasesPath() {
        return displayPath(bashAliasesFile);
    }

    private static File defaultFile(String fileName) {
        return new File(System.getProperty("user.home"), fileName);
    }

    // Shows paths inside the home directory as ~/file instead of /home/user/file.
    private static String displayPath(File file) {
        String home = System.getProperty("user.home");
        String path = file.getAbsolutePath();

        if (path.startsWith(home + File.separator)) {
            return "~" + path.substring(home.length());
        }

        return path;
    }
}
