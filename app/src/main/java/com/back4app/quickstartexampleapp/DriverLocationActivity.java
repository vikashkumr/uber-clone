package com.back4app.quickstartexampleapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Intent intent;
    public void acceptRequest(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects){
                            object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("driverLatitude",0) + "," + intent.getDoubleExtra("driverLongitude",0) + "&daddr=" + intent.getDoubleExtra("requestLatitude",0) + "," + intent.getDoubleExtra("requestLongitude",0) + ""));
                                        startActivity(directionsIntent);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRelativeLayout);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        intent = getIntent();
        LatLng driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0), intent.getDoubleExtra("driverLongitude", 0));
        LatLng requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude", 0), intent.getDoubleExtra("requestLongitude", 0));
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location")));
        markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Rider Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker : markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

//        int padding = 60;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


        mMap.animateCamera(cu);
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        mMap.animateCamera(cu);
    }
}
