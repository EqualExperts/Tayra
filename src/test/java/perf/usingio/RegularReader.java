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
		File file = new File(name);
		BufferedReader br = new BufferedReader(new FileReader(file));

		long startTime = new Date().getTime();
		String strLine = null;
		while ((strLine = br.readLine()) != null) {
			// System.out.println (strLine);
		}
		long endTime = new Date().getTime();
		System.out.printf("%s, %d\n", file.getName(), (endTime - startTime));
	}

}
