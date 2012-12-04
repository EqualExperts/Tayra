package usingio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class RegularReader {
	private static int count = 1010241024; // 10 MB

	public static void main(String[] args) throws Exception {
		String name = args[0];
		System.out.println("Reading File " + name);

		FileInputStream file = new FileInputStream(name);
		DataInputStream dis = new DataInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));

		long startTime = new Date().getTime();
		System.out.println("Start Time = " + startTime);

		String strLine;
		while ((strLine = br.readLine()) != null) {
			// System.out.println (strLine);
		}
		long endTime = new Date().getTime();
		System.out.println("End Time = " + endTime);
		System.out.println("Time Taken = " + (endTime - startTime));
		System.out.println();
		System.out.println("Reading File is completed");
	}

}
