# Project Paper: Alias Generator

**Student Name:** Andy Le  
**Course:** CS 3300 — Programming Project  
**Date:** November 11, 2026  

---

## a. Why Did You Choose This Project?

I chose this project to solve a single problem that is easy to ignore, but would save me a ton of time in the long run. My day-to-day work on my computer consists of being in the terminal 75% of the time. In that workflow, I frequently type long and repetitive commands over and over again, such as system service checks, remote SSH connections, and multi-flag Docker commands. While I can manually configure shell aliases, doing so breaks my focus in the middle of a task, and sometimes I honestly just forget the second after I run the command.

This project solves this problem by looking through my terminal history, identifying my most tedious and repetitive commands, and making my workflow less frustrating by automatically suggesting practical aliases to aid me.

---

## b. What Does It Compute? Inputs and Outputs

### Inputs
* **Input 1:** My Bash history file, `~/.bash_history`, where every command I type is stored one line at a time.
* **Input 2:** My existing aliases from `~/.bash_aliases` and `~/.bashrc`, so the program knows which shortcuts and commands I already have.
* **Input 3:** My choices in the terminal: `1, 3` to accept suggestions, `e 1` to rename one, `a` to accept all, or `q` to quit.

### What It Computes
It throws out history lines that are empty, only one word, start with an alias I already have, or are exactly what an alias already runs. Everything else goes into a trie one word at a time, and each full command keeps a count of how often I ran it.

For each command it multiplies the character count by the run count to get a Pain Score. Short commands need 6 runs to qualify, medium ones need 4, and long ones need only 2, since a long command is annoying even when I have only run it twice. It then makes a name from the first letters of the first two words plus the last word, and adds a number if that name is already taken.

### Outputs
* **Console Output:** A table of candidates sorted by Pain Score, showing the frequency, score, suggested alias, and original command, followed by a prompt.
* **File Output:** The aliases I approve, appended to `~/.bash_aliases` under a timestamp comment.

---

## c. Program Operation & Numbered Algorithm

1. **Read Existing Aliases:** `HistoryScanner.loadExistingAliases()` reads `~/.bash_aliases` and `~/.bashrc`, splits every line that begins with `alias` at the `=`, strips the surrounding quotes, and stores the name in `aliasNames` and the expansion in `aliasExpansions`.
2. **Scan History File:** `readHistory()` opens `~/.bash_history`, reads it line by line in a `while` loop, trims each line, and splits it into whitespace-separated tokens.
3. **Filter Active Shortcuts:** Lines that are empty, contain only one word, start with an existing alias name, or exactly match an alias expansion are discarded, so the program never re-suggests a shortcut the user already has.
4. **Build Prefix Tree (Trie):** Each remaining command is inserted token by token; child nodes are created on demand, and the leaf's frequency counter is incremented once per run.
5. **Recursively Extract Frequent/Painful Commands:** `collectCandidates()` walks the trie depth-first and rebuilds the command string as it descends. At every command end it computes the Pain Score (`length × frequency`) and adds the command to the candidate list if it meets the length-based threshold: 6 runs under 20 characters, 4 runs up to 40, and only 2 runs beyond that.
6. **Generate Candidate Alias Names:** `generateAliasName()` combines the initials of the first two non-flag tokens with the last token (`docker run ... postgres` becomes `dr-postgres`), then appends a numeric suffix until the name is not already taken by an existing alias or an earlier suggestion.
7. **Display Interactive TUI:** `renderTUI()` sorts the candidates by descending Pain Score and prints the scan statistics followed by a fixed-width table of frequency, pain score, proposed alias, and original command.
8. **Append Approved Aliases to Disk:** `handleUserInput()` accepts index lists such as `1, 3`, `e <num>` to rename a suggestion, `a` or `all` to accept everything, or `q` to quit. Approved aliases are appended to `~/.bash_aliases` under a timestamp comment, with single quotes escaped so the file remains valid Bash.

---

## d. What Have You Learned From This Project?

Before this project I had never built a trie. What surprised me was that a node can end one command and still lead to longer ones, so `git` and `git status` share a branch without interfering with each other. Recursion turned out to be the simplest way to walk every branch, rebuild the command as I go, and count each full command exactly once.

I also learned that a terminal tool is mostly about trust. Showing the frequency and pain score lets me see why a command ranked high instead of taking the program's word for it, and letting me rename a suggestion before it is written makes the result feel like mine. The biggest lesson was to make file changes boring: preview first, write only what I approve, and change nothing when I quit.

---

## e. Code Requirements Navigation Table

| Requirement | File Name | Method / Location | Line Number(s) |
| :--- | :--- | :--- | :--- |
| **Custom Class 1** | `CommandTrieNode.java` | Class definition | Line 8 |
| **Custom Class 2** | `HistoryScanner.java` | Class definition | Line 16 |
| **Custom Class 3** | `AliasEngine.java` | Class definition | Line 10 |
| **Supporting Class** | `AliasCandidate.java` | Class definition | Line 4 |
| **Entry Point** | `AliasGenerator.java` | Class definition | Line 9 |
| **Loop Requirement** | `HistoryScanner.java` | `while` loop in `readHistory()` reading history lines | Line 81 (loop body 81–104) |
| **Loop Requirement (2)** | `HistoryScanner.java` | `while` loop in `loadAliasesFrom()` reading alias files | Line 43 |
| **Recursive Method** | `CommandTrieNode.java` | `collectCandidates()` definition | Line 35; recursive call on line 46 |
| **Exception Handling**| `HistoryScanner.java` | `loadExistingAliases()` → `loadAliasesFrom()` `try...catch` | Method line 34; `try` line 40; catches lines 67 and 69 |
| **Exception Handling (2)**| `HistoryScanner.java` | `readHistory()` `try...catch` | Method line 75; `try` line 78; catches lines 105 and 107 |

---

## f. UML Class Diagram

```text
+----------------------------------+     +----------------------------------+
|          AliasGenerator          |     |          HistoryScanner          |
+----------------------------------+     +----------------------------------+
| + DEMO_MODE: boolean             |     | - aliasNames: Set<String>        |
+----------------------------------+     | - aliasExpansions: Set<String>   |
| + main(args: String[]): void     |     | - historyFile: File              |
| - runDemo(): void                |     | - bashAliasesFile: File          |
| - copyDemoFile(...): File        |     | - bashRcFile: File               |
+----------------------------------+     +----------------------------------+
                                         | + loadExistingAliases(): void    |
                 | calls                 | + readHistory(root): int         |
                 v                       | + appendAliases(list): void      |
                                         | + getHistoryPath(): String       |
+----------------------------------+     | + getBashAliasesPath(): String   |
|           AliasEngine            |     +----------------------------------+
+----------------------------------+
| - scanner: HistoryScanner        |     +----------------------------------+
| - rootNode: CommandTrieNode      |     |         CommandTrieNode          |
| - candidates: List<AliasCand.>   |     +----------------------------------+
| - scannedCount: int              |     | - token: String                  |
+----------------------------------+     | - frequency: int                 |
| + runPipeline(): void            |     | - isEndOfCommand: boolean        |
| - prepare(): void                |     | - children: Map<String, Node>    |
| - renderTUI(): void              |     +----------------------------------+
| - generateAliasName(cmd): String |     | + insert(tokens): void           |
| - handleUserInput(): void        |     | + collectCandidates(...): void   |
| + main(args: String[]): void     |     +----------------------------------+
+----------------------------------+

+----------------------------------+
|          AliasCandidate          |
+----------------------------------+
| - originalCommand: String        |
| - proposedAlias: String          |
| - frequency: int                 |
| - painScore: int                 |
+----------------------------------+
| + getters, setProposedAlias      |
+----------------------------------+

Relationships:
  AliasGenerator   ..>  AliasEngine          calls
  AliasGenerator   ..>  HistoryScanner       creates the demo scanner
  AliasEngine      *--> HistoryScanner       1
  AliasEngine      *--> CommandTrieNode      1  (root)
  AliasEngine      o--> AliasCandidate       0..*
  HistoryScanner   ..>  CommandTrieNode      insert() for every parsed line
  CommandTrieNode  o--> CommandTrieNode      0..*  (children map)
  CommandTrieNode  ..>  AliasCandidate       creates candidates while traversing
```

---

## g. Works Cited / References

1. Oracle. "Java Platform, Standard Edition Documentation." *Oracle Corporation*, 2021.
2. Sedgewick, Robert, and Kevin Wayne. *Algorithms*. 4th ed., Addison-Wesley, 2011. (Trie Structures).
3. Free Software Foundation. "Bash Reference Manual." *GNU Project*, 2022.
