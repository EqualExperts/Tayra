package usingio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

public final class RegularIOWriter {

	private static final int TIMES = 1;
	private static final int ONE_KB = 1024;
	private static final String NEW_LINE = System.getProperty("line.separator");

	private RegularIOWriter() {
	}

	public static void main(final String[] args) throws Exception {

		String fileToBeRead = args[0];
		String fileSize = args[1];
		String unit = args[2];

		int bufferSize = (TIMES * ONE_KB * ONE_KB);

		// Target File
		String directory = "c:\\test\\";
		String targetFileName = "GeneratedFile." + fileSize + unit;
		File target = new File(directory, targetFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(target), bufferSize);

		// Source File
		File source = new File(fileToBeRead);
		BufferedReader br = new BufferedReader(new FileReader(source), bufferSize);

		long startTime = new Date().getTime();
		String strLine = br.readLine();
		while ((strLine = br.readLine()) != null) {
			bw.append(strLine);
			bw.append(NEW_LINE);
		}
		bw.flush();
		bw.close();

		long endTime = new Date().getTime();
		System.out.printf("%s, %d\n", targetFileName, (endTime - startTime));
	}
}
