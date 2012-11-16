package com.ee.beaver.io;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestoreListenerSpecs {
	
	@Mock
	private Writer mockWriter;

	@Mock
	private PrintWriter mockProgressWriter;

	private RestoreListener restoreListener;
	
	@Before
	public void givenThereExists() {
		restoreListener = new RestoreListener(mockWriter, mockProgressWriter);
	}

	@Test
	public void writesExceptioningDocuments() throws IOException {
		//Given
		String document = "{}";
		Throwable problem = null;
		
		//When
		restoreListener.onWriteFailure(document , problem);
		
		//Then    
		verify(mockWriter).append(document);
		verify(mockProgressWriter, never()).printf("");
	}

	@Test
	public void incrementsReadDocumentsCountAfterASuccessfulRead() {
		//Given
		String document = "{}";
		
		//When
		restoreListener.onReadSuccess(document);
		
		//Then  
		assertThat(restoreListener.getDocumentsRead(), is(1));
	}
	
	@Test
	public void incrementsWrittenDocumentsCountAfterASuccessfulWrite() {
		//Given
		String document = "{}";
		
		//When
		restoreListener.onWriteSuccess(document);
		
		//Then  
		assertThat(restoreListener.getDocumentsWritten(), is(1));
	}
	
	@Test
	public void incrementsExceptionDocumentsCountAfterAFailedWrite() {
		//Given
		String document = "{}";
		Throwable ignore = null;
		
		//When
		restoreListener.onWriteFailure(document , ignore);
		
		//Then  
		assertThat(restoreListener.getExceptionDocuments(), is(1));
	}
	
	@Test
	public void doesNotIncrementWrittenDocumentsCountAfterAFailedWrite() {
		//Given
		String document = "{}";
		Throwable ignore = null;
		
		//When
		restoreListener.onWriteFailure(document , ignore);
		
		//Then  
		assertThat(restoreListener.getDocumentsWritten(), is(0));
	}

	@Test
	public void doesNotIncrementsExceptionDocumentsCountOnASuccessfulWrite() {
		//Given
		String document = "{}";
		
		//When
		restoreListener.onWriteSuccess(document);
		
		//Then  
		assertThat(restoreListener.getExceptionDocuments(), is(0));
	}

	@Test
	public void doesNotIncrementsReadDocumentsCountOnAFailedRead() {
		//Given
		String document = "{}";
		Throwable ignore = null;
		
		//When
		restoreListener.onReadFailure(document, ignore);
		
		//Then  
		assertThat(restoreListener.getDocumentsRead(), is(0));
	}
	
	@Test
	public void doesNotWriteExceptioningDocumentsWhenNoDocumentIsAvailable() throws IOException {
		//When
		String noDocument = null;
		Throwable problem = mock(Throwable.class);
		restoreListener.onWriteFailure(noDocument , problem);
		
		//Then    
		verify(mockWriter, never()).append(noDocument);
		verify(mockProgressWriter, never()).printf("");
		verify(problem).printStackTrace(mockProgressWriter);
  }
	
	@Test
	public void shoutsWhenThereIsAProblemWritingExceptioningDocuments() throws Exception {
		//Given
		String document = "{}";
		IOException problem = new IOException("Disk full");
		given(mockWriter.append(document)).willThrow(problem);
		
		//When
		restoreListener.onWriteFailure(document , problem);
		
		//Then    
		verify(mockWriter).append(document);
		verify(mockProgressWriter, never()).printf("");
	}
}
