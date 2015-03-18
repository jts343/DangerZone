package com.example.test.prototype;


import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class Crime
{
    private String text;
    private String address;
    private double latitude;
    private double longitude;
    private boolean resolved;
    private String dateTime;
    private long date;
    public Crime( String t,long d)
    {
        text=t.replace("DrexelAlert ","");
        date=d;
        dateTime=getDateFromMilli(date);
        address=parseAddress(text);
        resolved=text.contains("All Clear");

    }
    public void setLatitude(double l)
    {
        latitude=l;
    }
    public void setLongitude(double l)
    {
        longitude=l;
    }
    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public String getText()
    {
        return text;
    }
    public String getAddress()
    {
        return address;
    }
    public boolean getResolved()
    {
        return resolved;
    }
    public String getDateTime()
    {
        return dateTime;
    }
    public long getDate()
    {
        return date;
    }
    //Gets a properly formatted string from milliseconds
    public String getDateFromMilli(long milli)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR);
        int minute=calendar.get(Calendar.MINUTE);
        int amorpm=calendar.get(Calendar.AM_PM);

        String am_pm="";
        if(amorpm==0)
        {
            am_pm="AM";
        }
        else if(amorpm==1)
        {
            am_pm="PM";
        }

        String datetime=mMonth+"/"+mDay+"/"+mYear+"   "+hour+":"+minute+am_pm;
        return datetime;
    }
    public String parseAddress(String text)
    {
        //An exception for a two word street
        text=text.replace("Spring Garden","SpringGarden");
        String[] bodysplit=text.split(" ");
        String address="";
        for(int x=1;x<bodysplit.length;x++)
        {
            Log.i("bodysplit.length",bodysplit[x]);
            // Gets address in the form of Street & Street
            if(bodysplit[x].equals("&"))
            {
                address=bodysplit[x-1]+" and "+bodysplit[x+1];
                break;
            }
            //Gets address in the form of Street / Street
            else if(bodysplit[x].contains("/"))
            {
                String[] temp=bodysplit[x].split("/");
                address=temp[0]+" and "+temp[1];
                break;
            }
            //Gets address in the form 100 Street
            else if(bodysplit[x].matches("[-+]?\\d+(\\.\\d+)?") && Integer.parseInt(bodysplit[x])>99)
            {
                address=bodysplit[x]+" "+bodysplit[x+1];
                break;
            }
        }
        //Fixes exception for proper address
        address=address.replace("SpringGarden","Spring Garden");
        //Adds city and state onto end of address
        Log.i("Address",address);
        address=address+" Phildelphia PA";
        Log.i("Address + Philly,PA",address);
        Log.i("Address with replaceALL",address.replaceAll("[^\\w\\s]",""));

        //Removes all none letters and numbers
        return address.replaceAll("[^\\w\\s]","");
    }

}
