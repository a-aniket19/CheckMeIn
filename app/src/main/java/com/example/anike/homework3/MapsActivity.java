package com.example.anike.homework3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    EditText name;
    String newName;
    LatLng newLatLng;
    int locationChangeNumber =0;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    CoordinatorLayout coordinatorLayout;
    Boolean SnackBarActive = false;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        coordinatorLayout = findViewById(R.id.coordinator);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        final SQLiteDatabase mydata = openOrCreateDatabase("Maps", MODE_PRIVATE,null);
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task location1 = mFusedLocationProviderClient.getLastLocation();
                location1.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Boolean locationFound = false;
                            locationChangeNumber ++;
                            Log.d(TAG, "onComplete: location change number "+Integer.toString(locationChangeNumber));
                            Location currentLocation =  (Location) task.getResult();
                            double liveLat = currentLocation.getLatitude();
                            double livelong = currentLocation.getLongitude();
                            Cursor live = mydata.rawQuery("Select Name, Latitude, Longitude, TimeStamp from checkin",
                                    null);
                            while (live.moveToNext()){
                                if (distance(liveLat,livelong,live.getDouble(live.getColumnIndex("Latitude")),
                                        live.getDouble(live.getColumnIndex("Longitude")))< 30 && !SnackBarActive){
                                    Log.d(TAG, "onComplete: the location name is : "+live.getString(live.getColumnIndex("Name")));
                                    Log.d(TAG, "onComplete: was last visited on : "+live.getString(live.getColumnIndex("TimeStamp")));
                                    showSnackBar(live.getString(live.getColumnIndex("Name")),
                                            live.getString(live.getColumnIndex("TimeStamp")),true);
                                    LatLng temp = new LatLng(live.getDouble(live.getColumnIndex("Latitude")), live.getDouble(live.getColumnIndex("Longitude")));

                                    SnackBarActive=true;
                                    break;
                                }
                                else if (distance(liveLat,livelong,live.getDouble(live.getColumnIndex("Latitude")),
                                        live.getDouble(live.getColumnIndex("Longitude")))< 30 && SnackBarActive){
                                        break;
                                }
                                else{
                                    if (snackbar != null){
                                        Log.d(TAG, "onComplete: object is not null");
                                        snackbar.dismiss();
                                    }
                                    SnackBarActive= false;
                                }

                            }
                        }
                    }
                });
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
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);


//        Cursor c = mydata.rawQuery("Select * from Livedata Limit 1",null);
//        c.moveToFirst();
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(c.getDouble(c.getColumnIndex("Latitude")), c.getDouble(c.getColumnIndex("Longitude"))))
//                    .zoom(17)                   // Sets the zoom
//                    .build();                   // Creates a CameraPosition from the builder
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getDeviceLocation();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        //Adding marker on the map

        Cursor c1 =  mydata.rawQuery("Select * from checkin",null);
        int LatIndex = c1.getColumnIndex("Latitude");
        int LonIndex = c1.getColumnIndex("Longitude");
        int nameIndex = c1.getColumnIndex("Name");
        while(c1.moveToNext()){
            LatLng latLng = new LatLng(c1.getDouble(LatIndex),c1.getDouble(LonIndex));
            mMap.addMarker(new MarkerOptions().position(latLng).title(c1.getString(nameIndex)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }



        Cursor c2 = mydata.rawQuery("select * from newLocation", null);
        int newNametable = c2.getColumnIndex("Name");
        int newLattable = c2.getColumnIndex("Latitude");
        int newLontable = c2.getColumnIndex("Longitude");
            //int newtimetable= c2.getColumnIndex("TimeStamp");
        while (c2.moveToNext()) {
                LatLng latLngNew = new LatLng(c2.getDouble(newLattable), c2.getDouble(newLontable));

                mMap.addMarker(new MarkerOptions().position(latLngNew).title(c2.getString(newNametable)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngNew));
            }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name");
        builder.setMessage("Enter name for the new Location");
        name= new EditText(this );
        builder.setView(name);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newName  = name.getText().toString();
                getName(newLatLng,mMap,newName);
            }
        });



        final AlertDialog ad = builder.create() ;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                ad.show();
                newLatLng = latLng;

            }
        });

    }

    private void showSnackBar(String name, String timeStamp,Boolean s) {
        snackbar = Snackbar.make(coordinatorLayout,"Location name is  "+name+"and was last check was on "+timeStamp,
                Snackbar.LENGTH_INDEFINITE);

        //testing the snack bar.
        //auto check in
        //Report and UI
        //Graduate Requirement
        //Address using geo coder
        if (s){
            snackbar.show();
            Log.d(TAG, "showSnackBar: displaying snack bar");
        }else{
            Log.d(TAG, "showSnackBar: dismissing snack bar");
            snackbar.dismiss();
        }
    }

    private void getName(LatLng newLatLng, GoogleMap mMap, String newName) {
        mMap.addMarker(new MarkerOptions().position(newLatLng).title(newName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        SQLiteDatabase mydata = openOrCreateDatabase("Maps",MODE_PRIVATE,null);
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z "); // the format of your date
        String formattedDate = sdf.format(current);
        Log.d(TAG, "getName: time is "+formattedDate);
        mydata.execSQL("Create table if not exists newLocation ( Name Varchar,Latitude Double, Longitude Double, TimeStamp Varchar)");
        mydata.execSQL("insert into newLocation values ('"+newName+"','"+newLatLng.latitude+"','"+newLatLng.longitude+"','"+formattedDate+"')");
    }


    private  void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the current locstion of the device");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                return;
            }
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation =  (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),15));
                    }
                    else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapsActivity.this,"Couldn't get the location",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exception"+e.getMessage() );
        }
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


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
