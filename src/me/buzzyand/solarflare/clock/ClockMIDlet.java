package me.buzzyand.solarflare.clock;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import me.buzzyand.solarflare.base.SolarFlareComponent;
import me.buzzyand.solarflare.utils.Log;

import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.sensorboard.EDemoBoard;
public class ClockMIDlet extends MIDlet implements ISwitchListener {
	private static final String TAG_TIMER = "tagTimer";
	private static final String TAG_CLOCK = "tagClock";
	
	protected void startApp() throws MIDletStateChangeException {
		Log.log("Application Started");
		Log.printClockHelp();
		setupSwitches();
	}
	private void setupSwitches() {
		EDemoBoard board = EDemoBoard.getInstance();
		ISwitch timerSwitch = board.getSwitches()[EDemoBoard.SW1];
		ISwitch clockSwitch = board.getSwitches()[EDemoBoard.SW2];
		
		timerSwitch.addISwitchListener(this);
		timerSwitch.addTag(TAG_TIMER);
		clockSwitch.addISwitchListener(this);
		clockSwitch.addTag(TAG_CLOCK);
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		Log.log("Destroy App Called");
	}
	
	protected void pauseApp() {
		Log.log("Application Paused");
	}
	
	public void switchPressed(SwitchEvent arg0) {  }
	public void switchReleased(SwitchEvent switchEvent) {
		SolarFlareComponent component = null;
		if(switchEvent.getSwitch().hasTag(TAG_CLOCK)) {
			component = new SolarFlareClock();
		} else if(switchEvent.getSwitch().hasTag(TAG_TIMER)) {
			
		}
		
		if(component != null)
			component.start();
	}
}
