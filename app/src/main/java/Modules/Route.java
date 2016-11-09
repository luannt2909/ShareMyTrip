package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Nguyen Tan Luan on 10/21/2016.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String startAddress;
    public String endAddress;
    public LatLng startLat;
    public LatLng endLat;
    public List<LatLng> points;
}
