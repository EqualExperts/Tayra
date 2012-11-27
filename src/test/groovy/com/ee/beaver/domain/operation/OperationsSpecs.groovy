package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.mongodb.Mongo
import com.mongodb.MongoException

public class OperationsSpecs extends RequiresMongoConnection {
	
	def operations

	@Before
	public void given() {
		operations = new Operations(standalone)
	}
	
	@Test
	public void producesCreateCollectionOperation() throws Exception {
		//When
		Operation operation = operations.get('c')
		
		//Then 
		assertThat operation, instanceOf(DefaultSchemaOperation.class)
	}
	
	@Test
	public void producesInsertDocumentOperation() throws Exception {
		//When
		Operation operation = operations.get('i')
		
		//Then 
		assertThat operation, instanceOf(InsertDocument.class)
	}
	
	@Test
	public void producesaNoOperationWhenItCannotIdentifyOperationCode() throws Exception {
		//When
		Operation operation = operations.get('unindentifiableOpcode')
				
		//Then 
		assertThat operation, sameInstance(Operation.NO_OP)
	}
	
	@Test
	public void producesDeleteDocumentOperation() throws Exception {
		//When
		Operation operation = operations.get('d')
		
		//Then
		assertThat operation, instanceOf(DeleteDocument.class)
	}
	
	@Test
	public void producesUpdateDocumentOperation() throws Exception {
		//When
		Operation operation = operations.get('u')
				
		//Then
		assertThat operation, instanceOf(UpdateDocument.class)
	}

}
