package Modules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Nguyen Tan Luan on 10/21/2016.
 */

public class DirectionFinder {
    public static final String DIRECTION_URL_API="https://maps.googleapis.com/maps/api/directions/json?";
    public static final String GOOGLE_API_KEY="AIzaSyDx7X0x2uWZ8zw3Cv6b4FvFWOKuJ56zM0w";
    public DirectionFinderListener listener;
    public String origin;
    public String destination;

    public DirectionFinder() {
    }

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }
    public void excute() throws UnsupportedEncodingException{
        listener.onDirectionFinderStart();
        new DirectionAynctask(listener).execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String Urlorigin= URLEncoder.encode(origin,"utf-8");
        String Urldestination=URLEncoder.encode(destination,"utf-8");
        return DIRECTION_URL_API+"origin="+Urlorigin+"&destination="+Urldestination+"&key="+GOOGLE_API_KEY;

    }
}
