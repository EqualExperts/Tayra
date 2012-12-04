package usingnio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class MemoryMappedReader {
//	private static int count = 1010241024; // 10 MB

	public static void main(String[] args) throws Exception {
		String name = args[0];
		File file = new File(name);
		RandomAccessFile memoryMappedFile = new RandomAccessFile(file, "r");

		long startTime = new Date().getTime();
		// Mapping a file into memory
		MappedByteBuffer out = memoryMappedFile.getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, file.length());

		// Reading from Memory Mapped File
		while (out.hasRemaining()) {
			byte data = out.get();
			// System.out.print((char)data);
		}
		long endTime = new Date().getTime();
		System.out.printf("%s, %d\n", file.getName(), (endTime - startTime));
	}

}
