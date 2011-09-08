package me.buzzyand.solarflare.clock;

import me.buzzyand.solarflare.base.SolarFlareComponent;
import com.sun.spot.peripheral.*;
import com.sun.spot.util.*;

public class SolarFlareClock extends SolarFlareComponent implements TimerCounterBits {

	 public double measureInterval() {
		 IAT91_TC timer = Spot.getInstance().getAT91_TC(0); // Get a Timer Counter
		 timer.configure(TC_CAPT | TC_CLKS_MCK32); // Use fast clock speed
		 timer.enableAndReset();  // Start counting
		 // interval to measure ...
		 int cntr = timer.counter(); // Get number of elapsed clock ticks
		 timer.disable(); // Turn off the counter
		 double interval = cntr * 0.2400; // Convert to time in microseconds
		 double seconds = interval/1000000;
		 double minutes = seconds/60;
		 double hours = minutes/60;
		 
		   return interval;
		     }
	
	public void start() {
		// TODO Auto-generated method stub
		
	}

}
