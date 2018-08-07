

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.util.Scanner;

/**
 * 
 * Parser class that handle with parsing initial arguments.
 *
 */
public class Parser {

	private boolean encrypt;
	private int threadNumber = 1;
	private String algorithm;
	private String mode;
	private String inputPath;
	private String outputPath;
	private byte[] key;
	private byte[] initVector;

	public Parser(String[] args) {

		try {
		
			this.encrypt = initCryptoMode(args);

			this.inputPath = getArgument(args, "-i");
			this.outputPath = getArgument(args, "-o");
			this.algorithm = args[args.length - 3];
			this.mode = args[args.length - 2];

			parseKeyFile(args[args.length - 1]);

			// Thread number must be used when CTR mode selected and it is optional.
			if (mode.equals("CTR")) {
				String temp = getArgument(args, "-p");
				if (temp != null)
					this.threadNumber = new Integer(temp);
			}

			controlKeyCompaction();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}
	
	/**
	 * Since we have used java.security, initial values are declared as:
	 * <ul>
	 * 		<li>DES algorithm accepts 8 byte initial vector and key.</li>
	 * 		<li>AES algorithm accepts 16 byte initial vector and key.</li>
	 * </ul>
	 * @throws InvalidKeyException
	 */
	private void controlKeyCompaction() throws InvalidKeyException {
		if (algorithm.equals("DES")) {
			if (key.length != 8 || initVector.length != 8) {
				throw new InvalidKeyException(
						"Keys length or initial vector lentgh is not appropriate for algorithm DES");
			}
		} else if (algorithm.equals("AES")) {
			if (!(key.length == 16 || key.length == 24 || key.length == 32) || initVector.length != 16) {
				throw new InvalidKeyException(
						"Keys length or initial vector lentgh is not appropriate for algorithm AES");
			}
		}
	}
	
	private boolean initCryptoMode(String[] args) throws InvalidParameterException {
		if(args[0].equals("-e"))
			return true;
		else if (args[0].equals("-d"))
			return false;

		throw new InvalidParameterException("Invalid operation mode : " + args[0]);
	}
	
	/**
	 * 
	 * @param args console values that will be parsed.
	 * @param tag value that will be matched relevant parameter.
	 * @return null if any relevant parameter could not be found.
	 */
	private String getArgument(String[] args, String tag) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(tag))
				return args[i + 1];
		}
		return null;
	}
	
	/**
	 * Key file format should be <b>^<i>Key - Initial Vector</i>$</b>
	 * @param filePath that include a key and initial vector. 
	 */
	private void parseKeyFile(String filePath) {
		Scanner input;
		File keyFile = new File(filePath);

		try {
			input = new Scanner(keyFile);

			if (input.hasNext()) {
				String line = input.nextLine();

				String[] token = line.split(" - ");

				this.initVector = token[0].getBytes();
				this.key = token[1].getBytes();

			}

		} catch (FileNotFoundException e) {
			System.out.println("Key file cannot found : " + filePath);
			System.exit(1);
		}
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getMode() {
		return mode;
	}

	public String getInputPath() {
		return inputPath;
	}

	public String getOutputPath() {
		File inputFile = new File(inputPath);
		String path = inputFile.getAbsolutePath();
		String name = inputFile.getName();
		String outputAbsolutePath = path.replace(name, this.outputPath);
		return outputAbsolutePath;
	}

	public byte[] getKey() {
		return key;
	}

	public byte[] getInitVector() {
		return initVector;
	}
	
	/**
	 * Overwritten method that will be used for Logging.
	 */
	public String toString() {
		String str = inputPath + " " + outputPath;

		if (encrypt) {
			str += " enc ";
		} else {
			str += " dec ";
		}

		str += algorithm + " " + mode;
		return str;
	}
}
