
/**
 * 
 * <h1>Utility class that return value for block size. It is mostly used for CTR mode encryption 
 * and decryption in the program.</h1>
 *
 */
public class OctalComplement {
	public static final int MULTIPLES_OF_EIGHT = 8;
	public static final int MULTIPLES_OF_SIXTEEN = 16;
	
	
	
	public static int DES(int dataSize, int numberOfThread){
		int result = (int)Math.ceil((double)(dataSize / (double)numberOfThread));
		int i = 1;
		
		/*
		 * For example:
		 * Our data size is 58.
		 * Desired Number of thread = 3
		 * 58 / 3 = 19.33 ~ 20 (We should round it up)
		 * In the first loop i = 1.
		 * Difference between 20 and multiple of i'th multiple of eight = 20 - i*8 = 12
		 * First we control (12 < i*8)?. Answer is no. (This condition should satisfy to choose correct multiple of eight to
		 * get correct block size.) 
		 * Second we control if it is already a multiple of eight. If it is, block size is .
		 * Third if the first condition is not satisfied then try again with a new multiple of eight.
		 */
		while(true){
			int how = result - i * MULTIPLES_OF_EIGHT;
			if(how < MULTIPLES_OF_EIGHT && result < i * MULTIPLES_OF_EIGHT){ //We want to round the block size one step up in multiple of eight.
				break;
			}
			else if(how == 0){// if it is already a multiple of eight
				break;
			}
			else if(how > MULTIPLES_OF_EIGHT || result > i * MULTIPLES_OF_EIGHT)
				i++;
		}
		return i * MULTIPLES_OF_EIGHT;
	}
	
	public static int AES(int dataSize, int numberOfThread){
		int result = (int)Math.ceil((double)(dataSize / (double)numberOfThread));
		int i = 1;

		while(true){
			int how = result - i * MULTIPLES_OF_SIXTEEN;
			if(how < MULTIPLES_OF_SIXTEEN && result < i * MULTIPLES_OF_SIXTEEN){ //We want to round the block size one step up in multiple of sixteen.
				break;
			}
			else if(how == 0){// if it is already a multiple of eight
				break;
			}
			else if(how > MULTIPLES_OF_SIXTEEN || result > i * MULTIPLES_OF_SIXTEEN)
				i++;
		}
		return i * MULTIPLES_OF_SIXTEEN;
	}
}
