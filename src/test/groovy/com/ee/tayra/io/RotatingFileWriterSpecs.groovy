package com.ee.tayra.io

import org.apache.log4j.Logger
import spock.lang.Specification

public class RotatingFileWriterSpecs extends Specification {

	private String workingDirectory = System.getProperty("user.dir")
	private RotatingFileWriter rotatingFileWriter
	private String data = '1234567890'
	private String nameOfFile
    private Logger mockLogger

    def setup() {
        nameOfFile = 'test.log'
        rotatingFileWriter = new RotatingFileWriter(nameOfFile)
        mockLogger = Mock(Logger)
    }

	def itCreatesAFileUsingRotatingFileWriter() {
		when: 'I invoke Writer with file name'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)
			File file = new File(nameOfFile)

		then: 'file should be created'
			file.exists()
	}

	def writesDataToDestination() throws IOException {
		given: 'some data'
			String data = data

		when: 'it writes the data'
			rotatingFileWriter.writeDocument(data)

		then: 'destination should have the document'
			FileReader fileReader = new FileReader(new File(nameOfFile))
			data == fileReader.find {data}
			fileReader.close()
	}

	def writesDataToASingleFileOnly() {
		given: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.writeDocument(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataInSpecifiedNumberOfFilesOfDefaultSize() {
		given: 'Rotation Limit of Files'
			rotatingFileWriter.setFileMax(3)

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.writeDocument(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataToSingleFileOfSpecifiedSize() {
		given: 'Size of Files'
			rotatingFileWriter.setFileSize('2KB')

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.writeDocument(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataToFileOfSpecifiedSizeAndSpecifiedRotationLimit() {
		given: 'Size and Rotation Limit of Files'
			rotatingFileWriter.setFileSize('16KB')
			rotatingFileWriter.setFileMax(4)

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.writeDocument(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}
  def notifiesWhenItAboutToStartWritingADocument()
    throws Exception {
      given: 'a notifier'
        WriteNotifier mockNotifier = Mock(WriteNotifier)
        rotatingFileWriter.setNotifier(mockNotifier)

      when: 'the document is written'
        rotatingFileWriter.writeDocument(data)

      then: 'it notifies a successful write'
        1 * mockNotifier.notifyWriteStart(data)
  }

  def notifiesWhenWritingADocumentToWriterIsSuccessful()
    throws Exception {
      given: 'a notifier'
        WriteNotifier mockNotifier = Mock(WriteNotifier)
        rotatingFileWriter.setNotifier(mockNotifier)

      when: 'the document is written'
        rotatingFileWriter.writeDocument(data)

      then: 'it notifies a successful write'
        1 * mockNotifier.notifyWriteSuccess(data)
    }

    def notifiesWhenWriterFailsToWrite() {
      given: 'a rotating file writer'
        def rotatingFileWriter = new RotatingFileWriter(nameOfFile) {
          Logger createLogger() {
              mockLogger
          }
        }

      and: 'a notifier'
        WriteNotifier mockNotifier = Mock(WriteNotifier)
        rotatingFileWriter.setNotifier(mockNotifier)

      and: 'a problem occurs while writing'
        final IOException problem = new IOException("Disk Full")
        mockLogger.info(data) >> {throw problem}

      when: 'the document is copied'
        rotatingFileWriter.writeDocument(data)

      then: 'a failed write'
        1 * mockNotifier.notifyWriteStart(data)
        1 * mockNotifier.notifyWriteFailure(data, problem)
    }

    def cleanup() {
		rotatingFileWriter.close()
		def directory = new File(workingDirectory)
		for(File f : directory.listFiles()) {
			if(f.getName().startsWith(nameOfFile)) {
				f.delete()
			}
		}
	}
}
