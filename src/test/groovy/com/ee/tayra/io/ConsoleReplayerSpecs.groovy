package com.ee.tayra.io

import com.mongodb.DBApiLayer.Result;

import spock.lang.Specification;

class ConsoleReplayerSpecs extends Specification{

	def replaysDocumentToConsole() {
		given: 'a target console output'
			PrintWriter targetConsole = Mock(PrintWriter)
			ConsoleReplayer consoleReplayer = new ConsoleReplayer(targetConsole)

		and: 'a document'
			def document = '"ts"'

		when: 'document is replayed'
			consoleReplayer.replay(document)

		then: 'it is output to the target console'
			1 * targetConsole.println(document)
	}
}

