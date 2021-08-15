package utility;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;




public class SoundPlayer {
	/**
	 * 
	 * Constructor to create a sound file, also plays the sound file
	 * @param file
	 */
	public SoundPlayer() {
	    //generates the sound clip then plays it
	    try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(new File("./resources/buttonClick.wav")));
	        
	        
	        //plays the click sound if the vairable is true
	        if(SoundVariables.isClickSound()) {
	        	clip.start();
	        }  
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	}

}





