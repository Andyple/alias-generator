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

## Actions

| Input | Effect |
| :--- | :--- |
| `1, 3` | Append those suggestions |
| `e 1` | Rename suggestion 1 before appending |
| `a` / `all` | Append every suggestion |
| `q` | Quit without modifying files |
