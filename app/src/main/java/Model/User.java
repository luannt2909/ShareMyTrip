package Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class User {
    private String userID;
    private String userName;
    private String useravatar;
    private Double longitude;
    private Double latitude;
    private Boolean checkLocation;
    private List<User> friends;
    public User() {
    }


    public User(String userName, Double longitude, Double latitude) {
        this.userName = userName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String useravatar, Double longitude, Double latitude, Boolean checkLocation) {
        this.userName = userName;
        this.useravatar = useravatar;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
