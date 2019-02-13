package com.example.android.quakereport;

import android.content.Intent;

/**
 * Created by vanur on 30-03-2018.
 */

public class data {


    private double mMag;
    private String mOffset;
    private String mLocation;
    private long mTimeInMilliseconds;
    private Intent mIntent;
    public data(double mag, String offset, String location, long timeInMilliseconds, Intent browserIntent){
        mMag = mag;
        mOffset = offset;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mIntent = browserIntent;
    }
    public double getmMag() {
        return mMag;
    }

    public long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getmOffset() { return mOffset;}

    public String getmLocation() { return mLocation;}

    public Intent getmIntent(){ return mIntent;}

}
