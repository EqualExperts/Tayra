package usingnio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

public class MemoryMappedWriter {
    private static int count = 1010241024; //10 MB

    public static void main(String[] args) throws Exception {
        String name = args[0];
        File file = new File(name);
		RandomAccessFile memoryMappedFile = new RandomAccessFile(file, "rw");

        //Mapping a file into memory
		long length = file.length();
        FileChannel channel = memoryMappedFile
        					.getChannel();
		MappedByteBuffer out = channel
        					.map(FileChannel.MapMode.READ_WRITE, 0, length);

        //Writing to Memory Mapped File
        int counter = 0;
        List<String> data = Arrays.asList("A", "B", "C");
		while(counter < length){
            byte[] character = data.get(counter++ % data.size()).getBytes();
			out.put(character);
        }
    }
}


