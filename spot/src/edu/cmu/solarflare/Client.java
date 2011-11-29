package edu.cmu.solarflare;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class Client {
    public String userID;
    public String userName;
    public String spotAddress;
    
    Client(String userID, String userName, String spotAddress) {
        this.userID = userID;
        this.userName = userName;
        this.spotAddress = spotAddress;
    }
    
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", userName);
            json.put("userid", userID);
        } catch (JSONException e) {
            System.out.println("Error, Client JSON: " + e);
        } catch (Exception e) {
            System.out.println("Error, Client JSON: generic exception " + e);
        }
        return json;
    }
}