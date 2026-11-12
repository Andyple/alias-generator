/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.alias.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 *
 * @author andy
 */
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

    private static void runDemo() throws IOException {
        File home = Files.createTempDirectory("alias-gen-demo-").toFile();

        File history = copyDemoFile(home, "bash_history", ".bash_history");
        File aliases = copyDemoFile(home, "bash_aliases", ".bash_aliases");
        File bashrc = copyDemoFile(home, "bashrc", ".bashrc");

        System.out.println("[Demo] Temporary home: " + home.getAbsolutePath());
        System.out.println("[Demo] Your real dotfiles are not touched.");
        System.out.println();

        HistoryScanner scanner = new HistoryScanner(history, aliases, bashrc);
        new AliasEngine(scanner).runPipeline();
    }

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
