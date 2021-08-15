package utility;

/**
 * HelperThread class creates a background thread which helps to handle
 * tts without creating issues for GUI 
 *
 */
public class TTSThread extends Thread {
	private String _message;
	private Double _speed;
	
	public TTSThread(String message, double speed) {
		_message = message;
		_speed = speed;
	}
	
	public void run() {
		TextToSpeech.sayText(_message, _speed);
	}
	
	
}
