package com.github.kusumotolab;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.kusumotolab.lgmatcher.LGMatcher;

public class App {

  public static void main(final String[] args) throws IOException {
    final App app = new App();
    final List<Action> actions = app.calculateEditScript(args[0], args[1]);
    System.out.println(" The size of the edit scripts is " + actions.size() + ".");
  }

  public App() {
    Run.initGenerators();
  }

  public List<Action> calculateEditScript(final String src, final String dst) throws IOException {
    final ITree srcTree = Generators.getInstance()
        .getTree(src)
        .getRoot();
    final ITree dstTree = Generators.getInstance()
        .getTree(dst)
        .getRoot();

    final String srcContent = readAll(src);
    final String dstContent = readAll(dst);

    final MappingStore mapping = new MappingStore();
    final Matcher matcher = LGMatcher.create(srcContent, dstContent, srcTree, dstTree, mapping);
    matcher.match();

    final ActionGenerator generator = new ActionGenerator(srcTree, dstTree, mapping);
    generator.generate();

    return generator.getActions();
  }

  private String readAll(final String path) throws IOException {
    return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
        .collect(Collectors.joining(System.getProperty("line.separator")));
  }
}
