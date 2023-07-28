package com.example.classnavigator.ui.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.classnavigator.R;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] times;
    private final String[] classNames;
    private final String[] locations;
    private final String[] periods;

    public CustomAdapter(Activity context, String[] times, String[] classNames, String[] locations, String[] periods) {
        super(context, R.layout.list_item, times);
        this.context = context;
        this.times = times;
        this.classNames = classNames;
        this.locations = locations;
        this.periods = periods;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView textTime = rowView.findViewById(R.id.textTime);
        TextView textSubject = rowView.findViewById(R.id.textSubject);
        TextView textClassRoom = rowView.findViewById(R.id.textClassRoom);
        TextView textPeriod = rowView.findViewById(R.id.textPeriod);

        textTime.setText(times[position]);
        textSubject.setText(classNames[position]);
        textClassRoom.setText(locations[position]);
        textPeriod.setText(periods[position]);

        return rowView;
    }
}