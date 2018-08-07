

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class that execute encryption and decryption with given:
 * <ul>
 * <li>Algorithm: AES or DES</li>
 * <li>Mode: OFB, CBC or CTR</li>
 * <li>Encryption or Decryption</li>
 * <li>Key</li>
 * <li>Initial Vector</li>
 * </ul>
 * All process will be done by thread according to desired mode and thread
 * number.
 *
 */
public class Cryption extends Thread {

	private Cipher cipherObject;
	private IvParameterSpec ivSpec;
	private SecretKeySpec skeySpec;

	private int cryptoMode;

	private byte[] input;
	private byte[] output;

	public Cryption(boolean encrypt, String algorithm, String mode, byte[] key, byte[] initVector, byte[] input) {
		if (encrypt) {
			cryptoMode = Cipher.ENCRYPT_MODE;
		} else {
			cryptoMode = Cipher.DECRYPT_MODE;
		}

		this.input = input;

		try {
			cipherObject = Cipher.getInstance(algorithm + "/" + mode + "/" + "PKCS5PADDING");
			ivSpec = new IvParameterSpec(initVector);
			skeySpec = new SecretKeySpec(key, algorithm);

		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (NoSuchPaddingException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

	public void run() {
		try {
			cipherObject.init(cryptoMode, skeySpec, ivSpec);
			output = cipherObject.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getOutput() {
		return output;
	}

}
