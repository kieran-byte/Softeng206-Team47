package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToSpeech {
	

	public static synchronized void sayText(String message, double speed) {
		try {
			
			//String to be written into festival .scm file
			String festivalString =  "(voice_akl_nz_jdt_diphone)\n" + 
					"(Parameter.set 'Duration_Stretch " + speed + ")\n"+
					"(SayText \"" + message + "\")";
				
			//Writes string into temporary file
			File file = new File("./gameData/speechString.scm");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(festivalString.getBytes());
			fos.flush();
			fos.close();
			
			//Runs bash script to perform text to speech
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", "festival -b gameData/speechString.scm");
			Process _process = pb.start();
			_process.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
