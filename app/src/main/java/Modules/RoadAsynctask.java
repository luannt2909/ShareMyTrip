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
 * Created by Nguyen Tan Luan on 10/31/2016.
 */

public class RoadAsynctask  extends AsyncTask<String, Void, String> {
    RoadFinderListener listener;

    public RoadAsynctask() {
    }

    public RoadAsynctask(RoadFinderListener listener) {
        this.listener = listener;
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
        if (s==null) return;
        List<LatLng> location=new ArrayList<>();
        JSONObject object=new JSONObject(s);
        JSONArray array=object.getJSONArray("snappedPoints");
        for(int i=0; i<array.length();i++){
            JSONObject points=array.getJSONObject(i);
            JSONObject jsonlocation=points.getJSONObject("location");
            LatLng latlng=new LatLng(jsonlocation.getDouble("latitude"), jsonlocation.getDouble("longitude"));
            location.add(latlng);
        }
        listener.onRoadFinderSuccess(location);
    }
}
