package com.ee.tayra.design

import jdepend.framework.JDepend
import jdepend.framework.JavaPackage
import spock.lang.Specification

class DependencyTest extends Specification {
  private static JDepend jdepend

  def setupSpec() {
      JavaPackage.metaClass.dependsOn = { JavaPackage dependent ->
        delegate.efferents.contains(dependent)
      }

      jdepend = new JDepend()
      //given 'directory where classes are compiled using Gradle build'
      jdepend.addDirectory('build/classes/main/')
  }

  def ensuresThereAreNoCyclicDependencies() {
      when: 'jDepend analyzes codebase'
        jdepend.analyze()

      then:  'there are no cyclic dependencies'
        !jdepend.containsCycles()

  }

  def ensuresConnectorDoesNotDependOnAnyOtherTayraPackages() {
    given: 'connector package'
      def connector = jdepend.getPackage('com.ee.tayra.connector')

    when: 'jDepend analyzes codebase'
      jdepend.analyze()

    then: 'it should depend on no tayra packages'
      jdepend.packages.findAll {
        it.name ==~ /com\.ee\.tayra\..*/  || it.name ==~ /com\.ee\.tayra/
      }.inject (true) { initialValue, aPackage ->
        initialValue && !connector.dependsOn(aPackage)
      }
  }
}
