package usingnioandio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class NIOReaderAndRegularIOWriter {

	private static final int BUFFER_SIZE = 1048576; // Default : 8 KB

	public static void main(String[] args) throws Exception {
		String fileToBeRead = args[0];
		String fileSize = args[1];
		String unit = args[2];

		String directory = "c:\\test\\";
		String targetFileName = "GeneratedFile." + fileSize + unit;

		// Source File - NIO Read
		File source = new File(fileToBeRead);
		RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
		sourceFile.seek(0L);

		// Target File - BufferedWrite
		File target = new File(directory, targetFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(target), BUFFER_SIZE);

		int readSize = 0;
		long startTime = new Date().getTime();
		MappedByteBuffer sourceBuffer;

		while (sourceFile.getFilePointer() < sourceFile.length()) {
			readSize = (int) Math.min(BUFFER_SIZE, sourceFile.length()
					- sourceFile.getFilePointer());
			sourceBuffer = sourceFile.getChannel().map(
					FileChannel.MapMode.READ_ONLY, sourceFile.getFilePointer(),
					readSize);
			while (sourceBuffer.hasRemaining()) {
				byte input = sourceBuffer.get();
				String str = new String(new byte[] {input});
				bw.append(str);
			}
			bw.flush();
			sourceFile.seek(sourceFile.getFilePointer() + readSize);
		}
		long endTime = new Date().getTime();
		sourceFile.close();
		bw.close();
		System.out.printf("%s, %d\n", target.getName(), (endTime - startTime));
	}

}
