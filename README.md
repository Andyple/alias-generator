# alias-gen

A Java CLI that reads `~/.bash_history`, ranks commands by typing effort
(`Pain Score = length × frequency`), and appends the aliases you approve to
`~/.bash_aliases`.

## Run

```bash
javac -d target/classes $(find src/main/java -name '*.java')
java -cp target/classes com.mycompany.alias.generator.AliasGenerator
```

Or open `pom.xml` in NetBeans and run the project.

## Try it on the sample data

The files in `fixtures/home` stand in for your home directory:

```bash
cp -r fixtures/home /tmp/alias-gen-demo
java -Duser.home=/tmp/alias-gen-demo -cp target/classes com.mycompany.alias.generator.AliasGenerator
```

Use the copy rather than `fixtures/home` directly, since approved aliases are
written to the `.bash_aliases` under the chosen home directory.

## Running in NetBeans

1. **File → Open Project** and select the folder containing `pom.xml`.
2. Right-click the project → **Properties → Libraries** and set **Java Platform**
   to a JDK 27 (the pom targets release 27). Add it under **Tools → Java
   Platforms** if it is missing.
3. Right-click the project → **Properties → Run**:
   - **Main Class**: `com.mycompany.alias.generator.AliasGenerator`
   - **VM Options**: `-Duser.home=/tmp/alias-gen-demo`
4. Press **F6** to run.

NetBeans' Output window is read-only, so the interactive prompts cannot be
answered there. Use **Window → IDE Tools → Terminal** (Ctrl+4) instead, after
running the project once so the classes are compiled:

```bash
java -Duser.home=/tmp/alias-gen-demo -cp target/classes com.mycompany.alias.generator.AliasGenerator
```

If your NetBeans version offers **Run in External Terminal** under
**Properties → Run**, enabling it makes F6 open a terminal where input works.

## Actions

| Input | Effect |
| :--- | :--- |
| `1, 3` | Append those suggestions |
| `e 1` | Rename suggestion 1 before appending |
| `a` / `all` | Append every suggestion |
| `q` | Quit without modifying files |
