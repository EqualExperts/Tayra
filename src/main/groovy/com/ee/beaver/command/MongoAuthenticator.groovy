package com.ee.beaver.command

import java.io.PrintWriter;

import com.mongodb.Mongo
import com.mongodb.MongoException

class MongoAuthenticator implements Authenticator {
	
  private Mongo mongo;

  public MongoAuthenticator(final Mongo mongo) {
	  this.mongo = mongo;
  }
  
  private boolean onUnsecureDB() {
	  try {
		  mongo.databaseNames
		  return true
	  } catch (MongoException e) {
		  return false
	  }
  }
  
  public boolean authenticate(String username, String password) {
	  if(onUnsecureDB()) {
		  return false
	  }
	  if(!username) {
		  throw new MongoException('Username cannot be empty')
	  } 
	  if(!password) {
		  throw new MongoException('Password cannot be empty')
	  }
	  if(!mongo.getDB('admin').authenticate(username, password.toCharArray())) {
		  throw new MongoException("Authentication Failed to $mongo.address.host")
	  }
	  true
  }
}
