package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by vanur on 01-04-2018.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<data>> {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;


    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<data> loadInBackground() {
    if(mUrl == null){
        return null;
    }
    List<data> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
    return  earthquakes;
   }
}
