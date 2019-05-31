package com.github.kusumotolab.lgmatcher.line.entity;

public class LineMatch {

  private final int src;
  private final int dst;

  public LineMatch(final int src, final int dst) {
    this.src = src;
    this.dst = dst;
  }

  public int getSrc() {
    return src;
  }

  public int getDst() {
    return dst;
  }
}
