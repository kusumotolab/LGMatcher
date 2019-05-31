package com.github.kusumotolab.lgmatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.kusumotolab.lgmatcher.line.entity.Range;

public class FilePosConverter {

  private final ArrayList<Range> ranges = new ArrayList<>();
  private final Map<Integer, Integer> cacheMap = new HashMap<>();

  public FilePosConverter(final List<String> lines) {
    setUp(lines);
  }

  private void setUp(final List<String> lines) {
    int pos = 0;
    for (final String line : lines) {
      final Range range = new Range(pos, pos + line.length());
      ranges.add(range);
      pos += line.length() + 1;
    }
  }

  public int toLineNumber(final int pos) {
    final Integer cache = cacheMap.get(pos);
    if (cache != null) return cache;

    for (int i = 0; i < ranges.size(); i++) {
      final Range range = ranges.get(i);
      if (range.contain(pos)) {
        cacheMap.put(pos, i + 1);
        return i + 1;
      }
    }
    throw new RuntimeException("Over position.");
  }
}
