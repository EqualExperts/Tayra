package com.ee.tayra.io.reader.nio


import com.ee.tayra.io.reader.DocumentReader;

import spock.lang.Specification;


class MemoryMappedFileReaderSpecs extends Specification {

	def itReadsFileInChunk() {
		given: 'a file'
			String fileName = "C:\\test2\\test.1mb"

		when: 'it is read'
			DocumentReader reader = new MemoryMappedDocumentReader(fileName)
			String document;
			while ((document = reader.readDocument()) != null) {
//				println document
			}

		then: 'we get a chunk'
//			document1 == '{ "ts" : { "$ts" : 1366288961 , "$inc" : 2} , "h" : 2763120522771994968 , "v" : 2 , "op" : "i" , "ns" : "things.items" , "o" : { "_id" : { "$oid" : "516fea4283eb9397cf8b6b55"} , "name" : "One"}}'
	}
}
