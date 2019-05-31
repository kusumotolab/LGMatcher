package com.github.kusumotolab.lgmatcher.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.github.kusumotolab.lgmatcher.line.entity.LineDiff;
import com.github.kusumotolab.lgmatcher.line.entity.LineMatch;
import com.github.kusumotolab.lgmatcher.line.entity.MatchInformation;
import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;
import com.github.kusumotolab.lgmatcher.line.entity.Range;

public class MayersDiff {

  public static List<MatchInformation> diff(final String src, final String dst) {
    final List<String> srcList = Arrays.asList(src.split("\n"));
    final List<String> dstList = Arrays.asList(dst.split("\n"));
    return diff(srcList, dstList);
  }

  public static List<MatchInformation> diff(final List<String> srcList, final List<String> dstList) {
    final Patch<String> patch = DiffUtils.diff(srcList, dstList);
    final List<MatchInformation> matchInformations = new ArrayList<>();
    int srcPos = 0, dstPos = 0;

    for (final Delta<String> delta : patch.getDeltas()) {
      // マッチしている行
      final Chunk<String> original = delta.getOriginal();
      final Chunk<String> revised = delta.getRevised();
      while (srcPos < original.getPosition()) {
        srcPos += 1;
        dstPos += 1;
        final LineMatch lineMatch = new LineMatch(srcPos, dstPos);
        matchInformations.add(new MatchInformation(lineMatch));
      }

      // diffが発生した行
      final TYPE type = delta.getType();
      final Range srcRange = type.equals(TYPE.INSERT) ? null : new Range(srcPos + 1, srcPos + original.size());
      final Range dstRange = type.equals(TYPE.DELETE) ? null : new Range(dstPos + 1, dstPos + revised.size());
      final LineDiff lineDiff = new LineDiff(srcRange, dstRange);
      matchInformations.add(new MatchInformation(lineDiff));
      srcPos += original.size();
      dstPos += revised.size();
    }

    // 最後にマッチしている場合
    while (srcPos < srcList.size()) {
      final LineMatch lineMatch = new LineMatch(srcPos, dstPos);
      matchInformations.add(new MatchInformation(lineMatch));
      srcPos += 1;
      dstPos += 1;
    }
    return matchInformations;
  }
}
