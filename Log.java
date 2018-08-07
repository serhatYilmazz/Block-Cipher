
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Log {
	
	private static Writer logFile;
	
	public static void write(String log, long execTime) {
		try {
			logFile = new BufferedWriter(new FileWriter("run.log", true));
			
			logFile.append(log + " " + execTime);
			logFile.append(System.getProperty("line.separator"));
			
			logFile.flush(); 
			logFile.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
