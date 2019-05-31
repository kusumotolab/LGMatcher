package com.github.kusumotolab.lgmatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedyBottomUpMatcher;
import com.github.gumtreediff.tree.ITree;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.github.kusumotolab.lgmatcher.line.MayersDiff;
import com.github.kusumotolab.lgmatcher.line.entity.LineDiff;
import com.github.kusumotolab.lgmatcher.line.entity.LineMatch;
import com.github.kusumotolab.lgmatcher.line.entity.MatchInformation;
import com.github.kusumotolab.lgmatcher.line.entity.Range;

public class LineMatcher extends Matcher {

  private final String srcContent;
  private final String dstContent;
  private final FilePosConverter srcPosConverter;
  private final FilePosConverter dstPosConverter;

  public LineMatcher(final String srcContent, final String dstContent, final ITree srcTree,
      final ITree dstTree, final MappingStore mappings) {
    super(srcTree, dstTree, mappings);

    this.srcContent = srcContent;
    this.dstContent = dstContent;
    srcPosConverter = new FilePosConverter(Arrays.asList(srcContent.split("\n")));
    dstPosConverter = new FilePosConverter(Arrays.asList(dstContent.split("\n")));
  }

  @Override
  public void match() {
    final List<MatchInformation> informationList = MayersDiff.diff(srcContent, dstContent);

    final Map<Integer, MatchInformation> lineToInformation = createLineToInformation(
        informationList);

    final Multimap<Integer, ITree> dstLineToTree = createLineToTree(dst, dstPosConverter);

    final Map<Range, Range> diffMap = createDiffMap(informationList);
    final Multimap<Range, ITree> rangeToTree = createRangeToTree(
        Lists.newArrayList(diffMap.values()), dst, dstPosConverter);

    for (final ITree descendant : src.getDescendants()) {
      final int startSrcLineNumber = srcPosConverter.toLineNumber(descendant.getPos());
      final MatchInformation information = lineToInformation.get(startSrcLineNumber);
      if (information == null || mappings.hasSrc(descendant)) {
        continue;
      }
      if (!information.isDiff()) {
        final int endLineNumber = srcPosConverter.toLineNumber(descendant.getEndPos());
        matchSameLine(descendant, dstLineToTree.get(startSrcLineNumber),
            lineToInformation.get(endLineNumber));
      } else {
        final LineDiff diff = information.getDiff();
        final Range dstRange = diff.getDstRange();
        final Collection<ITree> candidates = rangeToTree.get(dstRange);
        matchDiffLine(descendant, candidates);
      }
    }
  }

  private Multimap<Integer, ITree> createLineToTree(final ITree tree,
      final FilePosConverter filePosConverter) {
    final Multimap<Integer, ITree> multimap = ArrayListMultimap.create();
    for (final ITree descendant : tree.getDescendants()) {
      final int lineNumber = filePosConverter.toLineNumber(descendant.getPos());
      multimap.put(lineNumber, descendant);
    }
    return multimap;
  }

  private void matchSameLine(final ITree srcTree, final Collection<ITree> candidates,
      final MatchInformation endMatchInformation) {
    if (endMatchInformation == null || endMatchInformation.isDiff()) {
      return;
    }
    final LineMatch endLineMatch = endMatchInformation.getMatch();

    for (final ITree candidate : candidates) {
      if (mappings.hasDst(candidate)) {
        continue;
      }

      if (srcTree.getType() != candidate.getType()) {
        continue;
      }

      final int endDstLineNumber = dstPosConverter.toLineNumber(candidate.getEndPos());
      if (endLineMatch.getDst() == endDstLineNumber) {
        addMapping(srcTree, candidate);
      }
    }
  }

  private void matchDiffLine(final ITree srcTree, final Collection<ITree> candidateTree) {
    final List<ITree> candidates = candidateTree.stream()
        .filter(e -> e.hasSameType(srcTree))
        .collect(Collectors.toList());

    ITree maxTree = null;
    double max = -1.0d;
    for (final ITree iTree : candidates) {
      if (mappings.hasDst(iTree)) {
        continue;
      }

      final double sim = jaccardSimilarity(srcTree, iTree);
      if (sim > max && sim > GreedyBottomUpMatcher.SIM_THRESHOLD) {
        maxTree = iTree;
        max = sim;
      }
    }
    if (maxTree != null) {
      addMapping(srcTree, maxTree);
    }
  }

  private Map<Range, Range> createDiffMap(final List<MatchInformation> matchInformationList) {
    final Map<Range, Range> map = new HashMap<>();
    matchInformationList.stream()
        .filter(MatchInformation::isDiff)
        .map(MatchInformation::getDiff)
        .forEach(e -> map.put(e.getSrcRange(), e.getDstRange()));
    return map;
  }

  private Map<Integer, Range> createLineToRange(final List<Range> rangeList) {
    final Map<Integer, Range> map = new HashMap<>();
    for (final Range range : rangeList) {
      if (range == null) {
        continue;
      }
      int i = range.getMin();
      while (i < range.getMax()) {
        map.put(i, range);
        i += 1;
      }
    }
    return map;
  }

  private Map<Integer, MatchInformation> createLineToInformation(
      final List<MatchInformation> matchInformationList) {
    final Map<Integer, MatchInformation> map = new HashMap<>();
    matchInformationList.forEach(e -> {
      if (!e.isDiff()) {
        final LineMatch match = e.getMatch();
        map.put(match.getSrc(), e);
      } else {
        final LineDiff diff = e.getDiff();
        final Range srcRange = diff.getSrcRange();
        if (srcRange == null) {
          return;
        }
        for (int i = srcRange.getMin(); i <= srcRange.getMax(); i++) {
          map.put(i, e);
        }
      }
    });
    return map;
  }

  private Multimap<Range, ITree> createRangeToTree(final List<Range> rangeList, final ITree tree,
      final FilePosConverter filePosConverter) {
    final Multimap<Range, ITree> multimap = ArrayListMultimap.create();

    final Map<Integer, Range> lineToRange = createLineToRange(rangeList);
    for (final ITree descendant : tree.getDescendants()) {
      final int lineNumber = filePosConverter.toLineNumber(descendant.getPos());
      final Range range = lineToRange.get(lineNumber);
      multimap.put(range, descendant);
    }
    return multimap;
  }


  @Override
  protected double jaccardSimilarity(ITree src, ITree dst) {
    if (src.isLeaf() && dst.isLeaf() && src.hasSameType(dst) && src.getLabel()
        .equals(dst.getLabel())) {
      return 1.0;
    }
    double num = (double) numberOfCommonDescendants(src, dst);
    double den = (double) src.getDescendants()
        .size() + (double) dst.getDescendants()
        .size() - num;
    return num / den;
  }

  @Override
  protected int numberOfCommonDescendants(ITree src, ITree dst) {
    Set<ITree> dstDescendants = new HashSet<>(dst.getDescendants());
    int common = 0;

    for (ITree t : src.getDescendants()) {
      ITree m = mappings.getDst(t);
      if (m != null && dstDescendants.contains(m)) {
        common++;
      } else if (t.getHeight() <= 1) {
        final List<ITree> trees = dstDescendants.stream()
            .filter(e -> e.getHeight() <= 1)
            .collect(Collectors.toList());
        for (ITree tree : trees) {
          if (tree.getType() == t.getType() && t.getLabel()
              .equals(tree.getLabel())) {
            common++;
            break;
          }
        }
      }
    }
    return common;
  }
}
