package edu.cmu.solarflare;

public class Client {
    public String userID;
    public String userName;
    public String spotAddress;
    
    Client(String userID, String userName, String spotAddress) {
        this.userID = userID;
        this.userName = userName;
        this.spotAddress = spotAddress;
    }
    
    public String toJSON() {
        return "{\"username\":\"" + userName + "\",\"userid\":\"" + userID + "\"}";
    }
}