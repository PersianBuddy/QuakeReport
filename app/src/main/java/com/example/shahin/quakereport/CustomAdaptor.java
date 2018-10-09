package com.example.shahin.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdaptor extends ArrayAdapter<Earthquake> {
    //default constructor
    public CustomAdaptor(Activity context, ArrayList<Earthquake> myList){
        super(context,0,myList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //view to update convertView
        View updatedView = convertView;
        if(updatedView == null) {
            updatedView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Earthquake currentItem = getItem(position);

        //split location to two string
        String [] locationArray = String.valueOf(currentItem.getLocation()).split("of ",2);
        String locationPlace;
        String locationDistance;
        //check if there is an "of" in String
        if (locationArray.length >1){
            locationDistance =locationArray[0] +"of";
            locationPlace = locationArray[1];
        }else{
            locationDistance="near the";
            locationPlace = locationArray[0];
        }

        //find textView for location
        TextView distanceTextView = updatedView.findViewById(R.id.item_location_distance);
        distanceTextView.setText(locationDistance);
        TextView locationTextView = updatedView.findViewById(R.id.item_location);
        //assign the value to text of TextView
        locationTextView.setText(locationPlace);

        //get only one decimal value of magnitude
        int magValue= (int)(currentItem.getMag()*10);
        double magValueDouble =(double)magValue/10;

        //find textView for magnitude
        TextView magTextView = updatedView.findViewById(R.id.item_magnitude);
        //assign the value to text of TextView
        magTextView.setText(String.valueOf(magValueDouble));

        //change background color of magnitude circle depends on its value
        GradientDrawable circleGradiant = (GradientDrawable)magTextView.getBackground();
        circleGradiant.setColor(getMagColor(magValueDouble));

        //find textView for date
        TextView dateTextView = updatedView.findViewById(R.id.item_date);
        //assign the value to text of TextView
        dateTextView.setText(currentItem.getDate());
        //find textView for date Clock
        TextView clockTextView = updatedView.findViewById(R.id.item_clock);
        //assign the value to text of TextView
        clockTextView.setText(currentItem.getClock());

        return updatedView;
    }

    private int getMagColor(double magnitude){
        int color;
        //Change background color for magnitude depends of its value
        switch ((int)magnitude){
            case 1:
                color = R.color.magnitude1;
                break;
            case 2:
                color = R.color.magnitude2;
                break;
            case 3:
                color = R.color.magnitude3;
                break;
            case 4:
                color = R.color.magnitude4;
                break;
            case 5:
                color = R.color.magnitude5;
                break;
            case 6:
                color = R.color.magnitude6;
                break;
            case 7:
                color = R.color.magnitude7;
                break;
            case 8:
                color = R.color.magnitude8;
                break;
            case 9:
                color = R.color.magnitude9;
                break;
            default:
                color = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), color);
    }
}
