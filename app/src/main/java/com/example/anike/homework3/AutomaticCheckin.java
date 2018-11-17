package com.example.anike.homework3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by anike on 13-11-2018.
 */

public class AutomaticCheckin extends Service {
    LocationListener  locationListener;
    LocationListener locationListener1;
    LocationManager locationManager2;
    LocationManager locationManager;
    private static final String TAG = "AutomaticCheckin";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: started");
        Toast.makeText(this, " Automatic check in Active", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager2= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: Auto check in after 1 min ");
                Double lat = location.getLatitude();
                Double lon= location.getLongitude();
                location.getAccuracy();
                long   time = location.getTime();
                Date date = new Date(time); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z "); // the format of your date
                String formattedDate = sdf.format(date);
                SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
                mydata.execSQL("Create table if not exists checkin ( Name Varchar,Latitude Double, Longitude Double," +
                        " Address Varchar, TimeStamp Varvchar )");

                /**
                 * Address has to be added
                 */
                Log.d(TAG, "onLocationChanged: adding data");
                mydata.execSQL(" Insert into checkin (Latitude, Longitude, TimeStamp)" +
                        "Values ('"+ lat + "','" + lon + "','" + formattedDate + "')");


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


        locationListener1= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: check in due to distance");
                Double lat = location.getLatitude();
                Double lon= location.getLongitude();
                location.getAccuracy();
                long   time = location.getTime();
                Date date = new Date(time); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z "); // the format of your date
                String formattedDate = sdf.format(date);
                SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE, null);
                mydata.execSQL("Create table if not exists checkin ( Name Varchar,Latitude Double, Longitude Double," +
                        " Address Varchar, TimeStamp Varvchar )");

                /**
                 * Address has to be added
                 */
                Log.d(TAG, "onLocationChanged: adding data");
                mydata.execSQL(" Insert into checkin (Latitude, Longitude, TimeStamp)" +
                        "Values ('"+ lat + "','" + lon + "','" + formattedDate + "')");


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

        locationManager2.requestLocationUpdates(locationManager2.NETWORK_PROVIDER,0,100,locationListener1);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 300000,0, locationListener);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy: destroying");
        locationManager.removeUpdates(locationListener);
        locationManager2.removeUpdates(locationListener1);
        super.onDestroy();
        Toast.makeText(this, "Auto check in turned Off", Toast.LENGTH_LONG).show();
    }
}
