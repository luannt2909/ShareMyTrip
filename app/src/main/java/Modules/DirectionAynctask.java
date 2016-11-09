package Modules;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nguyen Tan Luan on 10/21/2016.
 */

public class DirectionAynctask extends AsyncTask<String,Void,String> {
    DirectionFinderListener listener;
    public DirectionAynctask(DirectionFinderListener _listener) {
        listener=_listener;
    }


    @Override
    protected String doInBackground(String... strings) {
        String link=strings[0];
        try {
            URL url=new URL(link);
            InputStream input=url.openConnection().getInputStream();
            StringBuffer buffer=new StringBuffer();
            BufferedReader reader=new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line=reader.readLine())!=null){
                buffer.append(line+"\n");
            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            parseJson(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String s) throws JSONException {
        if(s==null)
            return;
        List<Route> routes=new ArrayList<>() ;
        JSONObject object=new JSONObject(s);
        JSONArray array=object.getJSONArray("routes");
        for(int i=0;i<array.length();i++){
            JSONObject jsonroute=array.getJSONObject(i);
            Route route=new Route();
            JSONObject overview=jsonroute.getJSONObject("overview_polyline");
            JSONArray legs=jsonroute.getJSONArray("legs");
            JSONObject leg=legs.getJSONObject(0);
            JSONObject distance=leg.getJSONObject("distance");
            JSONObject duration=leg.getJSONObject("duration");
            JSONObject endlocation=leg.getJSONObject("end_location");
            JSONObject startlocation=leg.getJSONObject("start_location");
            route.distance=new Distance(distance.getString("text"),distance.getInt("value"));
            route.duration=new Duration(duration.getString("text"),duration.getInt("value"));
            route.endAddress=leg.getString("end_address");
            route.startAddress=leg.getString("start_address");
            route.startLat=new LatLng(startlocation.getDouble("lat"),startlocation.getDouble("lng"));
            route.endLat=new LatLng(endlocation.getDouble("lat"),endlocation.getDouble("lng"));
            route.points=decodePolyline(overview.getString("points"));
            routes.add(route);
        }
        listener.onDirectionFinderSuccess(routes);

    }

    private List<LatLng> decodePolyline(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
