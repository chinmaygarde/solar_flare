package me.buzzyand.solarflare.utils;

public class Log {
	public static void log(String message) {
		System.out.println(message);
	}
	public static void printClockHelp() {
		log("Operations: ");
		log("Press Switch 1 to start timer.");
		log("Press Switch 2 to start clock.");
	}
}
