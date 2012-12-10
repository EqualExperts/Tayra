package usingnio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import com.sun.corba.se.impl.ior.ByteBuffer;

public final class MemoryMappedWriter {
	private static final int BUFFER_SIZE = 67108864;

	private MemoryMappedWriter() {

	}

	public static void main(final String[] args) throws Exception {
		String fileToBeRead = args[0];
		String fileSize = args[1];
		String unit = args[2];

		String directory = "c:\\test\\";
		String targetFileName = "GeneratedFile." + fileSize + unit;

		//Target File
		File target = new File(directory, targetFileName);
		RandomAccessFile targetFile = new RandomAccessFile(target, "rw");
		targetFile.seek(0L);
		MappedByteBuffer targetBuffer;

		// Source File
		File source = new File(fileToBeRead);
		RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
		sourceFile.seek(0L);

		int readSize = 0;
		long startTime = new Date().getTime();
		MappedByteBuffer sourceBuffer;

		while (sourceFile.getFilePointer() < sourceFile.length()) {
			readSize = (int) Math.min(BUFFER_SIZE, sourceFile.length()
					- sourceFile.getFilePointer());
			sourceBuffer = sourceFile.getChannel().map(
					FileChannel.MapMode.READ_ONLY, sourceFile.getFilePointer(),
					readSize);
			targetBuffer = targetFile.getChannel().map(
					FileChannel.MapMode.READ_WRITE, targetFile.getFilePointer(),
					readSize);

			// Reading from Memory Mapped File
			while (sourceBuffer.hasRemaining()) {
				byte data = sourceBuffer.get();
				targetBuffer.put(data);
			}

			targetBuffer.compact();
			targetBuffer.clear();

			sourceFile.seek(sourceFile.getFilePointer() + readSize);
		}
		long endTime = new Date().getTime();
		sourceFile.close();
		targetFile.close();
		System.out.printf("%s, %d\n", target.getName(), (endTime - startTime));
		}
}
