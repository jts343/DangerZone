package com.example.test.prototype;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private Location location=null;
    private ArrayList<Marker> eBoxes=new ArrayList<Marker>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMap();
        setUpButtons();
        setCrimeMarkers();
    }



    private void setUpMap()
    {
        try
        {
            if (map == null) {
                // Try to obtain the map from the SupportMapFragment.
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }
            // Check if we were successful in obtaining the map.
            if (map != null) {
                //Sets camera update to center of Drexel at a reasonable zoom
                LatLng centerDrexel = new LatLng(39.959429, -75.18922);
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(centerDrexel);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
                //Set info window to custom layout
                map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker marker) {

                        View v = getLayoutInflater().inflate(R.layout.info_window, null);

                        String title = marker.getTitle();
                        String snippet = marker.getSnippet();

                        TextView tv = (TextView) v.findViewById(R.id.textView);
                        TextView tv2 = (TextView) v.findViewById(R.id.textView2);

                        tv.setText(title);
                        tv2.setText(snippet);

                        return v;

                    }
                });
                //Set infor window to close when clicked
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        marker.hideInfoWindow();
                    }
                });
                //Sets users current location on map
                map.setMyLocationEnabled(true);
                //Removes button from upper right corner of GoogleMap
                map.getUiSettings().setMyLocationButtonEnabled(false);
                //Moves camera to camera update
                map.moveCamera(center);
                map.animateCamera(zoom);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void setUpButtons()
    {
        //Set up geolocation button
        Button button= (Button) findViewById(R.id.geoButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(location==null)
                {
                    //Gets location from map and removes blue dot
                    location = map.getMyLocation();
                    map.setMyLocationEnabled(false);

                }
                if(location!=null) {
                    Double lat = location.getLatitude();
                    Double lng = location.getLongitude();
                    Log.d("Geobutton", Double.toString(lat));
                    Log.d("Geobutton", Double.toString(lng));
                    //Adds marker at users current location
                    map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                            .title("You Are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    //Updates camera to center on users location
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((new LatLng(lat, lng)), 13);
                    map.animateCamera(cameraUpdate);
                }

            }
        });
        //Sets up emergency box button
        button= (Button) findViewById(R.id.eBoxButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button) findViewById(R.id.eBoxButton);
                //Checks if button has been pressed
                if(b.getText().equals("Show Emergency Boxes"))
                {
                    //Plots all emergency boxes
                    setEmergencyBoxes();
                    b.setText("Hide Emergency Boxes");
                }
                else
                {
                    //Hides emergency boxes
                    for(int x=0;x<eBoxes.size();x++)
                    {
                        eBoxes.get(x).remove();
                    }
                    b.setText("Show Emergency Boxes");
                }


            }
        });
    }
    public Cursor getSMSMessages()
    {
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[]{"date", "address", "body"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider

        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        return c;
    }
    public void setCrimeMarkers()
    {
        IncidentReport ir = new IncidentReport(getSMSMessages());
        ArrayList<Crime> crimeArray = ir.getIncidentReport();
        HttpClass httpClass=new HttpClass(map,crimeArray);
        httpClass.execute();
    }
    public void setEmergencyBoxes()
    {
        AssetManager am = this.getAssets();
        try {

            String line;
            InputStream is = am.open("boxes.csv");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            while((line = in.readLine()) != null)
            {
                //Parses CSV
                String[] linesplit=line.split(",");
                LatLng templl=new LatLng(Double.parseDouble(linesplit[1].replace(" ","")),Double.parseDouble(linesplit[0].replace(" ","")));
                //Plot emergency box on map
                Marker m=map.addMarker(new MarkerOptions().position(templl).title("Emergency Box")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                //Adds emergency box to arraylist
                eBoxes.add(m);
                Log.d("Longitude",linesplit[0]);
                Log.d("Latitude",linesplit[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 /*   private void BoxMarker(DZ_Eboxes.Ebox ebox) {
        boolean contains = false;
        LatLng templl = new LatLng(ebox.getLatitude(), ebox.getLongitude());
        if(!markers.contains(templl))
        {
            markers.add(templl);
            mMap.addMarker(new MarkerOptions().position(templl)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }
    /        // Build Ebox array
//        DZ_Eboxes.Ebox[] boxArray = new DZ_Eboxes.Ebox[100];
//        File inFile = new File("com/example/test/prototype/DrexelBoxes.xml");
//        boxArray = DZ_Eboxes.parseXML(inFile);
//
//        for(int i = 0; i < boxArray.length; i++) {
//            System.out.println(boxArray[i].getLatitude());
//            if (boxArray[i].getLatitude() != 0.0 && boxArray[i].getLongitude() != 0.0) {
//                BoxMarker(boxArray[i]);
//                Log.d( "Ebox Mark", "Marking Emergency box..." );
//            }
//        }*/



}



