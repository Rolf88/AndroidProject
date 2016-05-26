package cphbusiness.dk.androidproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private UiSettings mapSettings;
    private Intent i;
    private double friendLat;
    private double friendLong;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        i = getIntent();
        friendLat = i.getDoubleExtra("latitude", 1);
        friendLong = i.getDoubleExtra("longitude", 1);
        name = i.getStringExtra("name");
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
        mapSettings = mMap.getUiSettings();
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        //latitude of location
        double myLatitude = myLocation.getLatitude();

        //longitude og location
        double myLongitude = myLocation.getLongitude();

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

        //if you want to go to your location
        // mMap.setMyLocationEnabled(true);

        //Adding a marker in copenhagen + make a line from destination to copenhagen
        //LatLng copenhagen = new LatLng(55.68, 12.59);
        //mMap.addMarker(new MarkerOptions().position(copenhagen).title("Marker in Copenhagen"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(copenhagen));
        //mMap.addPolyline(new PolylineOptions().add(new LatLng(myLatitude, myLongitude)).add(new LatLng(55.68, 12.59)));

        //zooming to friend
        LatLng myFriend = new LatLng(friendLat, friendLong);
        mMap.addMarker(new MarkerOptions().position(myFriend).title("Here is " + name + "!"));
        float zoomLevel = (float) 16.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myFriend, zoomLevel));
        mapSettings.setZoomControlsEnabled(true);
    }
}
