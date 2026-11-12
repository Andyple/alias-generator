package com.mycompany.alias.generator;

// One command from history and the alias name suggested for it.
public class AliasCandidate {
    private final String originalCommand;
    private final int frequency;
    private final int painScore;
    private String proposedAlias;

    public AliasCandidate(String originalCommand, int frequency, int painScore) {
        this.originalCommand = originalCommand;
        this.frequency = frequency;
        this.painScore = painScore;
    }

    public String getOriginalCommand() {
        return originalCommand;
    }

    public String getProposedAlias() {
        return proposedAlias;
    }

    public void setProposedAlias(String proposedAlias) {
        this.proposedAlias = proposedAlias;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getPainScore() {
        return painScore;
    }
}
