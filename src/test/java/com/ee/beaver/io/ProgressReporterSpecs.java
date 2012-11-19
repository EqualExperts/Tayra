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
public class ProgressReporterSpecs {
	
	@Mock
	private Writer mockExceptionDocumentsWriter;

	@Mock
	private PrintWriter mockConsole;

	private ProgressReporter reporter;
	
	@Before
	public void givenThereExists() {
		reporter = new ProgressReporter(mockExceptionDocumentsWriter, mockConsole);
	}

	@Test
	public void writesExceptioningDocuments() throws IOException {
		//Given
		String document = "{}";
		Throwable problem = null;
		
		//When
		reporter.onWriteFailure(document , problem);
		
		//Then    
		verify(mockExceptionDocumentsWriter).append(document);
		verify(mockConsole, never()).printf("");
	}

	@Test
	public void incrementsReadDocumentsCountAfterASuccessfulRead() {
		//Given
		String document = "{}";
		
		//When
		reporter.onReadSuccess(document);
		
		//Then  
		assertThat(reporter.getDocumentsRead(), is(1));
	}
	
	@Test
	public void incrementsWrittenDocumentsCountAfterASuccessfulWrite() {
		//Given
		String document = "{}";
		
		//When
		reporter.onWriteSuccess(document);
		
		//Then  
		assertThat(reporter.getDocumentsWritten(), is(1));
	}
	
	@Test
	public void incrementsExceptionDocumentsCountAfterAFailedWrite() {
		//Given
		String document = "{}";
		Throwable ignore = null;
		
		//When
		reporter.onWriteFailure(document , ignore);
		
		//Then  
		assertThat(reporter.getExceptionDocuments(), is(1));
	}
	
	@Test
	public void doesNotIncrementWrittenDocumentsCountAfterAFailedWrite() {
		//Given
		String document = "{}";
		Throwable ignore = null;
		
		//When
		reporter.onWriteFailure(document , ignore);
		
		//Then  
		assertThat(reporter.getDocumentsWritten(), is(0));
	}

	@Test
	public void doesNotIncrementsExceptionDocumentsCountOnASuccessfulWrite() {
		//Given
		String document = "{}";
		
		//When
		reporter.onWriteSuccess(document);
		
		//Then  
		assertThat(reporter.getExceptionDocuments(), is(0));
	}

	@Test
	public void doesNotIncrementReadDocumentsCountOnAFailedRead() {
		//Given
		String document = null;
		Throwable ignore = new Throwable("");
		
		//When
		reporter.onReadFailure(document, ignore);
		
		//Then  
		assertThat(reporter.getDocumentsRead(), is(0));
	}
	
	@Test
	public void shoutsWhenThereIsAFailedRead() {
		//Given
		String document = null;
		Throwable problem = new Throwable("Connection Broken!");
		
		//When
		reporter.onReadFailure(document, problem);
		
		//Then  
		verify(mockConsole).printf("===> Unable to Read Documents: %s\r", problem.getMessage());
	}
	
	@Test
	public void doesNotWriteExceptioningDocumentsWhenNoDocumentIsAvailable() throws IOException {
		//When
		String noDocument = null;
		Throwable problem = mock(Throwable.class);
		reporter.onWriteFailure(noDocument , problem);
		
		//Then    
		verify(mockExceptionDocumentsWriter, never()).append(noDocument);
		verify(mockConsole, never()).printf("");
		verify(problem).printStackTrace(mockConsole);
		assertThat(reporter.getExceptionDocuments(), is(0));
  }
	
	@Test
	public void shoutsWhenThereIsAProblemWritingExceptioningDocuments() throws Exception {
		//Given
		String document = "{}";
		IOException problem = new IOException("Disk full");
		given(mockExceptionDocumentsWriter.append(document)).willThrow(problem);
		
		//When
		reporter.onWriteFailure(document , problem);
		
		//Then    
		verify(mockConsole).printf("===> Unable to Write Exceptioning Document(s) %s", problem.getMessage());
		assertThat(reporter.getExceptionDocuments(), is(1));
	}
	
	@Test
	public void writesDocumentsOnlyWhenExceptionsWriterIsDefined() throws Exception {
		//Given
		reporter = new ProgressReporter(null, mockConsole);
		String document = "{}";
		IOException problem = new IOException("Disk full");

		//When
		reporter.onWriteFailure(document , problem);

		//Then    
		verify(mockConsole).printf("===> Unable to Write Document(s) %s", problem.getMessage());
	}
}
