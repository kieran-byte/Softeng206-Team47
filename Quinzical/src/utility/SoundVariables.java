package utility;

public class SoundVariables {

	
	private static boolean _clickSound = true;
	private static double _readSpeed = 1.0;
	private static double _volumeLevel = 1.0;
	
	public static boolean isClickSound() {
		return _clickSound;
	}

	public static void setClickSound(boolean clickSound) {
		SoundVariables._clickSound = clickSound;
	}

	public static double get_readSpeed() {
		return _readSpeed;
	}

	public static void set_readSpeed(double _readSpeed) {
		SoundVariables._readSpeed = _readSpeed;
	}

	public static double get_volumeLevel() {
		return _volumeLevel;
	}

	public static void set_volumeLevel(double _volumeLevel) {
		SoundVariables._volumeLevel = _volumeLevel;
	}
	

}
