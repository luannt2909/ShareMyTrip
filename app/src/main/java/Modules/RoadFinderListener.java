package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Nguyen Tan Luan on 10/31/2016.
 */

public interface RoadFinderListener {
    void onRoadFinderStart();
    void onRoadFinderSuccess(List<LatLng> latLngs);
}
