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
| **Custom Class 1** | `CommandTrieNode.java` | Class definition | Line _____ |
| **Custom Class 2** | `HistoryScanner.java` | Class definition | Line _____ |
| **Custom Class 3** | `AliasEngine.java` | Class definition | Line _____ |
| **Loop Requirement** | `HistoryScanner.java` | `while` / `for` loop reading lines | Line _____ |
| **Recursive Method** | `CommandTrieNode.java` | `collectCandidates()` method | Line _____ |
| **Exception Handling**| `HistoryScanner.java` | `loadExistingAliases()` `try...catch` block | Line _____ |

---

## f. UML Class Diagram

```text
+------------------------------------+
|            AliasEngine             |
|------------------------------------|
| - scanner: HistoryScanner          |
| - rootNode: CommandTrieNode        |
|------------------------------------|
| + main(args: String[]): void       |
| + run(): void                      |
| + renderTUI(): void                |
+------------------------------------+
           |                  |
           v                  v
+--------------------+  +--------------------+
|   HistoryScanner   |  |  CommandTrieNode   |
|--------------------|  |--------------------|
| - existingNames    |  | - token: String    |
| - existingCommands |  | - frequency: int   |
|--------------------|  | - children: Map    |
| + loadAliases()    |  |--------------------|
| + parseHistory()   |  | + insert()         |
| + appendAliases()  |  | + collect() [REC]  |
+--------------------+  +--------------------+
```

---

## g. Works Cited / References

1. Oracle. "Java Platform, Standard Edition Documentation." *Oracle Corporation*, 2021.
2. Sedgewick, Robert, and Kevin Wayne. *Algorithms*. 4th ed., Addison-Wesley, 2011. (Trie Structures).
3. Free Software Foundation. "Bash Reference Manual." *GNU Project*, 2022.
