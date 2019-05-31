package com.github.kusumotolab.lgmatcher.line.entity;

public class MatchInformation {

  private LineDiff diff;
  private LineMatch match;

  public MatchInformation(final LineDiff diff) {
    this.diff = diff;
  }

  public MatchInformation(final LineMatch match) {
    this.match = match;
  }

  public boolean isDiff() {
    return diff != null;
  }

  public LineDiff getDiff() {
    return diff;
  }

  public LineMatch getMatch() {
    return match;
  }
}
