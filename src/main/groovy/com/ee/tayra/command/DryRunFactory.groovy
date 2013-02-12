package com.ee.tayra.command

import com.ee.tayra.io.ConsoleReplayer
import com.ee.tayra.io.EmptyProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import java.io.PrintWriter;

class DryRunFactory {

  private final PrintWriter console
  private final def binding
  private final listeningReporter = new EmptyProgressReporter()  private final def criteria;

  public DryRunFactory(def binding, def criteria, PrintWriter console) {
    this.binding = binding
    this.criteria = criteria;
    this.console = console
  }

  def getWriter() {
    binding.hasVariable('writer') ? binding.getVariable('writer')
        : new SelectiveOplogReplayer(criteria, new ConsoleReplayer(console))
  }

  def getListener() {
    binding.hasVariable('listener') ? binding.getVariable('listener')
        : listeningReporter
  }

  def getReporter() {
    binding.hasVariable('reporter') ? binding.getVariable('reporter')
        : listeningReporter
  }
}
