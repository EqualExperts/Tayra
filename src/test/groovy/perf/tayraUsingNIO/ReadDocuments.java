package perf.tayraUsingNIO;

import com.ee.tayra.io.DocumentReader;

public class ReadDocuments {

	public static void main(String[] args) throws Exception {

		String fileName = "C:\\test2\\test.16mb";
		DocumentReader reader = new FileReaderWithNIO(fileName);
		String document = null;
		while((document = reader.readDocument()) != null) {
			System.out.println(document);
		}

//		String documentRead1 = reader.readDocument();
//		System.out.println("document Read:1 - " + documentRead1);
//
//		String documentRead2 = reader.readDocument();
//		System.out.println("document Read:2 - " + documentRead2);
//
//		String documentRead3 = reader.readDocument();
//		System.out.println("document Read:3 - " + documentRead3);
//
//		String documentRead4 = reader.readDocument();
//		System.out.println("document Read:4 - " + documentRead4);
//
//		String documentRead5 = reader.readDocument();
//		System.out.println("document Read:5 - " + documentRead5);
//
//		String documentRead6 = reader.readDocument();
//		System.out.println("document Read:6 - " + documentRead6);
//
//		String documentRead7 = reader.readDocument();
//		System.out.println("document Read:7 - " + documentRead7);
//
//		String documentRead8 = reader.readDocument();
//		System.out.println("document Read:8 - " + documentRead8);

	}

}
