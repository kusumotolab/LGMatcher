package com.github.kusumotolab;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import com.github.gumtreediff.actions.model.Action;

public class AppTest {

  private final App app = new App();

  @Test
  public void testCalculateEditScript01() {
    final List<Action> actions = exec(1);
    assertThat(actions).hasSize(1);
  }

  private String getDir(final int num) {
    return "example/" + num;
  }

  private List<Action> exec(final int num) {
    final String dir = getDir(num);
    try {
      return this.app.calculateEditScript(dir + "/src.java", dir + "/dst.java");
    } catch (final IOException e) {
      e.printStackTrace();
      System.exit(-1);
      return null;
    }
  }
}