package com.ee.tayra.fixtures;
import java.net.UnknownHostException;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import fit.Parse;
import fit.exception.MissingCellsFailureException;
import fitlibrary.DoFixture;
//import fitlibrary.exception.table.MissingCellsException;

public class MongoFixture extends DoFixture {
	
	private MongoClient mongo;
	private DB db;
	
	public MongoFixture(String host, int port) throws UnknownHostException {
	    mongo = new MongoClient(host, port);
	    mongo.getDB("test").command("ismaster");
	}
	
	public void useDatabase(Parse cells) {
        Parse expected = cells.more;
        if (expected == null) {
            throw new MissingCellsFailureException(cells.text() + " requires an argument");
        }
        
        String databaseName = expected.text();
        db = mongo.getDB(databaseName);
        String actual = db.getName();
		if (databaseName.equals(actual)) {
            right(expected);
        } else {
            wrong(expected, actual);
        }
	}
	
	public void run(Parse cells) {
        Parse command = cells.more;
        if (command == null) {
            throw new MissingCellsFailureException(cells.text() + " requires an argument");
        }
        String cmdString = command.text();
		CommandResult result = db.doEval(cmdString);
		if(result.ok()) {
			right(command);
		} else {
			wrong(command, result.getErrorMessage());
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		mongo.close();
		super.tearDown();
	}
}
