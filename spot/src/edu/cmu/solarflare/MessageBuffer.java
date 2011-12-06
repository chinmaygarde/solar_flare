package edu.cmu.solarflare;

import java.util.Vector;

// Memory buffer for inter-thread communication.
public class MessageBuffer {
    private Vector messageQueue;
    private boolean isActive;
    
    MessageBuffer() {
        messageQueue = new Vector(5);
        isActive = false;
    }
    
    public void activate() {
        isActive = true;
    }
    
    public synchronized void put(String m) {
        if (!isActive) { return; }
        messageQueue.addElement(m);
        notify();
    }
    
    public synchronized String get() throws InterruptedException {
        if (messageQueue.isEmpty()) {
            wait();
        }
        
        String m = (String) messageQueue.elementAt(0);
        messageQueue.removeElementAt(0);
        return m;
    }
}