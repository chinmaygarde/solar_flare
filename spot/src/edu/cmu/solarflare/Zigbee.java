package edu.cmu.solarflare;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class Zigbee {
    private SolarFlare spot;
    public String address;             // spot's 64-bit dotted MAC address
    public int seqNo;                  // sequence counter for outgoing messages 
    private DatagramConnection senderConnection;
    private Datagram senderDatagram;
    private RadiogramConnection receiverConnection;
    private Datagram receiverDatagram;
    private MessageBuffer outgoing;     // a queue for outgoing messages, emptied by the sender thread
    private Vector lastMessages;        // (for broadcast) don't interpret previously seen messages
    private JSONObject msgJSON;
    private String msgAction;
    
    public Zigbee(SolarFlare spot) {
        this.spot = spot;
        this.address = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        this.seqNo = 0;
        this.outgoing = new MessageBuffer();
        this.lastMessages = new Vector(20);     // keep a record of the last 20 messages seen
    }
    
    public void init() throws IOException {
        senderConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
        senderDatagram = senderConnection.newDatagram(senderConnection.getMaximumLength());
        receiverConnection = (RadiogramConnection) Connector.open("radiogram://:37");
        receiverDatagram = receiverConnection.newDatagram(receiverConnection.getMaximumLength());
    }
    
    public void startComm() {
        startSenderThread();
        startReceiverThread();
    }
    
    private void startSenderThread() {
        new Thread() {
            public void run() {                
                while (true) {
                    try {
                        senderDatagram.reset();
                        senderDatagram.writeUTF(outgoing.get());    // will block until there's a message to send
                        senderConnection.send(senderDatagram);      // broadcast
                    } catch (InterruptedException e) {
                        System.out.println("Error, ZigBee threads: Could not get message from outgoing buffer. " + e);
                    } catch (IOException e) {
                        System.out.println("Error, ZigBee I/O: Could not send. " + e);
                    }
                }
            }
        }.start();
    }
    
    private void startReceiverThread() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        receiverDatagram.reset();
                        receiverConnection.receive(receiverDatagram);   // will block until there's a message to receive
                        parseIncomingMessage(receiverDatagram.readUTF());
                    } catch (IOException e) {
                        System.out.println("Error, ZigBee I/O: Nothing received. " + e);
                    }
                }
            }
        }.start();
    }
    
    public void parseIncomingMessage(String msg) {
        try {
            msgJSON = new JSONObject(msg);
            
            // process message, only if we're not the sender and if it's a new messageID
            if (!msgJSON.getString("sender").equals(address) && !lastMessages.contains(msgJSON.getString("messageid"))) {
                lastMessages.removeElementAt(0);
                lastMessages.addElement(msgJSON.getString("messageid"));
                
                System.out.println("Zigbee got: " + msg);
                msgAction = msgJSON.getString("action");
                if (msgAction.equals("adduser")) {
                    // We need to pass the messageId since we need to pass the message id that is present in the message
                    spot.addClient(msgJSON.getString("userid"), msgJSON.getString("username"), msgJSON.getString("sender"),msgJSON.getString("messageid"));
                }
                else if (msgAction.equals("removeuser")){
                    spot.removeClient(msgJSON.getString("userid"), msg);
                }
                else if (msgAction.equals("usermessage")){
                    if(msgJSON.getString("receiver").equals(address))
                        spot.sendUserMessage(msgJSON.getString("userid"), msgJSON.getString("message"));
                    else
                        broadcast(msg);
                }
                
                // re-broadcast
                outgoing.put(msg);
            }
        } catch (JSONException e) {
            System.out.println("Error, ZigBee JSON: " + e);
        }
    }
    
    public void broadcastNewClient(Client c, String messageId) {
        try {
            JSONObject m = new JSONObject();
            m.put("action", "adduser");
            m.put("username", c.userName);
            m.put("userid", c.userID);
            m.put("sender", c.spotAddress);
            m.put("receiver","");
            m.put("messageid", messageId); // We need to add messageId present in message and not add a new messageID
            broadcastJSON(m);
        } catch (JSONException e) {
            System.out.println("Error, ZigBee JSON: " + e);
        }
        
    }
    
    public void broadcastJSON(JSONObject m) {
        sendJSON(m, "");
    }
    
    public void sendJSON(JSONObject m, String receiverUserID) {
        try {
            // add sender, receiver, and messageID
            if (!receiverUserID.equals(""))
                m.put("receiver", receiverUserID);
            
            // serialize JSON and send off
            broadcast(m.toString());
        } catch (JSONException e) {
            System.out.println("Error, ZigBee JSON: " + e);
        }
    }
    
    public void broadcast(String m) {
        // add message to outgoing buffer
        outgoing.put(m);
    }

    void broadcastRemoveClient(Client c) {
        try {
            JSONObject m = new JSONObject();
            m.put("action", "removeuser");
            m.put("username", c.userName);
            m.put("userid", c.userID);  
            broadcastJSON(m);
        } catch (JSONException e) {
            System.out.println("Error, ZigBee JSON: " + e);
        }

    }
}