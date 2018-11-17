package com.example.anike.homework3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    Boolean PermissionGranted ;
    private static final String TAG = "MainActivity";
    EditText checkinText;
    TextView latitude;
    TextView longitude;
    ListView listView;
    double lat;
    double lon;
    long time;
    String address;
    public ArrayListAdapter adapter;
    public ArrayList<CheckInInfo> checkInInfos = new ArrayList<>();
    Boolean refresh = true;
    Boolean autoMode = false;
    LocationManager locationManager;
    LocationListener locationListener;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View _mw = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(_mw);
       if (savedInstanceState != null) {
           autoMode = savedInstanceState.getBoolean("AutoState");

           if (autoMode) {
               refresh = false;
           } else {
               refresh = true;
           }
       }
         else {
            Toast.makeText(MainActivity.this, " New Entry", Toast.LENGTH_SHORT).show();
        }
        LoadPreferences();

        SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
        mydata.execSQL("Create table if not exists checkin ( Name Varchar,Latitude Double, Longitude Double," +
                " Address Varchar, TimeStamp Varvchar )");
        Cursor hardCode= mydata.rawQuery("Select *from checkin",null);
        Log.d(TAG, "onCreate: hardCode.getCount()   "+hardCode.getCount());
        if (hardCode.getCount()==0){
            double hardlatp[] = {40.518696,40.521782,40.523691,40.526354,40.524258};
            double hardlon[] = {-74.460058,-74.456782,-74.456782,-74.466078,-74.464125};
            String names[] = {"Werblin Recreation","Buell Apartment","Busch Student Center","LSM","ARC"};
            String hardAddress[] = {"Bartholomew Rd, Piscataway Township, NJ 08854, USA","Bartholomew Rd, Piscataway Township, NJ 08854, USA",
                                    "Bartholomew Rd, Piscataway Township, NJ 08854, USA","Russell Apartments 725-748, Piscataway Township, NJ 08854, USA",
                                    "607 Allison Rd, Piscataway Township, NJ 08854, USA"};
            for (int k =0;k<5;k++) {
                double templat = hardlatp[k];
                mydata.execSQL(" Insert into checkin (Name, Latitude, Longitude, Address,TimeStamp)" +
                        "Values ('"+names[k]+"','" + hardlatp[k] + "','" +hardlon[k] +"','"+hardAddress[k]+"','NULL')");
            }

            Log.d(TAG, "onCreate: add hard coded locations");
        }
        checkinText = findViewById(R.id.checkinText);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        listView = findViewById(R.id.ListView);
        if (refresh) {
            inflateListView();
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Button auto = findViewById(R.id.autoCheck);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoMode) {
                    autoMode = false;
                    Log.d(TAG, "onClick: stopping service");
                    stopService(v);
                } else {
                    Log.d(TAG, "onClick: starting service");
                    autoMode = true;
                    startService(v);
                }
            }
        });
        latitude.setText(Double.toString(lat));
        longitude.setText(Double.toString(lon));

        Button Openmaps = findViewById(R.id.Openmaps);
        Openmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                time = location.getTime();

                SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
                mydata.execSQL("Create table if not exists Livedata ( Latitude Double, Longitude Double )");
                mydata.execSQL("Delete from Livedata ");
                mydata.execSQL(" Insert into Livedata ( Latitude, Longitude)" +
                        "Values ('" + lat + "','" + lon + "')");

                Log.d(TAG, "onLocationChanged: lat and long " + Double.toString(lat) + "    " + Double.toString(lon));

                Log.d(TAG, "onLocationChanged: time is :" + time);
                latitude.setText(Double.toString(lat));
                longitude.setText(Double.toString(lon));


                Date date = new Date(time); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z "); // the format of your date
                String formattedDate = sdf.format(date);
                Log.d(TAG, "onLocationChanged: date is :" + formattedDate);  //formatted date


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        Log.d(TAG, "onCreate: permission granted    "+PermissionGranted);
        if (PermissionGranted){
            Log.d(TAG, "onCreate: permission granted in main");
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        Button checkin = findViewById(R.id.Checkin);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CheckInName = checkinText.getText().toString();
                if (CheckInName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter the checkin Name", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "onLocationChanged: time is :" + time);
                    Log.d(TAG, "onClick: latitude " + lat);
                    Log.d(TAG, "onClick:  longitude " + lon);
                    Date date = new Date(time); // *1000 is to convert seconds to milliseconds
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z "); // the format of your date
                    String formattedDate = sdf.format(date);
                    Log.d(TAG, "onClick: the data and time is: " + formattedDate);
                    try {
                        address = getAddress(lat, lon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
                    Cursor live = mydata.rawQuery("Select Name, Latitude, Longitude,Address, TimeStamp from checkin",
                            null);
                    while (live.moveToNext()) {
                        if (distance(lat, lon, live.getDouble(live.getColumnIndex("Latitude")),
                                live.getDouble(live.getColumnIndex("Longitude"))) < 30) {
                            Log.d(TAG, "onClick: the location is within 30m of  " + live.getString(live.getColumnIndex("Address")));
                            CheckInName = live.getString(live.getColumnIndex("Name"));
                            if (live.getString(live.getColumnIndex("Address")) != null) {
                                address = live.getString(live.getColumnIndex("Address"));
                            }
                            break;
                        }
                    }


                    mydata.execSQL("Create table if not exists checkin ( Name Varchar,Latitude Double, Longitude Double," +
                            " Address Varchar, TimeStamp Varvchar )");
                    mydata.execSQL(" Insert into checkin (Name, Latitude, Longitude, Address,TimeStamp)" +
                            "Values ('" + CheckInName + "','" + lat + "','" + lon + "','" + address + "','" + formattedDate + "')");
                    if (checkInInfos.size() == 0) {
                        inflateListView();
                    } else if (checkInInfos.size() > 0) {
                        checkInInfos.add(new CheckInInfo(lat, lon, address, formattedDate));
                        adapter.notifyDataSetChanged();
                    }

                    checkinText.setText("");
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE,null);
                mydata.execSQL("Delete from checkin");
                mydata.execSQL("Delete from Livedata");
                mydata.execSQL("Delete from newLocation");
                checkInInfos.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");

        switch(requestCode){
            case 1:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: permission granted");

                            PermissionGranted =true;
                            Log.d(TAG, "onRequestPermissionsResult: permission granted"+PermissionGranted);
                            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                            return;
                        }
                    }


                    //initialize our map
                }
            }
        }
    }

    private void request() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: called");
        outState.putBoolean("AutoState",autoMode);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        savePreferences();
        super.onBackPressed();
    }

    private void savePreferences() {
        Log.d(TAG, "savePreferences: called");
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("AutoState",autoMode);
        editor.putBoolean("Permission",true);
        editor.apply();
    }

    private void LoadPreferences(){
        Log.d(TAG, "LoadPreferences: called");
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Boolean  state = sharedPreferences.getBoolean("AutoState", false);
        Boolean per = sharedPreferences.getBoolean("Permission",false);
        PermissionGranted = per;
        autoMode = state;
    }



    private String getAddress(double lat, double lon) throws IOException {
        String address ="";
        Geocoder geocoder = new Geocoder(MainActivity.this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon,1);
            address = addresses.get(0).getAddressLine(0);
            Log.d(TAG, "getAddress: Address is   "+address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    public void startService(View view) {
        startService(new Intent(getBaseContext(), AutomaticCheckin.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), AutomaticCheckin.class));
        checkInInfos.clear();
        inflateListView();
    }

    public void inflateListView() {
        SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
        Cursor c = mydata.rawQuery("Select * from checkin",null);
        int listAddress= c.getColumnIndex("Address");
        int listLat = c.getColumnIndex("Latitude");
        int listLon = c.getColumnIndex("Longitude");
        int listTime = c.getColumnIndex("TimeStamp");
        while (c.moveToNext()) {
            checkInInfos.add(new CheckInInfo(c.getDouble(listLat), c.getDouble(listLon), c.getString(listAddress), c.getString(listTime)));
        }
            adapter = new ArrayListAdapter(getApplicationContext(), R.layout.list_layout,checkInInfos);
            listView.setAdapter(adapter);

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515*1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void Grad(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }
}
