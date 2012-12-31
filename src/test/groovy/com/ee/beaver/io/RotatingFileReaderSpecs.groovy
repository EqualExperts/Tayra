package com.ee.beaver.io

import java.security.KeyStore.Builder.FileBuilder;

import spock.lang.Specification;

public class RotatingFileReaderSpecs extends Specification{

	private String workingDirectory = System.getProperty("user.dir")
	private String prefixOfFile
	private RotatingFileReader rotatingFileReader

	def itReadsAllFileNamesOfGivenPrefix() {
		given: 'prefix of file name'
			prefixOfFile = 'test.log'
			createTestFile(prefixOfFile)
			createTestFile(prefixOfFile + ".1")
			createTestFile(prefixOfFile + ".2")

		when: 'I invoke Log Reader'
			rotatingFileReader = new RotatingFileReader(prefixOfFile, new FileReader(prefixOfFile))

		then: 'all file names that exist for a given prefix should be listed'
			rotatingFileReader.getAllFileNames().contains(prefixOfFile)
	}

	def itReadsDataFromAllTheFilesWithAGivenPrefix() {
		given: 'prefix of file name'
			prefixOfFile = 'test.log'
			createTestFile(prefixOfFile)
			createTestFile(prefixOfFile + ".1")
			createTestFile(prefixOfFile + ".2")

		and: 'A Rotating File Reader'
			rotatingFileReader = new RotatingFileReader(prefixOfFile, new FileReader(prefixOfFile))

		when: 'I read'
			StringBuilder data = new StringBuilder()
			String dataRead
			while((dataRead = rotatingFileReader.readLine()) != null) {
				data.append(dataRead + ", ")
			}

		then: 'data from all prefix named files should be read'
			data.contains("test data: $prefixOfFile.2, test data: $prefixOfFile.1, test data: $prefixOfFile, ")
	}

	private void createTestFile(String fileName) {
		File testFile = new File(workingDirectory + "\\" + fileName)
		if(!testFile.exists())
			testFile.createNewFile();
		FileWriter writer = new FileWriter(testFile)
		writer.write("test data: " + fileName)
		writer.close()
	}

	def cleanup() {
		rotatingFileReader.close()
		def directory = new File(workingDirectory)
		for(File f : directory.listFiles()) {
			if(f.getName().startsWith(prefixOfFile)) {
				f.delete()
			}
		}
	}
}