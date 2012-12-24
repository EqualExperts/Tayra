package com.ee.beaver.io

import org.bson.types.ObjectId

import spock.lang.Specification

import com.ee.beaver.domain.operation.MongoUtils
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder

public class RotatingFileWriterSpecs extends Specification{

	private RotatingFileWriter rotatingFileWriter
	private String dbName = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def nameOfFile
	def objId = new ObjectId()

	def getDocumentString(ObjectId objId) {
		def o = new BasicDBObjectBuilder()
					.start()
						.add( "_id" , new BasicDBObject('$oid', objId))
						.add( "name" , name)
					.get()
		MongoUtils.insertDocument(dbName,collectionName, o) as String
	}

	def itCreatesAFileUsingRotatingFileWriter() {
		given: 'name of file'
			nameOfFile = 'test.log'

		when: 'I invoke Writer with file name'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile, '2KB', 1)
			File file = new File(nameOfFile)

		then: 'file should exist'
			file.exists()
	}

	def writesDocumentToDestination() throws IOException {
		given: 'an oplog entry'
			String document = getDocumentString(objId)

		and: 'file to record to and rotating File Writer'
			nameOfFile = 'test.log'
			rotatingFileWriter = new RotatingFileWriter(nameOfFile, '2KB', 1)

		when: 'it writes the document'
			rotatingFileWriter.write(document)

		then: 'destination should have the document'
			FileReader fileReader = new FileReader(new File(nameOfFile))
			document == fileReader.find {document}
			fileReader.close()
	}

	def generatesBackupOfSpecifiedSizeAndSpecifiedRotationLimit() {
		given: 'file name, Size and Rotation Limit of Backup Files'
			nameOfFile = 'test.log'
			String fileSize = '16KB'
			int fileMax = 4
			rotatingFileWriter = new RotatingFileWriter(nameOfFile, fileSize, fileMax)

		and: 'an oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes multiple document'
			1000.times {
				rotatingFileWriter.write(document)
			}

		then: 'destination should have the document'
			def backupFile = new File(System.getProperty("user.dir") + '\\' + nameOfFile)
			backupFile.exists()
	}

	def generatesMultipleBackupFileOfSpecifiedSize() {
		given: 'file name, Size and Rotation Limit of Backup Files'
			nameOfFile = 'test.log'
			String fileSize = '128KB'
			int fileMax = 10
			rotatingFileWriter = new RotatingFileWriter(nameOfFile, fileSize, fileMax)

		and: 'an oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes multiple document'
			3600.times {
				rotatingFileWriter.write(document)
			}

		then: 'destination should have the document'
			def backupFile = new File(System.getProperty("user.dir") + '\\' + nameOfFile)
			backupFile.exists()
	}

	def cleanup() {
		rotatingFileWriter.close()
		def directory = new File(System.getProperty("user.dir"))
		for(File f : directory.listFiles()) {
			if(f.getName().startsWith(nameOfFile)) {
				f.delete()
			}
		}
	}

}
