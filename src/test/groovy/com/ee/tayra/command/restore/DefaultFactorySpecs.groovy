package com.ee.tayra.command.restore

import com.ee.tayra.command.restore.DefaultFactory;
import com.ee.tayra.command.restore.RestoreCmdDefaults;
import com.ee.tayra.io.RestoreProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.ee.tayra.io.OplogReplayer
import com.mongodb.Mongo
import spock.lang.Specification

class DefaultFactorySpecs extends Specification {

	private RestoreCmdDefaults config
	private def factory
	private Mongo ignoreMongo
	private PrintWriter ignoreConsole

	def setup() {
		config = new RestoreCmdDefaults()
		config.destination = 'localhost'
		config.port = 27017
		config.username = 'admin'
		config.password = 'admin'
		config.exceptionFile = 'exception.documents'
		factory =  new DefaultFactory(config, ignoreMongo, ignoreConsole)
	}

	def createsEmptyListener() {
		expect: 'listener created is instance of EmptyProgressReporter'
			factory.createListener().class == RestoreProgressReporter
	}

	def createsEmptyReporter() {
		expect: 'reporter created is instance of EmptyProgressReporter'
			factory.createReporter().class == RestoreProgressReporter
	}

	def createsWriters() {
		given:
		    config.sNs = namespace
			config.sUntil = untilDate
			factory =  new DefaultFactory(config, ignoreMongo, ignoreConsole)
			
		expect: 'writer created is type of klass'
			factory.createWriter().class == klass
			
		where: 'namespace and until are varied as...'
			namespace |     untilDate        | klass
			    ''    |         ''           | OplogReplayer
			  'test'  |         ''           | SelectiveOplogReplayer
			  ''      |'2013-03-16T15:19:40Z'| SelectiveOplogReplayer
			  'test'  |'2013-03-16T15:19:40Z'| SelectiveOplogReplayer
 	}
}
