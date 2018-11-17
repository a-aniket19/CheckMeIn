package com.example.anike.homework3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anike on 08-11-2018.
 */

public class ArrayListAdapter extends ArrayAdapter<CheckInInfo> {
    private int mResource;
    private Context mcontext;


    public ArrayListAdapter(@NonNull Context context, int resource, @NonNull List<CheckInInfo> objects) {
        super(context, resource, objects);
        mResource= resource;
        mcontext= context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckInInfo info = getItem(position);
        Double lat, lon;
        String address, time;
        lat= info.getLatitude();
        lon = info.getLongitude();
        address= info.getAddress();
        time= info.getDate();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView=inflater.inflate(mResource,parent,false);
        TextView latit , longi, timestamp, add;
        latit= convertView.findViewById(R.id.LatInList);
        longi=convertView.findViewById(R.id.LongInList);
        add= convertView.findViewById(R.id.AddInList);
        timestamp= convertView.findViewById(R.id.TimeInList);


        latit.setText(Double.toString(lat));
        longi.setText(Double.toString(lon));
        timestamp.setText(time);
        add.setText(address);

        return convertView;
    }
}
