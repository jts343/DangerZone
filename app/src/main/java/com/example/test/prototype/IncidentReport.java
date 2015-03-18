package com.example.test.prototype;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;

public class IncidentReport
{

    private ArrayList incidentReport=new ArrayList(0);
    private Cursor database;
    private long currentTime = Calendar.getInstance().getTimeInMillis();//Current dat in milliseconds
    private long daytomilli=86400000;//Milliseconds in a day
    public IncidentReport(Cursor c)
    {
        database=c;
        populateIncidentReport();
    }
    public ArrayList getIncidentReport()
    {
        return incidentReport;
    }
    public void populateIncidentReport()
    {
        //while a text message still is in cursor
        while(database.moveToNext())
        {
            long date = database.getLong(0);
            String phoneNumber = database.getString(1);
            String body = database.getString(2);
            //If time is over a day, then we are done plotting and can stop looking at text messages
            if(currentTime-date>=daytomilli)
            {
                break;
            }
            //Check for DrexelAlert
            else if(body.contains("DrexelAlert"))
            {
                Crime temp=new Crime(body,date);
                //If an address can be taken out of the text
                if(!temp.getAddress().equals("")){
                    incidentReport.add(temp);
            }

            }
        }
    }
}
