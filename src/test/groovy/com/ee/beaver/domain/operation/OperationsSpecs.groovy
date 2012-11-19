package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import java.net.UnknownHostException

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.util.JSON
import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Before
import org.junit.Test

public class OperationsSpecs {
	private static Mongo standalone;
	private static final String HOST = "localhost";
	private static final int PORT = 27020;
	def operations

	@BeforeClass
	public static void connectToMongo() throws UnknownHostException,
			MongoException {
		standalone = new Mongo(HOST, PORT);
	}

	@AfterClass
	public static void closeConnectionToMongo() {
		standalone.close();
	}
	
	@Before
	public void setup() {
		operations = new Operations(standalone)
	}
	
	@Test
	public void producesCreateCollectionOperation() throws Exception {
		//When
		Operation operation = operations.get('c')
		
		//Then 
		assertThat operation, instanceOf(CreateAndDropCollection.class)
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
