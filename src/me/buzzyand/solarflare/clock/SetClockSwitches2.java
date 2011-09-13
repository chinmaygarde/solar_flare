/*
 * Copyright (c) 2006-2010 Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

package me.buzzyand.solarflare.clock;

/*
 * SetClockswitches.java
 *
 * Sets the time of the clock to the required time by using switches SW1 and SW2
 *    This app illustrates both a simple use of waiting for a switch to change state,
 *    and a call back style of using the switches, as in the listener-notifier
 *   
 */

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import me.buzzyand.solarflare.comm.TimeInOutCallback;
import me.buzzyand.solarflare.comm.TimeInOutManager;
import me.buzzyand.solarflare.text.AirText;

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.service.BootloaderListenerService;

public class SetClockSwitches2 extends MIDlet implements ISwitchListener, TimeInOutCallback {
	private ISwitch sw1, sw2; // switches
	private int hourCorrection = 0; // correction used to update hours
	private int minuteCorrection = 0; // correction used to update minutes
	private Calendar cal = Calendar.getInstance();

	private void app() throws IOException {
		sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
		sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");

		sw1.addISwitchListener(this); // enable automatic notification of switches
		sw2.addISwitchListener(this);

		System.out
				.println("Press SW1 to set 'hours' and SW2 to set 'minutes'.");

		AirText disp = new AirText();

		while (true) {
			updateClock();

			disp.setColor(255, 0, 0);
			disp.swingThis(cal.get(Calendar.HOUR) + "", 10);
			disp.setColor(0, 255, 0);
			disp.swingThis(cal.get(Calendar.MINUTE) + "", 10);
			disp.setColor(0, 0, 255);
			disp.swingThis(cal.get(Calendar.SECOND) + "", 10);
		}
	}

	/**
	 * Update the calendar with current time plus the provided corrections.
	 * 
	 */
	private void updateClock() {
		cal.setTime(new Date(System.currentTimeMillis() + hourCorrection * 60
				* 60 * 1000 + minuteCorrection * 60 * 1000));
	}

	/**
	 * These methods are the "call backs" that are invoked whenever the switch
	 * is pressed or released. They are run in a new thread.
	 * 
	 * @param sw
	 *            the switch that was pressed/released.
	 */
	public void switchPressed(SwitchEvent evt) {
		int switchNum = (evt.getSwitch() == sw1) ? 1 : 2;
		System.out.println("Switch " + switchNum + " pressed.");
	}

	public void switchReleased(SwitchEvent evt) {
		int switchNum = (evt.getSwitch() == sw1) ? 1 : 2;

		if (switchNum == 1) {
			// set 'hours'
			hourCorrection += 1;
			if (hourCorrection >= 12) {
				hourCorrection = 0;
			}
			System.out.println("Hour updated to " + cal.get(Calendar.HOUR));
		} else {
			// set 'minutes'
			minuteCorrection += 1;
			if (minuteCorrection >= 60) {
				minuteCorrection = 0;
			}
			System.out.println("Minute updated to " + cal.get(Calendar.MINUTE));
		}
		
		updateClock();
		broadcastCorrection();
	}

	private void broadcastCorrection() {
		
	}

	/**
	 * startApp() is the MIDlet call that starts the application.
	 */
	protected void startApp() throws MIDletStateChangeException {
		// Listen for downloads/commands over USB connection
		BootloaderListenerService.getInstance().start();
		TimeInOutManager manager = new TimeInOutManager(this);
		manager.startListening();
		try {
			app();
		} catch (IOException ex) { // A problem in reading the sensors.
			ex.printStackTrace();
		}
	}

	/**
	 * This will never be called by the Squawk VM.
	 */
	protected void pauseApp() {
	}

	/**
	 * Called if the MIDlet is terminated by the system.
	 * 
	 * @param unconditional
	 *            If true the MIDlet must cleanup and release all resources.
	 */
	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
	}

	public void updateTimerCorrections(int pHourCorrection, int pMinuteCorrection) {
		hourCorrection = pHourCorrection;
		minuteCorrection = pMinuteCorrection;
		updateClock();
	}

	public String stringToBroadcast() {
		return hourCorrection + ":" + minuteCorrection;
	}
}
