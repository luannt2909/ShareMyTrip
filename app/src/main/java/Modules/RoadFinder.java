package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Nguyen Tan Luan on 10/31/2016.
 */

public class RoadFinder {
    public static final String ROAD_URL_API="https://roads.googleapis.com/v1/snapToRoads?";
    public RoadFinderListener listener;
    public List<LatLng> latLngs;
    public String url;

    public RoadFinder(RoadFinderListener listener, List<LatLng> latLngs) {
        this.listener = listener;
        this.latLngs = latLngs;
    }

    public RoadFinder(RoadFinderListener listener, String url) {
        this.listener = listener;
        this.url = url;
    }

    public RoadFinder() {
    }

    public void excute(){
        listener.onRoadFinderStart();
        if(url!=null){
            new RoadAsynctask(listener).execute(url);
        }else
            new RoadAsynctask(listener).execute(createURL(latLngs));
    }

    public static String createURL(List<LatLng> lat) {
        String path="";
        for(int i=0;i<lat.size();i++){
            if(i==lat.size()-1)
                path+=String.valueOf(lat.get(i).latitude)+","+String.valueOf(lat.get(i).longitude);
            else
            path+=String.valueOf(lat.get(i).latitude)+","+String.valueOf(lat.get(i).longitude)+"|";

        }
        return ROAD_URL_API+"path="+path+"&interpolate=true&key="+DirectionFinder.GOOGLE_API_KEY;
    }
}
