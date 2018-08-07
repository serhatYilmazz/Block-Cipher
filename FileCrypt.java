
/**
 * 
 * @authors Serhat YILMAZ, Omer SAHIN
 * 			21607858, 21328433
 *
 *REFERENCES:
 *https://stackoverflow.com/questions/7642871/get-updated-iv-from-cipher-after-encrypting-bytes
 */
public class FileCrypt {

	public static void main(String[] args) {
		Parser parser = new Parser(args);
		Process process = new Process(parser);
		process.run();
	}

}
