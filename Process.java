import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <h1>Controlling and start processing all encryption and decryption.</h1>
 *
 */
public class Process {
	private Parser parser; // Object that include the necessary argument.

	public Process(Parser parser) {
		this.parser = parser;
	}

	/**
	 * Initial declarations and setting variables just before execution.
	 */
	public void run() {
		long executingTime = 0;

		/*
		 * If it is wanted to encrypt or decrypt in CTR mode with one thread, it
		 * can be executed with a single thread.
		 */
		if (parser.getMode().equals("CTR") && parser.getThreadNumber() > 1) {
			executingTime = runThreads();
		} else {
			executingTime = runSingle();
		}

		Log.write(parser.toString(), executingTime);

	}

	/**
	 * <p>Reading the input file as a byte with {@link IOUtils}. Since this function is
	 * responsible only a single operation, necessary {@link Cryption} object is created and 
	 * it processes.</p>
	 * <p>After process is completed, the encrpyted or decrypted data is written to
	 * output file.</p>
	 * @return execution time
	 */
	private long runSingle() {
		byte[] input = IOUtils.getFileAsByte(parser.getInputPath());

		Cryption cryption = new Cryption(parser.isEncrypt(), parser.getAlgorithm(), parser.getMode(), parser.getKey(),
				parser.getInitVector(), input);

		long start = System.currentTimeMillis();
		// Run without creating new thread.
		cryption.run();
		long end = System.currentTimeMillis();

		IOUtils.createFile(parser.getOutputPath(), cryption.getOutput());

		return end - start;

	}
	
	/**
	 * This function is explained step by step;
	 * <ol>
	 * 		<li>Reading input file as byte array.</li>
	 * 		<li>Since the output of all threads' after processed result will be kept in
	 * a byte array, a List of byte[] initialized.</li>
	 * 		<li>To start and finish all thread, a List of Thread is used.</li>
	 * 		<li>This function is only call for multithread CTR mode;
	 * 			<ul>
	 * 				<li>CTR mode first uses read (from key file) initial vector 
	 * as initial vector. </li>
	 * 			<li>In each block encryption or decryption, it uses incremental value of initial vector
	 * .</li>
	 * 			<li>To determine block size for a thread, {@link OctalComplement} is used.(AES: 16Byte, DES: 8Byte)</li>
	 * 		    </ul>
	 * </li>
	 * 		<li>In each block initial vector is incremented by one. To determine how much increment is needed, counter value is specified by block size of input file is divided by algorithm's blocks size</li>
	 * 		<li>To specify which block will be processed {@link IOUtils} of getSubInput() is used. After creating each thread, the threads will be added to List of Thread.
	 * To provide integrity of process, initial vector is incremented by counter value</li>
	 * 		<li>Threads are started processing.</li>
	 * 		<li>Threads' process are finished and their part of process will be kept in List of byte[]</li>
	 * 		<li>The output file is created at the end of the processes. Thus reaching the IO will be more efficient.</li>
	 * </ol>
	 * @return execution time
	 */
	private long runThreads() {
		//1
		byte[] input = IOUtils.getFileAsByte(parser.getInputPath());
		//2
		List<byte[]> outputList = new ArrayList<>();
		//3
		List<Cryption> threadList = new ArrayList<>();
		
		//4
		int blockSize = OctalComplement.AES(input.length, parser.getThreadNumber());
		//5
		int counter = blockSize / OctalComplement.MULTIPLES_OF_SIXTEEN;
		if (parser.getAlgorithm().equals("DES")) {
			blockSize = OctalComplement.DES(input.length, parser.getThreadNumber());
			counter = blockSize / OctalComplement.MULTIPLES_OF_EIGHT;
		}
		
		byte[] initVector = parser.getInitVector();
		
		//6
		for (int i = 0; i < parser.getThreadNumber(); i++) {
			byte[] subInput = IOUtils.getSubInput(input, i, blockSize);
			Cryption cryption = new Cryption(parser.isEncrypt(), parser.getAlgorithm(), parser.getMode(),
					parser.getKey(), initVector, subInput);
			threadList.add(cryption);
			initVector = increment(initVector, counter);

		}
		
		long start = System.currentTimeMillis();
		
		//7
		for (Cryption cryption : threadList) {
			cryption.start();
		}
		
		//8
		for (Cryption cryption : threadList) {
			try {
				cryption.join();
				byte[] subOutput = cryption.getOutput();
				outputList.add(subOutput);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		long end = System.currentTimeMillis();
		
		//9
		IOUtils.createFile(parser.getOutputPath(), outputList);

		return end - start;
	}
	
	/**
	 * To increment byte value of initial vector BigInteger is used.
	 * @param bytes initial vector's versions.
	 * @param counter how much '1' is added to initial vector.
	 * @return
	 */
	private byte[] increment(byte[] bytes, int counter) {
		BigInteger bigIntValue = new BigInteger(bytes);
		bigIntValue = bigIntValue.add(BigInteger.valueOf(counter));
		return bigIntValue.toByteArray();
	}
}
