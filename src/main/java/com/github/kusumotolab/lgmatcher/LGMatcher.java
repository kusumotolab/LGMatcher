package com.github.kusumotolab.lgmatcher;

import com.github.gumtreediff.matchers.CompositeMatcher;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedyBottomUpMatcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.ITree;

/**
 * LGMatcher creates CompositeMatcher which contains line-based Matcher and GumTree's Matcher.
 */
public class LGMatcher {

  /**
   * @param srcContent content of src file
   * @param dstContent content of dst file
   * @param srcTree tree of src file
   * @param dstTree tree of dst file
   * @param mapping MappingStore shared in all Matchers
   * @return CompositeMatcher which contains line-based Matcher and GumTree's Matcher
   */
  public static Matcher create(final String srcContent, final String dstContent,
      final ITree srcTree, final ITree dstTree, final MappingStore mapping) {
    return new CompositeMatcher(srcTree, dstTree, mapping, new com.github.gumtreediff.matchers.Matcher[]{
        new LineMatcher(srcContent, dstContent, srcTree, dstTree, mapping),
        new GreedySubtreeMatcher(srcTree, dstTree, mapping),
        new GreedyBottomUpMatcher(srcTree, dstTree, mapping)
    });
  }
}
