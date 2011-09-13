package me.buzzyand.solarflare.comm;

public interface TimeInOutCallback {
	public void updateTimerCorrections(int hourCorrection, int minuteCorrection);
	public String stringToBroadcast();
}
