package com.ee.beaver;

public class UpdateDocumentPayload {

  public final String name;
  public final String _id;
  public final String age;

  public UpdateDocumentPayload(String _id, String name, String age) {
	this._id = _id;
	this.name = name;
	this.age = age;
  }
}
