

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class IOUtils {
	
	public static byte[] getFileAsByte(String inputFilePath) {
		byte[] input = null;
		try {
			Path inputPath = Paths.get(inputFilePath);
			input = Files.readAllBytes(inputPath);
		} catch (IOException ioExc) {
			System.out.println("Input file cannot found : " + inputFilePath);
			System.exit(1);
		}
		
		return input;
	}
	
	public static byte[] getSubInput(byte[] input, int threadIndex, int threadSize) {
		int startIndex = threadIndex * threadSize;
		int endIndex = (threadIndex+1) * threadSize;
		
		if(startIndex > input.length-1)
			return new byte[0];
		
		if(endIndex > input.length)
			endIndex = input.length;
		
		byte[] subInput = Arrays.copyOfRange(input, startIndex, endIndex);
		
		return subInput;
	}
	
	public static void createFile(String outputFilePath, byte[] output) {
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(outputFilePath);
			fileOutputStream.write(output);
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createFile(String outputFilePath, List<byte[]> outputList) {
		FileOutputStream fileOutputStream;		
		try {
			fileOutputStream = new FileOutputStream(outputFilePath);
			for (byte[] output : outputList) {
				fileOutputStream.write(output);
			}
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
