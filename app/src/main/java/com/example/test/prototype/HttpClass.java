package com.example.test.prototype;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class HttpClass extends AsyncTask<Void,Void,Void>
{

    private GoogleMap map;
    private ArrayList<Crime> crimeList;
    private ArrayList<LatLng> markers;
    private long currentTime;
    public HttpClass(GoogleMap _map,ArrayList<Crime> _crimeList)
    {
        map=_map;
        crimeList=_crimeList;
        markers=new ArrayList<LatLng>();
    }
    @Override
    protected Void doInBackground(Void... params)
    {

        URL url;
        HttpURLConnection request = null;

        try {
            for(int x=0;x<crimeList.size();x++) {
                //Get latitude and longitude of a crimes address
                String sURL = "http://maps.googleapis.com/maps/api/geocode/json?address=" + crimeList.get(x).getAddress().replace(" ", "%20") + "&sensor=true";
                Log.i("Debug",sURL);
                url = new URL(sURL);
                request = (HttpURLConnection) url.openConnection();

                request.connect();


                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                double latitude = root.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                double longitude = root.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

                crimeList.get(x).setLatitude(latitude);
                crimeList.get(x).setLongitude(longitude);

            }request.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }
    @Override
    protected void onPostExecute(Void result)
    {
        currentTime=Calendar.getInstance().getTimeInMillis();
        Log.i("Debug", String.valueOf(currentTime));
        for(int x=0;x<crimeList.size();x++)
        {
            Crime c=crimeList.get(x);
            LatLng templl=new LatLng(c.getLatitude(), c.getLongitude());
            //If a crime is not at position already
            if(!markers.contains(templl))
            {
                markers.add(templl);
                //If crime is resolved
                if (c.getResolved()) {
                    map.addMarker(new MarkerOptions().position(templl)
                            .title(c.getDateTime() + "\n" + c.getText())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                //If crime occured over an hour ago
                else if (currentTime - c.getDate() >= 7200000) {
                    map.addMarker(new MarkerOptions().position(templl)
                            .title(c.getDateTime() + "\n" + c.getText())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                //Else crime is recent
                else {
                    Log.i("Debug","in Red Markers");
                    Log.i("Debug",c.getText());
                    Log.i("Debug",c.getDateTime());
                    Log.i("Debug",c.getAddress());
                    Log.i("Debug",templl.toString());
                    map.addMarker(new MarkerOptions().position(templl)
                            .title(c.getDateTime() + "\n" + c.getText())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }

            }
        }

    }


}
