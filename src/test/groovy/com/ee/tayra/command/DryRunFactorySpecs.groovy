package com.ee.tayra.command

import com.ee.tayra.io.DeafAndDumbReporter;
import com.ee.tayra.io.SelectiveOplogReplayer;

import spock.lang.Specification

class DryRunFactorySpecs extends Specification{

	private def config
	private def factory

	def setup() {
		config = new Config()
		factory =  new DryRunFactory(config)
	}

	def createsEmptyListener() {
		expect: 'listener created is instance of EmptyProgressReporter'
			factory.createListener().class == DeafAndDumbReporter
	}

	def createsEmptyReporter() {
		expect: 'reporter created is instance of EmptyProgressReporter'
			factory.createReporter().class == DeafAndDumbReporter
	}

	def createsWriter() {
		expect: 'writer created is instance of SelectiveOplogReplayer'
			factory.createWriter().class == SelectiveOplogReplayer
	}
}