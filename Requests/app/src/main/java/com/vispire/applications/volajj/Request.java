package com.vispire.applications.volajj;

import com.google.firebase.database.Exclude;

import java.util.Map;
import java.util.HashMap;

public class Request {
    public double longitude;
    public double latitude;
    public String QRCode;
    public String requestType;
    public String currentStatus;
    public Map<String, Boolean> requests = new HashMap<>();

    public Request() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Request(double longitude, double latitude, String requestType, String QRCode, String currentStatus) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.QRCode = QRCode;
        this.requestType = requestType;
        this.currentStatus = currentStatus;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("requestType", requestType);
        result.put("QRCode", QRCode);
        result.put("status", currentStatus);
        return result;
    }
}
