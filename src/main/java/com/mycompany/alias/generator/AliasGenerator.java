package com.mycompany.alias.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

// Demo entry point: copies bundled dotfiles into a temp home, then runs the normal TUI.
public class AliasGenerator {
    public static final boolean DEMO_MODE = true;

    public static void main(String[] args) {
        if (!DEMO_MODE) {
            AliasEngine.main(args);
            return;
        }

        try {
            runDemo();
        } catch (IOException e) {
            System.err.println("[warn] Could not prepare demo: " + e.getMessage());
        }
    }

    // Creates an isolated home and copies the three demo dotfiles into it.
    private static void runDemo() throws IOException {
        File home = Files.createTempDirectory("alias-gen-demo-").toFile();

        File history = copyDemoFile(home, "bash_history", ".bash_history");
        File aliases = copyDemoFile(home, "bash_aliases", ".bash_aliases");
        File bashrc = copyDemoFile(home, "bashrc", ".bashrc");

        System.out.println("[Demo] Temporary home: " + home.getAbsolutePath());
        System.out.println("[Demo] Your real dotfiles are not touched.");
        System.out.println();

        new AliasEngine(new HistoryScanner(history, aliases, bashrc)).runPipeline();
    }

    // Copies one bundled resource into the demo home.
    private static File copyDemoFile(File home, String resource, String fileName)
            throws IOException {
        File target = new File(home, fileName);

        try (InputStream input = AliasGenerator.class.getResourceAsStream("/demo/" + resource)) {
            if (input == null) {
                throw new IOException("Missing resource /demo/" + resource);
            }

            Files.copy(input, target.toPath());
        }

        return target;
    }
}
