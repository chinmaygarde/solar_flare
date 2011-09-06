package me.buzzyand.solarflare.clock;

import java.util.Timer;
import java.util.TimerTask;

import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.sensorboard.EDemoBoard;

import me.buzzyand.solarflare.base.SolarFlareComponent;
import me.buzzyand.solarflare.text.AirText;

public class SolarFlareTimer extends SolarFlareComponent implements ISwitchListener {
	private EDemoBoard board = EDemoBoard.getInstance(); 
	private AirText text = new AirText(board); 

	private boolean isInMode = true;
	private long SECOND_ONE = 1000;
	private int seconds = 0;
	private boolean isRunning = false;
	
	public void start() {
		ISwitch timerSwitch = board.getSwitches()[EDemoBoard.SW1];
		timerSwitch.addISwitchListener(this);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				if(isRunning)
					seconds ++;
			}}, 0, SECOND_ONE);
		
		DisplayThread thread = new DisplayThread();
		thread.start();
	}

	public void switchPressed(SwitchEvent switchEvent) {  }

	public void switchReleased(SwitchEvent switchEvent) {
		isRunning = !isRunning;
	}
	
	private class DisplayThread extends Thread {
		public void run() {
			while(isInMode) {
				if(seconds % 2 == 1)
					text.setBlueColor();
				else
					text.setGreenColor();
				text.swingThis(seconds + "", 2);
			}
		}
	}
}
