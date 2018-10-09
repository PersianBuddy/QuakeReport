package com.example.shahin.quakereport;

public class Earthquake {
    //location of earthquake
    private String mLoaction;
    //time of earthquake
    private String  mDate;
    //time Clock of earthquake
    private String mClock;
    //magnitude of earthquake
    private double mMag;
    //URL to see more detail in web browser
    private String mURL;


    //constructor of class
    public Earthquake(String location, String date, double mag, String clock,String url){
        this.mLoaction =location;
        this.mDate= date;
        this.mMag = mag;
        this.mClock = clock;
        this.mURL = url;
    }

    //Methods to get private variable outside of class
    public String getLocation(){
        return this.mLoaction;
    }
    public String getDate(){
//        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
//        return dateFormat.format(mDate);
        return mDate;
    }
    public double getMag(){
        return this.mMag;
    }
    public String getClock(){
        return this.mClock;
    }
    public String getURL(){return this.mURL;}
}
