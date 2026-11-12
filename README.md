# alias-gen

A Java CLI that reads `~/.bash_history`, ranks commands by typing effort
(`Pain Score = length × frequency`), and appends the aliases you approve to
`~/.bash_aliases`.

## Demo mode (default)

`AliasGenerator.DEMO_MODE` is `true` in the submitted copy. Press **Run** in
NetBeans (F6) and the program:

1. Creates a fresh temporary home and copies the bundled demo files into it.
2. Prints the temp path and the normal candidate table.
3. Runs the normal interactive TUI against those files.

Type the usual commands, for example:

```text
Select: e 1
Enter new alias name for [1] (currently 'dr-postgres'): my-db
Select: a
```

Your real dotfiles are never read or written. Each run gets a new temp home, so
repeated runs start clean. Typed input works in the NetBeans Output window.

The demo data lives in `src/main/resources/demo/` as `bash_history`,
`bash_aliases`, and `bashrc`.

## Real mode

Set `DEMO_MODE` to `false` in
`src/main/java/com/mycompany/alias/generator/AliasGenerator.java`:

```java
public static final boolean DEMO_MODE = false;
```

Then the program reads your real `~/.bash_history`, `~/.bash_aliases`, and
`~/.bashrc`. The prompts and commands are identical to demo mode; only the
files differ:

| Input | Effect |
| :--- | :--- |
| `1, 3` | Append those suggestions |
| `e 1` | Rename suggestion 1 before appending |
| `a` / `all` | Append every suggestion |
| `q` | Quit without modifying files |

## Run from a terminal

Requires JDK 17 or newer.

```bash
javac -d target/classes $(find src/main/java -name '*.java')
cp -r src/main/resources/demo target/classes/
java -cp target/classes com.mycompany.alias.generator.AliasGenerator
```

The `cp` line is only needed for a manual demo run: `javac` does not copy
classpath resources. Maven and NetBeans do that automatically.

## Running in NetBeans

1. **File → Open Project** and select the folder containing `pom.xml`.
2. Make sure a JDK 17+ platform is available under **Tools → Java Platforms**.
3. Press **F6** to run, then type your choices in the Output window.

The project targets Java 17 (`maven.compiler.release` in `pom.xml`), so it
builds on any current JDK and does not require JDK 27.

## Testing against the fixtures

To run the interactive TUI against `fixtures/home` without touching your
dotfiles, bypass demo mode and run the engine directly:

```bash
rm -rf /tmp/alias-gen-demo
cp -r fixtures/home/. /tmp/alias-gen-demo/
java -Duser.home=/tmp/alias-gen-demo -cp target/classes com.mycompany.alias.generator.AliasEngine
```

Using `fixtures/home/.` copies the files directly into the destination, so the
program finds `.bash_history` and `.bash_aliases` on the first try.
