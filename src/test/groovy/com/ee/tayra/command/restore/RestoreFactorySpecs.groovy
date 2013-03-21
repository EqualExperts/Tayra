package com.ee.tayra.command.restore

import com.mongodb.Mongo
import spock.lang.Specification

class RestoreFactorySpecs extends Specification{

	private RestoreCmdDefaults config
	private Mongo ignoreMongo
	private PrintWriter ignoreConsole

	def setup() {
		config = new RestoreCmdDefaults()
		config.destination = 'localhost'
		config.port = 27017
		config.username = 'admin'
		config.password = 'admin'
		config.exceptionFile = 'exception.documents'
	}

	def createsAppropriateFactories() {
		given: 'Dry run is required or not'
			config.dryRunRequired = dryRunRequired

		expect: 'correct factory is created'
			RestoreFactory.createFactory(config, ignoreMongo, ignoreConsole).class == klass

		where: 'appropriate factories are created for dry run and non dry run options'
			dryRunRequired | klass
				true       | DryRunFactory
				false      | DefaultFactory
	}
	
}