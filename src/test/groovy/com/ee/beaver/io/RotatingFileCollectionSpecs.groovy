package com.ee.beaver.io

import spock.lang.Specification;

public class RotatingFileCollectionSpecs extends Specification{

	private String workingDirectory = System.getProperty("user.dir")
	private String nameOfFile
	private RotatingFileCollection fileCollection
	private boolean isMultiple

	def itReadsAllFilesOfGivenPrefix() {
		given: 'a file'
			nameOfFile = 'test.log'
			int numberOfFiles = 0

		and: 'multiple files having postfix exist'
			createTestFile(nameOfFile)
			createTestFile(nameOfFile + ".1")
			createTestFile(nameOfFile + ".2")

		and: 'I want to read all'
			isMultiple = true

		when: 'I invoke File Collector'
			fileCollection = new RotatingFileCollection(nameOfFile, isMultiple)

		then: 'all file names that exist should be collected'
			fileCollection.withFile {
				numberOfFiles++
			}
			numberOfFiles == 3

	}

	def itIteratesAllFilesInReverseOrderOfSuffix() {
		given: 'a file'
			nameOfFile = 'test.log'
			def expectedFiles = [nameOfFile + ".2", nameOfFile + ".1", nameOfFile]
			def actualFiles = []

		and: 'multiple files having postfix exist'
			createTestFile(nameOfFile)
			createTestFile(nameOfFile + ".1")
			createTestFile(nameOfFile + ".2")

		and: 'I want to read all'
			isMultiple = true

		when: 'I invoke File Collector'
			fileCollection = new RotatingFileCollection(nameOfFile, isMultiple)

		then: 'all file names that exist should be collected'
			fileCollection.withFile { file ->
				actualFiles << file.name
				file instanceof File
			}
			actualFiles == expectedFiles
	}

	def itIteratesOverFileObject() {
		given: 'a file'
			nameOfFile = 'test.log'
			createTestFile(nameOfFile)

		and: 'I want to read it'
			isMultiple = false

		when: 'I invoke File Collector'
			fileCollection = new RotatingFileCollection(nameOfFile, isMultiple)

		then: 'it iterates over a File Object'
			fileCollection.withFile {
				it instanceof File
			}
	}

	def itReadsOnlyOneFileForGivenFileName() {
		given: 'file name'
			nameOfFile = 'test.log'
			def expectedFiles = [nameOfFile]
			def actualFiles = []

		and: 'multiple files with that prefix exist'
			createTestFile(nameOfFile)
			createTestFile(nameOfFile + ".1")
			createTestFile(nameOfFile + ".2")

		and: 'I want to read only One file of given name'
			isMultiple = false

		when: 'I invoke File Collector'
			fileCollection = new RotatingFileCollection(nameOfFile, isMultiple)

		then: 'Only file that matches the name should be listed'
			fileCollection.withFile { file ->
				actualFiles << file
			}
			actualFiles == expectedFiles
	}

	private void createTestFile(String fileName) {
		File testFile = new File(workingDirectory + System.getProperty("file.separator") + fileName)
		if(!testFile.exists())
			testFile.createNewFile();
		FileWriter writer = new FileWriter(testFile)
		writer.write("test data: " + fileName)
		writer.close()
	}

	def cleanup() {
		def directory = new File(workingDirectory)
		for(File f : directory.listFiles()) {
			if(f.getName().startsWith(nameOfFile)) {
				f.delete()
			}
		}
	}
}
