package com.ee.beaver.domain.operation;

public class DropCollectionFailed extends RuntimeException {
	 
	private static final long serialVersionUID = 1L;
    
	public DropCollectionFailed(final String message) {
	    super(message);
	}
}
