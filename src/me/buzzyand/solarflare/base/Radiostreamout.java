package me.buzzyand.solarflare.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.spot.io.j2me.radiostream.RadiostreamConnection;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLEDArray;

public class Radiostreamout extends javax.microedition.midlet.MIDlet {

	protected void startApp() throws MIDletStateChangeException {
	try{
		RadiostreamConnection conn = 
	    (RadiostreamConnection)Connector.open("radiostream://0014.4F01.0000.2910");
	DataInputStream dis = conn.openDataInputStream();
	DataOutputStream dos = conn.openDataOutputStream();
	
	dos.writeUTF("radio stream sent out");
    dos.flush();
    System.out.println ("Answer was: " + dis.readUTF());
	}
	catch (IOException ie) {
		System.out.println("Unable to open connection");
		  System.out.println ("0014.4F01.0000.2910");
		}
	
	/*finally {
	    dis.close();
	    dos.close();
	    conn.close();
	}*/
}

	protected void pauseApp() {
    }


protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    leds.setOff();
}

}
