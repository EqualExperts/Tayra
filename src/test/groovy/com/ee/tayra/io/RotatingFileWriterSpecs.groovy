package com.ee.tayra.io

import com.ee.tayra.io.RotatingFileWriter;

import spock.lang.Specification

public class RotatingFileWriterSpecs extends Specification{

	private String workingDirectory = System.getProperty("user.dir")
	private RotatingFileWriter rotatingFileWriter
	private String data = '1234567890'
	private String nameOfFile

	def itCreatesAFileUsingRotatingFileWriter() {
		given: 'name of file'
			nameOfFile = 'test.log'

		when: 'I invoke Writer with file name'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)
			File file = new File(nameOfFile)

		then: 'file should be created'
			file.exists()
	}

	def writesDataToDestination() throws IOException {
		given: 'some data'
			String data = data

		and: 'filename of file to record'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)

		when: 'it writes the data'
			rotatingFileWriter.write(data)

		then: 'destination should have the document'
			FileReader fileReader = new FileReader(new File(nameOfFile))
			data == fileReader.find {data}
			fileReader.close()
	}

	def writesDataToASingleFileOnly() {
		given: 'file name, Size and Rotation Limit of Files'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.write(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataInSpecifiedNumberOfFilesOfDefaultSize() {
		given: 'file name, Size and Rotation Limit of Files'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)
			rotatingFileWriter.setFileMax(3)

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.write(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataToSingleFileOfSpecifiedSize() {
		given: 'file name, Size and Rotation Limit of Files'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)
			rotatingFileWriter.setFileSize('2KB')

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.write(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
	}

	def writesDataToFileOfSpecifiedSizeAndSpecifiedRotationLimit() {
		given: 'file name, Size and Rotation Limit of Files'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile)
			rotatingFileWriter.setFileSize('16KB')
			rotatingFileWriter.setFileMax(4)

		and: 'some data'
			String data = data

		when: 'it writes data'
			200.times {
				rotatingFileWriter.write(data)
			}

		then: 'the file containing data should exist'
			def backupFile = new File(workingDirectory + File.separator + nameOfFile)
			backupFile.exists()
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
