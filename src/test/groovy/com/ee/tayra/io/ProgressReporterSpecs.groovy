package com.ee.tayra.io

import com.ee.tayra.io.ProgressReporter;

import spock.lang.*

public class ProgressReporterSpecs extends Specification{

	private Writer mockExceptionDocumentsWriter

	private PrintWriter mockConsole

	private ProgressReporter reporter

	def setup() {
		mockConsole = Mock(PrintWriter)
		reporter = new ProgressReporter(mockConsole)
	}


	def incrementsReadDocumentsCountAfterASuccessfulRead() {
		given: 'an empty document'
			String document = "{}"

		when: 'it succeeds to read the document'
			reporter.onReadSuccess(document)

		then: 'read documents count is incremented'
			reporter.getDocumentsRead() ==  1
	}


	def incrementsWrittenDocumentsCountAfterASuccessfulWrite() {
		given: 'an empty document'
			String document = "{}"

		when: 'it suceeds to write the document'
			reporter.onWriteSuccess(document)

		then: 'written documents count is incremented'
			reporter.getDocumentsWritten() == 1
	}


	def doesNotIncrementWrittenDocumentsCountAfterAFailedWrite() {
		given: 'an empty document'
			String document = "{}"
			Throwable ignore = new Throwable("")

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , ignore)

		then: 'written documents count is not incremented'
			reporter.getDocumentsWritten() == 0
	}


	def doesNotIncrementReadDocumentsCountOnAFailedRead() {
		given: 'a null document'
			String document = null
			Throwable ignore = new Throwable("")

		when: 'it fails to read the document'
			reporter.onReadFailure(document, ignore)

		then: 'read documents count is not incremented'
			reporter.getDocumentsRead() == 0
	}


	def shoutsWhenThereIsAFailedRead() {
		given: 'a null document'
			String document = null

		and: 'a problem occurs'
			Throwable problem = new Throwable("Connection Broken!")

		when: 'it fails to read the document'
			reporter.onReadFailure(document, problem)

		then: 'error message should be shown as'
			1 * mockConsole.printf("===> Unable to Read Documents: %s\r", problem.message)
	}

	def reportsWaitingWhenNoDocumentIsRead() {
		given: 'a document is read'
			String document = ''

		when: 'it starts reading document'
			reporter.onReadStart(document)

		then: 'waiting for document is not reported'
			1 * mockConsole.printf('%s Wrote %d Document(s). Waiting for documents...\r', '/', 0)
	}

}
