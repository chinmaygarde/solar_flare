package edu.cmu.solarflare;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import org.json.me.JSONObject;

public class Zigbee {
    private SolarFlare spot;
    public String address;             // spot's 64-bit dotted MAC address
    private int seqNo;                  // sequence counter for outgoing messages 
    private DatagramConnection senderConnection;
    private Datagram senderDatagram;
    private RadiogramConnection receiverConnection;
    private Datagram receiverDatagram;
    private MessageBuffer outgoing;     // a queue for outgoing messages, emptied by the sender thread
    private Vector lastMessages;        // (for broadcast) don't interpret previously seen messages
    
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
                        String m = outgoing.get();  // will block until there's a message to send
                        senderDatagram.reset();
                        senderDatagram.writeUTF(m.toString());
                        senderConnection.send(senderDatagram);  // broadcast
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
                        receiverConnection.receive(receiverDatagram);
                        
                        String msg = receiverDatagram.readUTF();
                        System.out.println("Zigbee got: " + msg);
                        
                        //TODO: process message
                    } catch (IOException e) {
                        System.out.println("Error, ZigBee I/O: Nothing received. " + e);
                    }
                }
            }
        }.start();
    }
    
    public void broadcastClient(Client c) {
        JSONObject m = new JSONObject();
        //TODO: add some json attributes
    }
    
    public void broadcastJSON(JSONObject m) {
        //TODO: add sender, receiver, and messageid, then send off
    }
    
    public void broadcast(String m) {
        outgoing.put(m);
    }
}