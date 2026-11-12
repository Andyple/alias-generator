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

[Write your rationale here...]



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

1. **Read Existing Aliases:** 
2. **Scan History File:** 
3. **Filter Active Shortcuts:** 
4. **Build Prefix Tree (Trie):** 
5. **Recursively Extract Frequent/Painful Commands:** 
6. **Generate Candidate Alias Names:** 
7. **Display Interactive TUI:** 
8. **Append Approved Aliases to Disk:** 



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
