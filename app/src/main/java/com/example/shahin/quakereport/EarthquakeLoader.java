package com.example.shahin.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.net.URL;
import java.util.ArrayList;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    String mUrl;

    public EarthquakeLoader(Context context,String url) {
        super(context);
        this.mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        return QueryUtils.fetchEarthquake(mUrl);
    }
}
