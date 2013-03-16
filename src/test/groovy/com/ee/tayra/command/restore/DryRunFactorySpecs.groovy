package com.ee.tayra.command.restore

import java.io.PrintWriter;

import com.ee.tayra.command.restore.DryRunFactory
import com.ee.tayra.command.restore.RestoreCmdDefaults
import com.ee.tayra.io.DeafAndDumbReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.ee.tayra.io.ConsoleReplayer

import spock.lang.Specification

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
			factory.createListener().class == DeafAndDumbReporter
	}

	def createsEmptyReporter() {
		expect: 'reporter created is instance of EmptyProgressReporter'
			factory.createReporter().class == DeafAndDumbReporter
	}

	def createsReplayers() {
		given:
		    config.sNs = namespace
			config.sUntil = untilDate
			factory =  new DryRunFactory(config, ignoreConsole)
			
		expect: 'writer created is type of klass'
			factory.createWriter().class == klass
			
		where: 'namespace and until are varied as...'
			namespace | untilDate | klass
			    ''    |    ''     | ConsoleReplayer
			  'test'  |    ''     | SelectiveOplogReplayer
			  ''      |'16/3/2013'| SelectiveOplogReplayer
			  'test'  |'16/3/2013'| SelectiveOplogReplayer
		
	}
}