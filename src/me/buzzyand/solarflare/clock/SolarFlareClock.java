package me.buzzyand.solarflare.clock;

import me.buzzyand.solarflare.base.SolarFlareComponent;

import com.sun.spot.peripheral.IAT91_TC;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.TimerCounterBits;

public class SolarFlareClock extends SolarFlareComponent implements
		TimerCounterBits {

	public double measureInterval() {
		// Get a Timer Counter
		IAT91_TC timer = Spot.getInstance().getAT91_TC(0);

		// Use fast clock speed
		timer.configure(TC_CAPT | TC_CLKS_MCK32);
		
		// Start counting
		timer.enableAndReset();
		
		// interval to measure ...
		// Get number of elapsed clock ticks
		int cntr = timer.counter();
		
		// Turn off the counter
		timer.disable();
		
		// Convert to time in microseconds
		double interval = cntr * 0.2400;
		double seconds = interval * 1000000;
		double minutes = seconds / 60;
		double hours = minutes / 60;

		return interval;
	}

	public void start() {
		// TODO Auto-generated method stub
	}
}
