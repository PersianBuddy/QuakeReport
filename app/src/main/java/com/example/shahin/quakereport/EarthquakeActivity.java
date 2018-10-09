package com.example.shahin.quakereport;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EarthquakeActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    //sample Query to get json file from 'USGS' Website
    private static final String SAMPLE_QUERY="https://earthquake.usgs.gov/fdsnws/event/1/query";
    //array of earthquakes to same info that extract from json file from 'USGS'
    private ArrayList<Earthquake> earthquakes ;
//

    //id for Earthquake loader
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //adaptor to show items inside ListView
    CustomAdaptor mAdaptor;
    //a textView to be shown if list is empty ->> if not it's text property will be empty
    TextView emptyListTextView;
    //progressbar to be shown while application is making http request
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);
        // Create a fake list of earthquake objects

        //earthquakes.add(new Earthquake("San Francisco","1460156770040",6.1));
//        earthquakes.add(new Earthquake("London",new Date(2012-1900,1,6),5.4));
//        earthquakes.add(new Earthquake("Berlin",new Date(2011-1900,6,1),8.5));
//        earthquakes.add(new Earthquake("Iran",new Date(2000-1900,5,5),5.4));
//        earthquakes.add(new Earthquake("Germany",new Date(1900-1900,2,22),8));
//        earthquakes.add(new Earthquake("Turkey",new Date(1970-1900,8,5),7.5));
//        earthquakes.add(new Earthquake("Iraq",new Date(2018-1900,8,7),6));

//        // Find a reference to the {@link ListView} in the layout
//        ListView earthquakeListView = (ListView) findViewById(R.id.list);
//
//        // Create a new {@link ArrayAdapter} of earthquakes
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
////                this, android.R.layout.simple_list_item_1, earthquakes);
//        CustomAdaptor adapter = new CustomAdaptor(this,earthquakes);
//
//        // Set the adapter on the {@link ListView}
//        // so the list can be populated in the user interface
//        earthquakeListView.setAdapter(adapter);
        //Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        //set mAdaptor to contain an empty list of earthquakes
        mAdaptor= new CustomAdaptor(this,new ArrayList<Earthquake>());
        //view to be shown if list is empty
        emptyListTextView = (TextView) findViewById(R.id.empty_list);
        earthquakeListView.setEmptyView(emptyListTextView);
        //initialize progress bar
        progressBar = (ProgressBar)findViewById(R.id.loading_spinner);

        ////////////////////////////////////////////make http request using Async task////////////////////////////////
//        RequestAsyncTask myTask= new RequestAsyncTask();
//        myTask.execute(SAMPLE_QUERY);
//        if (mAdaptor==null){
//            Toast.makeText(this, "Null Adaptor", Toast.LENGTH_SHORT).show();
//        }else {
//            earthquakeListView.setAdapter(mAdaptor);
//            Toast.makeText(this, "Adapter set", Toast.LENGTH_SHORT).show();
//        }

        //////////////////////////////////////////make http request using loader////////////////////////////////////////
        //check network connectivity
        ConnectivityManager cm =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork= cm.getActiveNetworkInfo();
        if (activeNetwork!=null && activeNetwork.isConnectedOrConnecting()){
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID,null,this);
            if (mAdaptor==null){
                Toast.makeText(this, "Null Adaptor", Toast.LENGTH_SHORT).show();
            }else {
                earthquakeListView.setAdapter(mAdaptor);
                Toast.makeText(this, "Adaptor set", Toast.LENGTH_SHORT).show();
            }
        }else{
            progressBar.setVisibility(View.GONE);
            //network problem
            emptyListTextView.setText(R.string.no_internet);
        }


        ////////////////////////////////////////fake data/////////////////////////////////////////
//        //set adaptor
//        if (earthquakes !=null){
//            CustomAdaptor adaptor = new CustomAdaptor(this,earthquakes);
//            earthquakeListView.setAdapter(adaptor);
//        }else {
//            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
//        }


        //add onClickListener for listItem
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                double magValue = earthquakes.get(i).getMag();
//                Toast.makeText(EarthquakeActivity.this,"Hello Friend"+ magValue,Toast.LENGTH_SHORT).show();
                //get intent to web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdaptor.getItem(i).getURL()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /////////////////////////////override loaderManager.LoaderCallback methods ///////////////////////////////
    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        //add query to url using preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMag= sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        //create a base Uri
        Uri baseUri = Uri.parse(SAMPLE_QUERY);
        //make a uri builder to add query
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //add query to uri
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMag);
        uriBuilder.appendQueryParameter("orderby", "time");


        EarthquakeLoader loader = new EarthquakeLoader(EarthquakeActivity.this,uriBuilder.toString());
        Toast.makeText(this, "http request inside loader", Toast.LENGTH_SHORT).show();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        // Clear the adapter of previous earthquake data
        mAdaptor.clear();

        //hide progress bar
        progressBar.setVisibility(View.GONE);

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdaptor.addAll(earthquakes);
            Toast.makeText(this, "data set inside loader", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Empty earthquake inside loader", Toast.LENGTH_SHORT).show();
        }
        //set text of textView that will show if list is empty
        emptyListTextView.setText(R.string.empty_list);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        // Clear the adapter of previous earthquake data
        mAdaptor.clear();
    }


    //////////////////////////////////////////Using Async task////////////////////////////////////
    //Async task for make a http request
    private class RequestAsyncTask extends AsyncTask <String,Void,ArrayList <Earthquake>> {

        @Override
        protected ArrayList<Earthquake> doInBackground(String... url) {
//            Toast.makeText(EarthquakeActivity.this, "http requst inside Async task", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG,"http request inside Async task");
            if (url.length>0){
                return QueryUtils.fetchEarthquake(url[0]);
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList <Earthquake> earthquakesReport) {
            // Clear the adapter of previous earthquake data
            mAdaptor.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthquakesReport != null && !earthquakesReport.isEmpty()) {
                mAdaptor.addAll(earthquakesReport);
                Toast.makeText(EarthquakeActivity.this, "Adapter set inside Async Task", Toast.LENGTH_SHORT).show();
                earthquakes =earthquakesReport;
            }else {
                Toast.makeText(EarthquakeActivity.this, "Empty list inside AsyncTask", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //menu setting
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
