package me.buzzyand.solarflare.comm;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import me.buzzyand.solarflare.base.DataInputOutputStreamConnection;
import me.buzzyand.solarflare.base.RadioOutputStreamConnection;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.Utils;
import com.sun.squawk.util.StringTokenizer;

public class TimeInOutManager {
	TimeInOutCallback mCallback;
	long uid;
	
	public TimeInOutManager(TimeInOutCallback callback) {
		mCallback = callback;
		uid = System.currentTimeMillis();
	}
	
	private final String REMOTE_SPOT_ADDRESS = "0014.4F01.0000.467E";
	private DataInputOutputStreamConnection rConnection = null;
	private RadioOutputStreamConnection rosConnection = null;
	
	public void startListening() {
		startSenderThread();
		startReceiverThread();
	}

	public void startReceiverThread() {
        new Thread() {
            public void run() {
                String tmp = null;
                RadiogramConnection dgConnection = null;
                Datagram dg = null;
                
                try {
                    dgConnection = (RadiogramConnection) Connector.open("radiogram://:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                } catch (IOException e) {
                    System.out.println("Could not open radiogram receiver connection");
                    e.printStackTrace();
                    return;
                }
                
                while(true){
                    try {
                        dg.reset();
                        dgConnection.receive(dg);
                        try {
                        	tmp = dg.readUTF();
                        	StringTokenizer tok = new StringTokenizer(tmp,":");
                        	long id = Long.parseLong(tok.nextToken());
    						int hours = Integer.parseInt(tok.nextToken());
    						int minutes = Integer.parseInt(tok.nextToken());
    						if(id != uid) {
    							mCallback.updateTimerCorrections(hours, minutes);
    							System.out.print("Updating time from " + id);
    						}
                        } catch(Exception e) {
                        	System.out.print("Error while parsing response");
                        }
                    } catch (IOException e) {
                        System.out.println("Nothing received");
                    }
                }
            }
        }.start();
    }
	
	synchronized public void startSenderThread() {
        new Thread() {
            public void run() {
                // We create a DatagramConnection
                DatagramConnection dgConnection = null;
                Datagram dg = null;
                try {
                    // The Connection is a broadcast so we specify it in the creation string
                    dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                } catch (IOException ex) {
                    System.out.println("Could not open radiogram broadcast connection");
                    ex.printStackTrace();
                    return;
                }
                
                while(true){
                    try {
                        // We send the message (UTF encoded)
                        dg.reset();
                        dg.writeUTF(uid + ":" + mCallback.stringToBroadcast());
                        dgConnection.send(dg);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Utils.sleep(500);
                }
            }
        }.start();
    }
}
