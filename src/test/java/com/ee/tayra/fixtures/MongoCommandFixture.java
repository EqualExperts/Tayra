package com.ee.tayra.fixtures;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import fit.Parse;
import fit.exception.MissingCellsFailureException;
import fitlibrary.DoFixture;

public class MongoCommandFixture extends DoFixture {
  private final MongoClient mongo;
  private DB db;

  public MongoCommandFixture(final MongoClient source) {
    this.mongo = source;
    mongo.getDB("test").command("ismaster");
  }

  public final void useDatabase(final Parse cells) {
    Parse expected = cells.more;
    if (expected == null) {
      throw new MissingCellsFailureException(cells.text()
          + " requires an argument");
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

  public final void run(final Parse cells) {
    Parse command = cells.more;
    if (command == null) {
      throw new MissingCellsFailureException(cells.text()
          + " requires an argument");
    }
    String cmdString = command.text();
    CommandResult result = db.doEval(cmdString);
    if (result.ok()) {
      right(command);
    } else {
      wrong(command, result.getErrorMessage());
    }
  }
}
