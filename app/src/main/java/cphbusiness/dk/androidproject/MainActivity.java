package cphbusiness.dk.androidproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private ListView listView;
    private LocationManager locationManager;
    private Criteria criteria;
    private double myLatitude;
    private double myLongitude;
    private DatabaseOperation db;
    private Context ctx;
    private Intent i;
    private User mySelf;
    private List<User> tempList;
    private List<User> myFriendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        ctx = this;
        db = new DatabaseOperation(ctx);
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        String provider = locationManager.getBestProvider(criteria, true);
        i = getIntent();
        mySelf = (User) i.getSerializableExtra("MySelf");
        myFriendsList = new ArrayList();


        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);
        myLatitude = myLocation.getLatitude();
        myLongitude = myLocation.getLongitude();
        mySelf.setLatitude(myLatitude);
        mySelf.setLongitude(myLongitude);
        upDateFirebase();

        Cursor CR = db.getInformations(db);
        CR.moveToFirst();
        do {
            User tempUser = new User();
            tempUser.setName(CR.getString(1));
            tempUser.setEmail(CR.getString(2));
            tempUser.setLatitude(CR.getDouble(4));
            tempUser.setLongitude(CR.getDouble(5));
            myFriendsList.add(tempUser);
        } while (CR.moveToNext());

        //Retrieve online friends
        tempList = new ArrayList();
        tempList.add(mySelf);

        listView.setAdapter(new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, tempList));
        listView.setTextFilterEnabled(true);
    }

    public void upDateFirebase() {
        Firebase putRef = firebaseRef.child(mySelf.getName().toString());
        Map<String, Object> map = new HashMap();
        map.put("latitude", new Double(mySelf.getLatitude()).toString());
        map.put("longitude", new Double(mySelf.getLongitude()).toString());
        putRef.updateChildren(map);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.find_friends) {
            Intent in = new Intent(MainActivity.this, AddFriends.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
