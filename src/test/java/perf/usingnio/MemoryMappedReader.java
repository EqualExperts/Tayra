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
		System.out.println("Reading File " + name);
		File file = new File(name);
		RandomAccessFile memoryMappedFile = new RandomAccessFile(file, "r");

		long startTime = new Date().getTime();
		System.out.println("Start Time = " + startTime);

		// Mapping a file into memory
		MappedByteBuffer out = memoryMappedFile.getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, file.length());

		// Reading from Memory Mapped File
		while (out.hasRemaining()) {
			byte data = out.get();
			// System.out.print((char)data);
		}
		long endTime = new Date().getTime();
		System.out.println("End Time = " + endTime);
		System.out.println("Time Taken = " + (endTime - startTime));
		System.out.println();
		System.out.println("Reading to Memory Mapped File is completed");
	}

}
