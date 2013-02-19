package com.ee.tayra.fixtures;

public class Result {
	private Object sourceValue = 0;
	private Object destinationValue = 0;
	
	public Result(Object sourceValue, Object destinationValue) {
		this.sourceValue = sourceValue;
		this.destinationValue = destinationValue;
	}
	
	public String getSourceValue() {
		return sourceValue.toString();
	}
	
	public String getDestinationValue() {
		return destinationValue.toString();
	}
}
