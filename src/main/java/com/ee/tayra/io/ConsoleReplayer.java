package com.ee.tayra.io;

import java.io.PrintWriter;

public class ConsoleReplayer implements Replayer {

  private final PrintWriter console;

  public ConsoleReplayer(final PrintWriter console) {
    this.console = console;
  }

  @Override
  public final boolean replay(final String document) {
    console.println(document);
    return true;
  }

}
