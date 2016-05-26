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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private ListView listView;
    private List<User> userList;
    private List<User> tempfriendList;
    private LocationManager locationManager;
    private Criteria criteria;
    private double myLatitude;
    private double myLongitude;
    private Intent i;
    private DatabaseOperation db;
    private Context ctx;
    public User mySelf;
    public ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        userList = new ArrayList();
        tempfriendList = new ArrayList();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        ctx = this;
        db = new DatabaseOperation(ctx);
        String provider = locationManager.getBestProvider(criteria, true);
        i = getIntent();
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        mySelf = (User) i.getSerializableExtra("mySelf");
        stringList = i.getStringArrayListExtra("userNames");

        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);
        myLatitude = myLocation.getLatitude();
        myLongitude = myLocation.getLongitude();

        mySelf.setLatitude(myLatitude);
        mySelf.setLongitude(myLongitude);

        //Remember to add mySelf to the firebasedb with the new locations
        sendToFireDB();

        Cursor CR = db.getInformations(db);
        if (CR.moveToFirst()) {
            do {
                User tempUser = new User();
                tempUser.setName(CR.getString(1));
                tempUser.setEmail(CR.getString(2));
                tempUser.setLatitude(CR.getDouble(3));
                tempUser.setLongitude(CR.getDouble(4));

                tempfriendList.add(tempUser);
            } while (CR.moveToNext());

        } else {
            Toast.makeText(MainActivity.this, "You have no friends find some in Find Friends in the menu", Toast.LENGTH_LONG).show();
        }

        listView.setAdapter(new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, tempfriendList));
        listView.setTextFilterEnabled(true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);

                i.putExtra("latitude", tempfriendList.get((int) id).getLatitude());
                i.putExtra("longitude", tempfriendList.get((int) id).getLongitude());
                i.putExtra("name", tempfriendList.get((int) id).getName());
                startActivity(i);

            }
        });


    }

    /*private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            in.putStringArrayListExtra("userNames", stringList);
            in.putExtra("mySelf", mySelf);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToFireDB() {
        Firebase putRef = firebaseRef.child(mySelf.getName());
        Map<String, String> map = new HashMap();
        map.put("name", mySelf.getName());
        map.put("email", mySelf.getEmail());
        map.put("password", mySelf.getPassword());
        map.put("latitude", new Double(mySelf.getLatitude()).toString());
        map.put("longitude", new Double(mySelf.getLongitude()).toString());
        putRef.setValue(map);
    }
}
