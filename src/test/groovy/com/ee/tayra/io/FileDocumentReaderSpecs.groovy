package com.ee.tayra.io;

import spock.lang.Specification

public class FileDocumentReaderSpecs extends Specification {

	private ReadNotifier mockNotifier
	private Reader mockDelegate
	private FileDocumentReader fileDocumentReader
	private final String document = "\"ts\""
	

	def setup() {
		mockDelegate = Mock(BufferedReader)
		mockNotifier = Mock(ReadNotifier)
		fileDocumentReader = new FileDocumentReader(mockDelegate)
		fileDocumentReader.notifier = mockNotifier
	}

	def notifiesWhenReadingADocumentIsSuccessful() throws Exception {
		given: 'a document is read'
			mockDelegate.readLine() >> {document}

		when: 'document is read'
			fileDocumentReader.readLine()

		then: 'a notification of successful read is given'
			1 * mockNotifier.notifyReadSuccess(document)
	}
	
	def notifiesWhenReadingFromReaderFails() throws Exception {
		given: 'a problem occurs while reading'
			final IOException problem = new IOException()
			mockDelegate.readLine() >> {throw problem}

		when: 'the document is read'
			fileDocumentReader.readLine()

		then: 'it notifies a failed read only'
			1 * mockNotifier.notifyReadFailure(null, problem)
	}
}
