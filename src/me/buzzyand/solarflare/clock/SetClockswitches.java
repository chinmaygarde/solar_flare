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

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.Utils;
import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import me.buzzyand.solarflare.text.AirText;

public class SetClockswitches extends MIDlet implements ISwitchListener { 
    private ISwitch sw1, sw2;      // Variables to hold the two switches.
    private int hourcounter = 0;
    private int minutecounter =0;
    
    /**
     * First setup a switch listener to monitor both switches for press or release events.
     * Then call a routine to manually monitor switch 1.
     *
     * Note: if the SPOT is attached with a USB cable, and if NetBeans issues the run to the project
     * or if you use a command shell to do "ant run," you will see the printed output from System.out.
     */
    private void monitorSwitches()throws IOException {
    	
    	
    	
        sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
        sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");

        sw1.addISwitchListener(this);       // enable automatic notification of switches
        sw2.addISwitchListener(this);
        
        System.out.println("Please press switch 1 to set the 'hours' during the next 30 seconds");
        Utils.sleep(30000);     // sleep for 30 seconds

        System.out.println("Time's up.");

        sw1.removeISwitchListener(this);    // disable automatic notification for switch 1

        manuallyMonitorSwitch1();
        
        System.out.println("Good bye");
        notifyDestroyed();  // exit from MIDlet
    }
    
    /**
     * These methods are the "call backs" that are invoked whenever the
     * switch is pressed or released. They are run in a new thread.
     *
     * @param sw the switch that was pressed/released.
     */
    public void switchPressed(SwitchEvent evt) {
        int switchNum = (evt.getSwitch() == sw1) ? 1 : 2;
        System.out.println("Switch " + switchNum + " pressed.");
    }
    
    public void switchReleased(SwitchEvent evt) {
    	BootloaderListenerService.getInstance().start();       // Listen for downloads/commands over USB connection
        EDemoBoard board = EDemoBoard.getInstance();
    	AirText disp = new AirText(board);
    	
    	int switchNum = (evt.getSwitch() == sw1) ? 1 : 2;
        if (switchNum ==1){
        hourcounter++;
        if (hourcounter>23){
        	hourcounter = hourcounter-23;
        }
        String sethourcounter = hourcounter + " ";
        
        // Initialize and start the application
     
        System.out.println("Switch " + switchNum + " released." + "your set value is :" + sethourcounter);
        disp.setColor(255, 0, 0);
        disp.swingThis(sethourcounter, 8);
        }
        else
        {   
        	minutecounter ++;
        	if (minutecounter>60){ minutecounter = minutecounter-60;}
        	String setminutecounter = minutecounter + " ";
        	System.out.println("Switch " + switchNum + " released." + "your set value is :" + setminutecounter);
        	System.out.println("If done press switch 2 to set the 'minutes' during the next 30 seconds");
        	disp.setColor(255, 0, 0);
            disp.swingThis(setminutecounter, 8);
        	
        }
    }

    /**
     * Actively loop waiting for the switch state to change.
     */
    private void manuallyMonitorSwitch1() {
        System.out.println("Please press switch 1 several times");

        for (int i = 1; i <= 4; i++) {     // first catch a few clicks of sw1 manually
            sw1.waitForChange();
            if (sw1.isClosed()){
                System.out.println("Switch 1 pressed.  (" + i + " of 4)");
            } else {
                System.out.println("Switch 1 released.  (" + i + " of 4)");
            }
        }
    }

    /**
     * startApp() is the MIDlet call that starts the application.
     */
    protected void startApp() throws MIDletStateChangeException {
	// Listen for downloads/commands over USB connection
	new com.sun.spot.service.BootloaderListenerService().getInstance().start();
        try {
            monitorSwitches();
        } catch (IOException ex) { //A problem in reading the sensors.
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
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
    
}
