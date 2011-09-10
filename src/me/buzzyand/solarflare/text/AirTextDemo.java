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

package me.buzzyand.solarflare.text;
//import java.text.SimpleDateFormat;
import java.util.*;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.service.BootloaderListenerService;
import javax.microedition.midlet.MIDletStateChangeException;
import com.sun.spot.sensorboard.EDemoBoard;

/**
 * AirText Demo
 *
 * Deploy on a Spot with an eDemoBoard attached. When you shake the
 * Sun SPOT back and forth, the RGB LEDs will flash in a pattern
 * that will magically spell out words in the air.
 *
 * @author roger (modifications by vipul)
 */

public class AirTextDemo extends javax.microedition.midlet.MIDlet {
    
    protected void startApp() throws MIDletStateChangeException {
        BootloaderListenerService.getInstance().start();       // Listen for downloads/commands over USB connection
        System.out.println("StartApp");
    	 EDemoBoard board = EDemoBoard.getInstance(); 
        // Initialize and start the application
        AirText disp = new AirText(board);
        // Main loop of the application
       
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        
        
       // Date date = new Date();
       // long yourmilliseconds;
       
        while(true){
         //yourmilliseconds = System.currentTimeMillis();
         //date.setTime(yourmilliseconds);
         
        	int hour = cal.get(Calendar.HOUR_OF_DAY);
        	int minutes = cal.get(Calendar.MINUTE);
        	int seconds = cal.get(Calendar.SECOND);
                	
        	String dateString = hour + ":" + minutes + ":" +seconds;
        	System.out.println(dateString);
        	disp.setColor(255, 0, 0);
         disp.swingThis(dateString.substring(0, 2), 17);
         disp.setColor(0, 255, 0);
         disp.swingThis(dateString.substring(3, 5), 8);
         disp.setColor(0,0 , 255);
         disp.swingThis(dateString.substring(6, 8), 3);
         
    }
       
     
    }
    
    protected void pauseApp() {
    }
    
    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * 
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *    cleanup and release all resources. If false the MIDlet may throw
     *    MIDletStateChangeException  to indicate it does not want to be destroyed
     *    at this time.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
        leds.setOff();
    }
}
