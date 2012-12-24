package com.ee.beaver.io;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class RotatingFileWriter extends Writer {

	private PatternLayout layout;
	private RollingFileAppender appender;
	private Logger logger;

	public RotatingFileWriter(String recordToFile, String fSize, int fMax)
			throws IOException {
		super();
		layout = new PatternLayout();
		appender = new RollingFileAppender(layout, recordToFile, false);
		if (fSize != null) {
			appender.setMaxFileSize(fSize);
		}
		if (fMax >= 0) {
			appender.setMaxBackupIndex(fMax);
		}
		appender.setImmediateFlush(true);
//		appender.rollOver();
		logger = Logger.getLogger(recordToFile);
		logger.removeAllAppenders();
		logger.setAdditivity(false);
		logger.setLevel(Level.INFO);
		logger.addAppender(appender);

	}

	@Override
	public void write(String document, int off, int len) throws IOException {
		logger.info(document);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		appender.close();
	}

	@Override
	public void write(char[] document, int off, int len) throws IOException {
		write(document, off, len);
	}

}
