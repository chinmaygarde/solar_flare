package me.buzzyand.solarflare.comm;

import java.io.IOException;

import me.buzzyand.solarflare.base.DataInputOutputStreamConnection;
import me.buzzyand.solarflare.base.RadioOutputStreamConnection;

import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.squawk.util.StringTokenizer;

public class TimeInOutManager {
	TimeInOutCallback mCallback;
	public TimeInOutManager(TimeInOutCallback callback) {
		mCallback = callback;
	}
	
	private final String REMOTE_SPOT_ADDRESS = "0014.4F01.0000.467E";
	private DataInputOutputStreamConnection rConnection = null;
	//private RadioOutputStreamConnection rosConnection = null;
	
	public void startListening() {
		BootloaderListenerService.getInstance().start();
		new Thread() {
			public void run() {
				rConnection = new DataInputOutputStreamConnection();
				//rosConnection = new RadioOutputStreamConnection();
				
				System.out.println("Starting time broadcaster");
				
				rConnection.connect(REMOTE_SPOT_ADDRESS);
				//rosConnection.connect(REMOTE_SPOT_ADDRESS);
				
				System.out.println("I'm connected");
				
				rConnection.startSendingThread();
				//rosConnection.startSendingThread();
				
				while(true) {
					// Send the current time to the base station
					try {
						String message = mCallback.stringToBroadcast();
						System.out.println("Sending " + message);
						rConnection.send(message, (int)System.currentTimeMillis());
					} catch (NoRouteException nrException) {
						System.out.println("NoRouteException while sending to base station");
					} catch (IOException ioExeception) {
						System.out.println("IOException while sending to base station");
					}
					
					// Receive any broadcast
					String recv = rConnection.receive();
					System.out.println("Broadcast received: " + recv);
					
					//int iRecv = rosConnection.receive();
					//System.out.println(iRecv);
					
					String hours = null;
					String minutes = null;
					StringTokenizer tok;

					try {
						tok = new StringTokenizer(recv,":");
						hours = tok.nextToken();
						minutes = tok.nextToken();
						
						System.out.println("Received");
						System.out.println("Mins: " + minutes);
						System.out.println("Hours:" + hours);
						try {
							mCallback.updateTimerCorrections(Integer.parseInt(hours), Integer.parseInt(minutes));							
						} catch (NumberFormatException e) {
							System.out.println("Invalid broadcast");
							continue;
						}
					} catch(Exception e) {
						System.out.print("Exception while parsing arguments");
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("TimeInOutManager thread interrupted");
					}
				}
			}
		}.start();
	}
}
