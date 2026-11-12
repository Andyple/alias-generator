# Project Paper: [Your Project Title Here]

**Student Name:** [Your Name]  
**Course:** CS 3300 — Programming Project  
**Date:** November 11, 2026  

---

## a. Why Did You Choose This Project?
&lt;!-- Target: ~100–120 words --&gt;
&lt;!-- Prompts to answer:
  - What problem does this solve in your daily computing/terminal workflow?
  - Why is typing long administrative, container, or SSH commands frustrating?
  - Why did you want to build this instead of a standard tutorial or game?
--&gt;

I chose this project to solve a single problem that is easy to ignore, but would save me a ton of time in the long run. My day-to-day work on my computer consists of being in the terminal 75% of the time. In that workflow, I frequently type long and repetitive commands over and over again, such as system service checks, remote SSH connections, and multi-flag Docker commands. While I can manually configure shell aliases, doing so breaks my focus in the middle of a task, and sometimes I honestly just forget the second after I run the command.

This project solves this problem by looking through my terminal history, identifying my most tedious and repetitive commands, and making my workflow less frustrating by automatically suggesting practical aliases to aid me.



---

## b. What Does It Compute? Inputs and Outputs
&lt;!-- Target: ~120–150 words --&gt;

### Inputs
&lt;!-- List all inputs your program reads:
  - Shell history file path (~/.bash_history or ~/.zsh_history)
  - Existing alias configs (~/.bashrc or ~/.bash_aliases)
  - User terminal selections/inputs (e.g., typing numbers or &#39;e 1&#39;)
--&gt;
* **Input 1:** 
* **Input 2:** 
* **Input 3:** 

### What It Computes
&lt;!-- Explain the calculations and filtering logic:
  - How it filters out already-aliased commands
  - How it counts command frequencies using the Trie structure
  - How it calculates the Pain Score (Length * Frequency)
  - How it creates a shorthand name without colliding with existing aliases
--&gt;
* [Explain the core computations here...]

### Outputs
&lt;!-- List what the program produces:
  - The formatted terminal table (frequencies, pain scores, suggestions)
  - The updated ~/.bash_aliases file with appended alias lines
--&gt;
* **Console Output:** 
* **File Output:** 



---

## c. Program Operation & Numbered Algorithm
&lt;!-- Target: ~150 words --&gt;
&lt;!-- The prompt allows this to be a numbered list instead of paragraphs. --&gt;

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
&lt;!-- Target: ~100–120 words --&gt;
&lt;!-- Prompts to answer:
  - What did you learn about implementing a custom tree/Trie structure in Java?
  - What did you learn about recursion and tree traversal?
  - How did writing try...catch blocks help with reading real-world dirty files?
  - What did you learn about designing practical CLI/TUI tools?
--&gt;

[Write what you learned here...]



---

## e. Code Requirements Navigation Table
&lt;!-- Fill in the exact line numbers from your NetBeans project before exporting to PDF. --&gt;

| Requirement | File Name | Method / Location | Line Number(s) |
| :--- | :--- | :--- | :--- |
| **Custom Class 1** | `CommandTrieNode.java` | Class definition | Line 8 |
| **Custom Class 2** | `HistoryScanner.java` | Class definition | Line 15 |
| **Custom Class 3** | `AliasEngine.java` | Class definition | Line 9 |
| **Supporting Class** | `AliasCandidate.java` | Class definition | Line 3 |
| **Entry Point** | `AliasGenerator.java` | Class definition | Line 11 |
| **Loop Requirement** | `HistoryScanner.java` | `while` loop in `readHistory()` reading history lines | Line 80 (loop body 80–102) |
| **Loop Requirement (2)** | `HistoryScanner.java` | `while` loop in `loadAliasesFrom()` reading alias files | Line 39 |
| **Recursive Method** | `CommandTrieNode.java` | `collectCandidates()` definition | Line 50; recursive call on line 65 |
| **Exception Handling**| `HistoryScanner.java` | `loadExistingAliases()` → `loadAliasesFrom()` `try...catch` | Method line 30; `try` line 36; catches lines 63 and 65 |
| **Exception Handling (2)**| `HistoryScanner.java` | `readHistory()` `try...catch` | Method line 73; `try` line 77; catches lines 105 and 107 |

---

## f. UML Class Diagram

```text
+----------------------------------+     +----------------------------------+
|          AliasGenerator          |     |         HistoryScanner           |
|----------------------------------|     |----------------------------------|
| + main(args: String[]): void     |     | - aliasNames: Set<String>        |
+----------------------------------+     | - aliasExpansions: Set<String>   |
                 | calls                | - bashAliasesFile: File          |
                 v                      |----------------------------------|
+----------------------------------+     | + loadExistingAliases(): void    |
|            AliasEngine           |     | + readHistory(root): int         |
|----------------------------------|     | + appendAliases(list): void      |
| - scanner: HistoryScanner        |     +----------------------------------+
| - rootNode: CommandTrieNode      |
| - candidates: List<AliasCand.>   |     +----------------------------------+
| - scannedCount: int              |     |         CommandTrieNode          |
|----------------------------------|     |----------------------------------|
| + runPipeline(): void            |     | - token: String                  |
| + renderTUI(): void              |     | - frequency: int                 |
| + generateAliasName(cmd): String |     | - isEndOfCommand: boolean        |
| + handleUserInput(): void        |     | - children: Map<String, Node>    |
| + main(args: String[]): void     |     |----------------------------------|
+----------------------------------+     | + insert(tokens): void           |
                                         | + collectCandidates(...): void   |
                                         +----------------------------------+

+----------------------------------+
|          AliasCandidate          |
|----------------------------------|
| - originalCommand: String        |
| - proposedAlias: String          |
| - frequency: int                 |
| - painScore: int                 |
|----------------------------------|
| + getters / setters              |
+----------------------------------+

Relationships:
  AliasGenerator   ..>  AliasEngine          calls
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
