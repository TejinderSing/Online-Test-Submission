package com.example.maps_tejinder_singh_792806;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.ULocale;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Counter
    int count = 0;
    private GoogleMap mMap;

    //GeoCoder



    private static final int REQUEST_CODE = 1;
    //Markers
    private Marker homemarker;
    private Marker destmarker;

    //Polyline and polygon
    //Polyline line;
    Polygon gon;
    private static final int POLYGON_SIDES = 4;
    //Markers List
    List<Marker> markers = new ArrayList();

    //Location Manager and Location Listener

    LocationListener locationListener;
    LocationManager locationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //setHomemarker();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
       // setHomemarker(location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               setHomemarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

        };

        if(!hasLocationPermssion()){
            requestLocationPermission();
        }
        else {
            startUpdateLocation();
        }

        //LongPressGesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                count ++;
                switch (count){
                    case 1:
                        setMarker(latLng, "A");
                        break;
                    case 2:
                        setMarker(latLng, "B");
                        break;
                    case 3:
                        setMarker(latLng, "C");
                        break;
                    case 4:
                        setMarker(latLng, "D");
                        break;
                    default:
                        setMarker(latLng);
                }
                if(count == 4){
                    drawPolygon();
                }

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

            }
        });

//        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//            @Override
//            public void onPolylineClick(Polyline polyline) {
//
//            }
//        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),"Locality:"+addresses.get(0).getLocality()+"\nCountry:"+addresses.get(0).getCountryName()+"\nPostal Code:"+addresses.get(0).getPostalCode(),Toast.LENGTH_LONG);
                toast.show();
                double dd = countDistance(homemarker,marker);
                marker.setSnippet("Distance is "+dd);
                return false;
            }

        });
    }




    private void startUpdateLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);


    }
    private void requestLocationPermission(){
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
    private boolean hasLocationPermssion(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void setHomemarker(Location location){
        //Location ll = mMap.getMyLocation();
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation).title("You are here").snippet("Usr Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        homemarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    private void setMarker(LatLng latLng, String a){

        MarkerOptions options = new MarkerOptions().position(latLng).title(a);
        destmarker = mMap.addMarker(options);

        destmarker.setDraggable(true);
        markers.add(destmarker);
        destmarker.showInfoWindow();

        //drawLine();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
//    private void drawLine(){
//        PolylineOptions options = new PolylineOptions().color(Color.BLACK).width(10).add(homemarker.getPosition(),destmarker.getPosition());
//        line = mMap.addPolyline(options);
//        line.setTag("Hello");
//        line.setClickable(true);
//
//    }
    private void drawPolygon(){
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x33000000)
                .strokeColor(Color.RED)
                .strokeWidth(5);

        for (int i=0; i<POLYGON_SIDES; i++) {
            options.add(markers.get(i).getPosition());
        }


        gon = mMap.addPolygon(options);
    }
    private double countDistance(Marker homemarker, Marker destmarker){
        float[] distance = new float[1];
        Location.distanceBetween(homemarker.getPosition().latitude,homemarker.getPosition().longitude,destmarker.getPosition().latitude,destmarker.getPosition().longitude,distance);
        return Double.parseDouble(String.valueOf(distance[0]));
    }
    private void setMarker(LatLng latLng){

        MarkerOptions options = new MarkerOptions().position(latLng).title("Nothing");
        destmarker = mMap.addMarker(options);

        destmarker.setDraggable(true);
        destmarker.showInfoWindow();

        //drawLine();
    }

}