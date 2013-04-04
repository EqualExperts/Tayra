package com.ee.tayra.io

import com.ee.tayra.io.ProgressReporter;
import com.mongodb.MongoException

import spock.lang.*

public class RestoreProgressReporterSpecs extends Specification {

	private Writer mockExceptionDocumentsWriter

	private Writer mockExceptionDetailsWriter

	private PrintWriter mockConsole

	private ProgressReporter reporter

	def setup() {
		mockExceptionDocumentsWriter = Mock(Writer)
		mockExceptionDetailsWriter = Mock(Writer)
		mockConsole = Mock(PrintWriter)
		reporter = new RestoreProgressReporter(mockExceptionDocumentsWriter, mockExceptionDetailsWriter, mockConsole)
	}


	def writesExceptioningDocuments() throws IOException {
		given: 'an empty document'
			String document = "{}"
			Throwable problem = new Throwable()

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)

		then: 'an exception document is written'
			1 * mockExceptionDocumentsWriter.append(document)
			1 * mockExceptionDetailsWriter.append(document)
			1 * mockExceptionDetailsWriter.append(problem.message)
			0 * mockConsole.printf("")
	}


	def incrementsExceptionDocumentsCountAfterAFailedWrite() {
		given: 'an empty document'
			String document = "{}"
			Throwable ignore =  new Throwable()

		when: 'it fails to write the document'
			reporter.onWriteFailure(document, ignore)

		then: 'exception documents count is incremented'
			reporter.getExceptionDocuments() == 1
	}


	def doesNotIncrementsExceptionDocumentsCountOnASuccessfulWrite() {
		given: 'an empty document'
			String document = "{}"

		when: 'it succeeds to write the document'
			reporter.onWriteSuccess(document)

		then: 'exception documents count is not incremented'
			reporter.getExceptionDocuments() == 0
	}


	def doesNotWriteExceptioningDocumentsWhenNoDocumentIsAvailable() throws IOException {
		given: 'a null document'
			String noDocument = null

		and: 'a problem occurs'
			Throwable problem = Mock(Throwable)

		when: 'it fails to write the document'
			reporter.onWriteFailure(noDocument , problem)

		then: 'exception document is not written'
			0 * mockExceptionDocumentsWriter.append(noDocument)

		and: 'no error message given'
			0 * mockConsole.printf("")

		and: 'problem stack trace is given'
			1 * problem.printStackTrace(mockConsole)

		and: 'exception documents count is not incremented'
			reporter.getExceptionDocuments() == 0
  }


	def shoutsWhenThereIsAProblemWritingExceptioningDocuments() throws Exception {
		given: 'an empty document'
			String document = "{}"

		and: 'a problem occurs'
			IOException problem = new IOException("Disk full")
			mockExceptionDocumentsWriter.append(document) >> { throw problem }

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)

		then: 'it shows proper error message'
			1 * mockConsole.printf("===> Unable to Write Exceptioning Document(s) %s", problem.message)

		and: 'exception documents count is incremented'
			reporter.getExceptionDocuments() == 1
	}


	def writesDocumentsOnlyWhenExceptionsWriterIsDefined() throws Exception {
		given: 'a progress reporter without exception document writer'
		    def ignoreWriter = null
			reporter = new RestoreProgressReporter(mockExceptionDocumentsWriter, ignoreWriter, mockConsole)

		and: 'an empty document'
			String document = "{}"

		and: 'a problem occurs'
			IOException problem = new IOException("Disk full")

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)

		then: 'error message should be shown as'
			1 * mockConsole.printf("===> Unable to Write Document(s) %s", problem.message)
	}

	def writesDocumentsOnlyWhenExceptionDetailsWriterIsDefined() throws Exception {
		given: 'a progress reporter without exception details writer'
			def ignoreWriter = null
			reporter = new RestoreProgressReporter(ignoreWriter, mockExceptionDetailsWriter, mockConsole)

		and: 'an empty document'
			String document = "{}"

		and: 'a problem occurs'
			IOException problem = new IOException("Disk full")

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)

		then: 'error message should be shown as'
			1 * mockConsole.printf("===> Unable to Write Document(s) %s", problem.message)
	}

	def recordsWriteFailureWithDetails() {
		given: 'a document'
			String document = 'document'

		and: 'a problem occurs'
			def problem = new MongoException("Duplicate Key")

		when: 'there is a write failure'
			reporter.onWriteFailure(document , problem)

		then: 'the failure details should have been recorded'
			2 * mockExceptionDocumentsWriter.append(_)
			1 * mockExceptionDocumentsWriter.flush()
			6 * mockExceptionDetailsWriter.append(_)
			3 * mockExceptionDetailsWriter.flush()
	}
}