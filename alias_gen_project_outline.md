# Project Specification: `alias-gen` (Terminal Workflow Optimizer)

## 1. Project Overview & Philosophy
`alias-gen` is an on-demand Java CLI utility built for terminal power users. It analyzes shell command history (`~/.bash_history` or `~/.zsh_history`), filters out commands that already use or have aliases in `~/.bashrc` or `~/.bash_aliases`, identifies high-utility commands (both frequent repetitive commands and long, painful one-liners), and presents customizable alias suggestions through an interactive terminal interface.

### Core Design Rules
* **Utility-Driven ("Pain Score")**: Ranks commands by typing effort (`Length * Frequency`) and dynamic thresholds, so long commands (e.g., a 90-character `docker run` command run 3 times) bubble to the top alongside short, frequent commands.
* **Strict Deduplication & Active Alias Awareness**: Never suggests an alias for a command already covered, never suggests a name already taken, and skips history lines where the first word is already an active alias (e.g., typing `ll` or `gco`).
* **Interactive TUI Editing**: Allows users to rename proposed aliases (`e <num>`) directly before appending them.
* **Non-destructive**: Only appends explicitly approved aliases to `~/.bash_aliases` (or `~/.bashrc`), preceded by a clear timestamp comment.
* **Self-contained**: Runs entirely on standard Java (JDK 8+) with zero external dependencies, ensuring 100% compatibility with NetBeans and instant execution by course graders.

---

## 2. Terminal UI (TUI) Specification

The interface uses standard ANSI terminal output designed for quick scanning, high readability, and rapid input.

### State 1: Scan & Inspection
```text
========================================================================
                      ALIAS-GEN : SHELL OPTIMIZER                       
========================================================================
[Config] Ingested 18 existing aliases from ~/.bash_aliases
[Scan]   Parsed 3,420 commands from ~/.bash_history
[Filter] 38 distinct candidate commands qualified by Pain Score
------------------------------------------------------------------------
 #   FREQ   PAIN   PROPOSED ALIAS              ORIGINAL COMMAND
------------------------------------------------------------------------
[1]    4x    348   alias dr-pg='docker run...' docker run -d --name pg...
[2]   47x    282   alias scs='sudo systemc...' sudo systemctl status
[3]   31x    248   alias ssh-nas='ssh pi@...'  ssh pi@192.168.1.5 -p 22
[4]   24x    192   alias pacup='sudo pacma...' sudo pacman -Syu
[5]   12x    144   alias gcb='git checkou...'  git checkout -b
------------------------------------------------------------------------
[Actions]
  • Type index numbers separated by commas to append (e.g., "1, 3")
  • Type "e <num>" to rename an alias (e.g., "e 1")
  • Type "a" or "all" to append all suggestions
  • Type "q" to quit without modifying files
------------------------------------------------------------------------
Select: e 1
Enter new alias name for [1] (currently 'dr-pg'): my-db
[✓] Updated [1] to: alias my-db='docker run -d --name pg -p 5432:5432 postgres'

Select: 1, 2
```

### State 2: Confirmation & Handoff
```text
[✓] Successfully appended 2 aliases to /home/user/.bash_aliases:
    + alias my-db='docker run -d --name pg -p 5432:5432 postgres'
    + alias scs='sudo systemctl status'

[i] To activate immediately in current session, run:
    source ~/.bash_aliases
```

### State 3: Edge Case Handling
```text
Select: q
[i] Aborted by user. No files were modified.
```

---

## 3. Algorithm & Heuristics

### A. The "Pain Score" Metric
Instead of simple frequency counting, commands are ranked by the physical typing effort they require:
$$\text{Pain Score} = \text{Length of Command} \times \text{Frequency}$$

To ensure both long commands and frequent short commands are captured, a **Dynamic Threshold** is applied:
* **Short Commands ($< 20$ characters)**: Requires $\ge 6$ runs to qualify.
* **Medium Commands ($20 - 40$ characters)**: Requires $\ge 4$ runs to qualify.
* **Long Commands ($> 40$ characters)**: Requires only $\ge 2$ or $3$ runs to qualify.

### B. Active Alias Filtering
When reading each line of `~/.bash_history`:
1. Extract the first token (`tokens[0]`).
2. If `existingNames.contains(tokens[0])`, immediately **discard** the line. (The user is already using a shortcut).
3. If the entire command is already present in `existingCommands`, immediately **discard** the line.

### C. Name Generation & Collision Handling
1. Generate name candidate via the "Acronym + Target" rule (e.g., `docker run ... postgres` $\rightarrow$ `dr-postgres`).
2. If the generated name exists in `existingNames`, append letters or a numeric index (`dr-pg2`) until unique.
3. Allow the user to override any generated name in the TUI via `e <num>`.

---

## 4. CS 3300 Course Rubric Alignment

| Rubric Item | Project Implementation | Notes / Target |
| :--- | :--- | :--- |
| **IDE Requirement** | Apache NetBeans | Standard Java SE Application |
| **Custom Classes (Min 2)** | 3 Custom Classes: `CommandTrieNode`, `HistoryScanner`, `AliasEngine` | Native Java classes (no 3rd-party libs) |
| **Loop Requirement (Min 1)** | File ingestion `while` loop, ranking & TUI rendering `for` loops | Used for file ingestion & display |
| **Recursive Method (Min 1)** | `collectFrequent(CommandTrieNode node, ...)` in `CommandTrieNode` | Traverses word-level prefix tree |
| **Exception Handling (Min 1)** | `loadExistingAliases()` & `parseHistory()` in `HistoryScanner` | `try...catch` handling `IOException`, `FileNotFoundException` |
| **Line Count (Min 150)** | Estimated ~250–320 lines of clean Java | Well-structured, readable, spaced code |
| **Extra Credit (Up to 30 pts)**| **Trie (Prefix Tree) Data Structure** & heuristic pattern ranking | Advanced data structure not in intro CS |

---

## 5. Architecture & Class Responsibilities

```
+--------------------------------------------------------------------+
|                            AliasEngine                             |
|--------------------------------------------------------------------|
| - scanner: HistoryScanner                                          |
| - rootNode: CommandTrieNode                                        |
| - candidates: List<AliasCandidate>                                 |
|--------------------------------------------------------------------|
| + main(args: String[]): void                                       |
| + runPipeline(): void                                              |
| + renderTUI(): void                                                |
| + generateAliasName(cmd: String): String                           |
| + handleUserInput(): void                                          |
+--------------------------------------------------------------------+
                                   |
          +------------------------+------------------------+
          |                                                 |
          v                                                 v
+------------------------------------+   +------------------------------------+
|           HistoryScanner           |   |          CommandTrieNode           |
|------------------------------------|   |------------------------------------|
| - existingNames: Set<String>       |   | - token: String                    |
| - existingCommands: Set<String>    |   | - frequency: int                   |
| - bashAliasesFile: File            |   | - isEndOfCommand: boolean          |
|------------------------------------|   | - children: Map<String, TrieNode>  |
| + loadExistingAliases(): void      |   |------------------------------------|
| + readHistory(root: TrieNode): int |   | + insert(tokens: String[]): void   |
| + appendAliases(list: List): void  |   | + collectCandidates(...): [REC]    |
+------------------------------------+   +------------------------------------+
```

### 1. `CommandTrieNode.java`
* Models nodes in the word-level prefix tree.
* **Recursive Method (Requirement C)**:
  * `collectCandidates(String currentPath, List<AliasCandidate> results)`: Recursively descends through child branches, calculating the Pain Score and applying the dynamic threshold.

### 2. `HistoryScanner.java`
* Handles file I/O, config detection, and exception handling.
* **Exception Handling Method (Requirement D)**:
  * Uses `try...catch` to safely read `~/.bash_aliases` and `~/.bashrc`.
  * Extracts lines matching `alias <name>=['"]<cmd>['"]`.
  * Filters out history commands whose first token matches an existing alias name.
  * Appends selected aliases in append mode (`new FileWriter(file, true)`).

### 3. `AliasEngine.java` (Main Driver)
* Coordinates the pipeline and drives the interactive TUI.
* Implements the command-line menu loop: supports selecting, appending, renaming (`e <num>`), and quitting.

---

## 6. Research Paper & Presentation Guidelines

### Research Paper (Min 500 words, PDF/.doc, 10% of grade)
* **Section a (Why did you choose this?)**: Discuss terminal productivity on Linux/Arch, avoiding alias name collisions, and identifying long, cumbersome commands automatically.
* **Section b (Computation, Inputs & Outputs)**: 
  * *Inputs*: Lines from `~/.bash_history` and `~/.bash_aliases`.
  * *Computation*: Tokenization, Trie insertion, Pain Score calculation (`Length * Freq`), collision resolution.
  * *Outputs*: Interactive terminal table and formatted bash alias statements appended to disk.
* **Section c (Algorithm)**: Present the ingestion $\rightarrow$ Trie $\rightarrow$ Pain Score ranking $\rightarrow$ TUI interaction pipeline as a clean numbered algorithm.
* **Section d (What you learned)**: Working with Trie data structures, recursion for tree mining, handling real-world dirty shell logs, and defensive file I/O.
* **Section e (Code Navigation Table)**: Line numbers for:
  * 2 Custom Classes: `CommandTrieNode.java`, `HistoryScanner.java`, `AliasEngine.java`
  * 1 Loop: Line numbers in `HistoryScanner.java` and `AliasEngine.java`
  * 1 Recursive Method: `CommandTrieNode.java` (`collectCandidates`)
  * 1 Exception Handling Method: `HistoryScanner.java` (`loadExistingAliases`)
* **Section f (UML Diagram)**: UML diagram reflecting the 3 classes and their relationships.

### Oral Presentation (4–6 minutes)
1. **Introduction (1 min)**: The motivation: finding painful commands and automating alias generation without clobbering existing shortcuts.
2. **Architecture & UML (1 min)**: Show the UML diagram; explain how the Trie stores command tokens hierarchically.
3. **Code Walkthrough (1.5 min)**:
   * Show `collectCandidates()` (base case and recursive call).
   * Show the `try...catch` block in `HistoryScanner`.
   * Point out the line counter verifying 200+ lines.
4. **Live Demonstration (1.5 min)**:
   * Run the project in NetBeans.
   * Highlight a long docker command ranked near the top.
   * Demonstrate renaming it with `e 1`.
   * Save it and run `cat ~/.bash_aliases` in the terminal to prove it appended cleanly.