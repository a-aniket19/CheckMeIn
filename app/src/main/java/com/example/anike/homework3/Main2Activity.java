package com.example.anike.homework3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";
    Long d1[] = new Long [1000];
    Long d2[] = new Long [1000];
    long g,d;
    int i=0;
    int j=0;
    LocationListener locationListener2;
    LocationListener locationListener1;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
         locationListener1 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();
                Log.d(TAG, "onLocationChanged: network accuracy is "+Float.toString(accuracy));
                d1[i] = System.nanoTime();
                Log.d(TAG, "onLocationChanged: time is "+Long.toString(d1[i]));
                if (i==0){
                    Log.d(TAG, "onLocationChanged: the delay for network is  :"+(d1[i]-d));
                }
                i++;
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
         d = System.nanoTime();
        final LocationManager locationManager2 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener2 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy2 = location.getAccuracy();
                Log.d(TAG, "onLocationChanged: GPS accuracy is  "+Float.toString(accuracy2));
                d2[j]= System.nanoTime();
                Log.d(TAG, "onLocationChanged: time is "+Long.toString(d2[j]));
                if (j==0){
                    Log.d(TAG, "onLocationChanged: the delay for GPS  "+ (d2[j]-g));
                }
                Long[] diffSeconds = new Long[100];
//                diffSeconds[j] = (Long) d2[j] - d1[j];
             //   Log.d(TAG, "onLocationChanged: time Difference is "+Long.toString(diffSeconds[j]));
                j++;
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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener1);
       // locationManager2.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener2);
        final Button gps = findViewById(R.id.gps);
        final Button net = findViewById(R.id.network);
        final TextView accuracy = findViewById(R.id.textView2);
        gps.setVisibility(View.VISIBLE);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                gps.setVisibility(View.INVISIBLE);
                net.setVisibility(View.VISIBLE);
                locationManager.removeUpdates(locationListener1);
                accuracy.setText("Logging GPS accuracy");
                 j=0;
                 g = System.nanoTime();
                locationManager2.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener2);
            }
        });

        net.setVisibility(View.INVISIBLE);
        net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                net.setVisibility(View.INVISIBLE);
                gps.setVisibility(View.VISIBLE);
                accuracy.setText("Logging Network accuracy");
                locationManager.removeUpdates(locationListener2);
                i=0;
                d=System.nanoTime();
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,0,0,locationListener1);

            }
        });
    }
}
