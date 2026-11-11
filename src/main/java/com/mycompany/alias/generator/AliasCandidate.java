package com.mycompany.alias.generator;

public class AliasCandidate {
  private String originalCommand;
  private String proposedAlias;
  private int frequency;
  private int painScore;

  public AliasCandidate(String originalCommand, int frequency, int painScore) {
    this.originalCommand = originalCommand;
    this.frequency = frequency;
    this.painScore = painScore;
  }

  public String getOriginalCommand() {
    return originalCommand;
  }

  public void setOriginalCommand(String originalCommand) {
    this.originalCommand = originalCommand;
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

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public int getPainScore() {
    return painScore;
  }

  public void setPainScore(int painScore) {
    this.painScore = painScore;
  }
}
