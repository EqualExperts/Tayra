package com.ee.beaver.io

import spock.lang.*

public class ProgressReporterSpecs extends Specification{
	
	private Writer mockExceptionDocumentsWriter

	private PrintWriter mockConsole

	private ProgressReporter reporter
	
	def setup() {
		mockExceptionDocumentsWriter = Mock(Writer)
		mockConsole = Mock(PrintWriter)
		reporter = new ProgressReporter(mockExceptionDocumentsWriter, mockConsole)
	}

	
	def writesExceptioningDocuments() throws IOException {
		given: 'an empty document'
			String document = "{}"
			Throwable problem = null
		
		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)
		
		then: 'an exception document is written'
			1 * mockExceptionDocumentsWriter.append(document)
			0 * mockConsole.printf("")
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
	
	
	def incrementsExceptionDocumentsCountAfterAFailedWrite() {
		given: 'an empty document'
			String document = "{}"
			Throwable ignore = null
		
		when: 'it fails to write the document'
			reporter.onWriteFailure(document , ignore)
		
		then: 'exception documents count is incremented'
			reporter.getExceptionDocuments() == 1
	}
	
	
	def doesNotIncrementWrittenDocumentsCountAfterAFailedWrite() {
		given: 'an empty document'
			String document = "{}"
			Throwable ignore = null
		
		when: 'it fails to write the document'
			reporter.onWriteFailure(document , ignore)
		
		then: 'written documents count is not incremented'
			reporter.getDocumentsWritten() == 0
	}

	
	def doesNotIncrementsExceptionDocumentsCountOnASuccessfulWrite() {
		given: 'an empty document'
			String document = "{}"
		
		when: 'it succeeds to write the document'
			reporter.onWriteSuccess(document)
		
		then: 'exception documents count is not incremented'
			reporter.getExceptionDocuments() == 0
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
			reporter = new ProgressReporter(null, mockConsole)
			
		and: 'an empty document'
			String document = "{}"
		
		and: 'a problem occurs'
			IOException problem = new IOException("Disk full")

		when: 'it fails to write the document'
			reporter.onWriteFailure(document , problem)

		then: 'error message should be shown as'
			mockConsole.printf("===> Unable to Write Document(s) %s", problem.message)
	}
}
