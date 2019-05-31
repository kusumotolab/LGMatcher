package com.github.kusumotolab.lgmatcher.line.entity;

public class Range {

  private final int min;
  private final int max;

  public Range(final int min, final int max) {
    this.min = min;
    this.max = max;
  }

  public boolean contain(final int num) {
    return min <= num && num <= max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}
