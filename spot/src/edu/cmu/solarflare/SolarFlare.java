package edu.cmu.solarflare;

import com.sun.spot.peripheral.TimeoutException;
import com.sun.spot.sensorboard.EDemoBoard;
import org.json.me.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
 
public class SolarFlare extends MIDlet {
 
    public EDemoBoard board = EDemoBoard.getInstance();
    private Zigbee zigbee;
    private Wifi wifi;
    private Hashtable clients;          // all known clients in the network (userID => Client object)
 
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("SPOT on.");
        
        // state
        clients = new Hashtable();
        
        // communication
        zigbee = new Zigbee(this);
        wifi = new Wifi(this, "ulala", 10000);  // TCP server on port 10000
        
//        try {
//            zigbee.init();
//        } catch (IOException e) {
//            System.out.println("Error, ZigBee I/O: Could not open radiogram broadcast connection. " + e);
//        }
        
        try {
            wifi.init();
        } catch (TimeoutException e) {
            System.out.println("Error, WiFi timeout: " + e);
        } catch (IOException e) {
            System.out.println("Error, WiFi I/O: " + e);
        } catch (Exception e) {
            System.out.println("Error, WiFi general: " + e);
        }
        
//        zigbee.startComm();
        wifi.startComm();
    }
 
    public void addLocalClient(String userID, String userName, Integer clientCID) {
        addClient(userID, userName, zigbee.address);
        
        // send the new user our list of known clients
        JSONArray clientArray = new JSONArray();
        for (Enumeration e = clients.elements(); e.hasMoreElements();) {
            clientArray.put(((Client) e.nextElement()).toJSONObject());
        }
        JSONObject m = new JSONObject();
        try {
            m.put("action", "adduser");
            m.put("users", clientArray);
            wifi.sendToClient(clientCID, m.toString());
        } catch (JSONException e) {
            System.out.println("Error, WiFi JSON: " + e);
        }
    }
    
    public void addClient(String userID, String userName, String spotAddress) {
        Client c = new Client(userID, userName, spotAddress);
        clients.put(userID, c); 
        
        // broadcast new client info if it's a local client
        if (spotAddress.equals(zigbee.address)) {
            zigbee.broadcastNewClient(c);
        }
        
        //TODO: tell all local clients about the new user
        //wifi.broadcastNewClient(c);
    }
    
    protected void pauseApp() {
    }
 
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}