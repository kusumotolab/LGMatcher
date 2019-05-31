package com.github.kusumotolab.lgmatcher.line.entity;

public class LineDiff {

  private final Range srcRange;
  private final Range dstRange;

  public LineDiff(final Range srcRange,
      final Range dstRange) {
    this.srcRange = srcRange;
    this.dstRange = dstRange;
  }

  public Range getSrcRange() {
    return srcRange;
  }

  public Range getDstRange() {
    return dstRange;
  }
}
