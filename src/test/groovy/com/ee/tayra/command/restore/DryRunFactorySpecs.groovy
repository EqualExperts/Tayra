package com.ee.tayra.command.restore

import spock.lang.Specification

import com.ee.tayra.io.listener.DeafAndDumbReporter
import com.ee.tayra.io.writer.ConsoleReplayer
import com.ee.tayra.io.writer.SelectiveOplogReplayer

class DryRunFactorySpecs extends Specification{

  private def config
  private def factory
  private PrintWriter ignoreConsole

  def setup() {
    config = new RestoreCmdDefaults()
    factory =  new DryRunFactory(config, ignoreConsole)
  }

  def createsEmptyListener() {
    expect: 'listener created is instance of EmptyProgressReporter'
      factory.createListener().getClass() == DeafAndDumbReporter
  }

  def createsEmptyReporter() {
    expect: 'reporter created is instance of EmptyProgressReporter'
      factory.createReporter().getClass() == DeafAndDumbReporter
  }

  def createsReplayers() {
    given:
      config.sNs = namespace
      config.sUntil = untilDate
      factory =  new DryRunFactory(config, ignoreConsole)

    expect: 'writer created is type of klass'
      factory.createWriter().getClass() == klass

    where: 'namespace and until are varied as...'
      namespace |     untilDate        | klass
      ''        |         ''           | ConsoleReplayer
      'test'    |         ''           | SelectiveOplogReplayer
      ''        |'2013-03-16T15:19:40Z'| SelectiveOplogReplayer
      'test'    |'2013-03-16T15:19:40Z'| SelectiveOplogReplayer
  }
}