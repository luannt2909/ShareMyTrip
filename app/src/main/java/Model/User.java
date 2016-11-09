package Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class User {
    private String userID;
    private String userName;
    private String useravatar;
    private LatLng userLocation;
    private Boolean checkLocation;

    public User() {
    }

    public User(String userName, LatLng userLocation) {
        this.userName = userName;
        this.userLocation = userLocation;
    }

    public User(String userName, String useravatar, LatLng userLocation) {
        this.userName = userName;
        this.useravatar = useravatar;
        this.userLocation = userLocation;
    }

    public User(String userID, String userName, String useravatar, LatLng userLocation, Boolean checkLocation) {
        this.userID = userID;
        this.userName = userName;
        this.useravatar = useravatar;
        this.userLocation = userLocation;
        this.checkLocation = checkLocation;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public Boolean getCheckLocation() {
        return checkLocation;
    }

    public void setCheckLocation(Boolean checkLocation) {
        this.checkLocation = checkLocation;
    }

    public String getUseravatar() {
        return useravatar;
    }

    public void setUseravatar(String useravatar) {
        this.useravatar = useravatar;
    }
}
