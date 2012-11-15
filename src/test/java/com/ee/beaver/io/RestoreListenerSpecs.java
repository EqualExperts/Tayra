package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.Writer;

import org.junit.Test;

public class RestoreListenerSpecs {
	@Test
	public void writesExceptioningDocuments() throws Exception {
		//Given
		Writer mockWriter = mock(Writer.class);
		RestoreListener restoreListener = new RestoreListener(mockWriter);
		
		//When
		String document = "{}";
		Throwable ignore = null;
		restoreListener.onWriteFailure(document , ignore);
		
		//Then    
		verify(mockWriter).append(document);
  }
}
