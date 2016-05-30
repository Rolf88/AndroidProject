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
import android.os.Vibrator;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private Firebase firebaseRequest;
    private static final String FRIENDREQ_URL = "https://friend-request-5555.firebaseio.com";
    private ListView listView;
    private List<FriendRequest> requestList;
    private List<User> tempfriendList;
    private Map<String, User> friendMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private double myLatitude;
    private double myLongitude;
    private Intent i;
    private DatabaseOperation db;
    private Context ctx;
    public User mySelf;
    public ArrayList<String> stringList;
    public ArrayList<String> requestStringList;
    private Vibrator vibrator;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        friendMap = new HashMap<>();
        listView = (ListView) findViewById(R.id.listView);
        requestList = new ArrayList();
        tempfriendList = new ArrayList();
        requestStringList = new ArrayList();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        ctx = this;
        db = new DatabaseOperation(ctx);
        String provider = locationManager.getBestProvider(criteria, true);
        i = getIntent();
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        firebaseRequest = new Firebase(FRIENDREQ_URL);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mySelf = (User) i.getSerializableExtra("mySelf");
        stringList = i.getStringArrayListExtra("userNames");
        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, tempfriendList);

        getUsersFromFDB();

        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);
        myLatitude = myLocation.getLatitude();
        myLongitude = myLocation.getLongitude();

        mySelf.setLatitude(myLatitude);
        mySelf.setLongitude(myLongitude);

        sendToFireDB();

        getRequestsFromFDB();

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

    @Override
    public void onResume() {
        super.onResume();
        tempfriendList.clear();
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
            Toast.makeText(MainActivity.this, "You have no friends find some in Find Friends in the menu", Toast.LENGTH_SHORT).show();
        }

        List<FriendRequest> tempRequestList = requestList;
        ArrayList<String> tempRequestStringList = requestStringList;

        for (User user : tempfriendList) {
            for (String name : tempRequestStringList) {
                if (user.getName().equals(name)) {
                    requestStringList.remove(name);
                }
            }
        }

        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
    }

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
        } else if (id == R.id.my_friends) {
            Intent in = new Intent(MainActivity.this, MyFriends.class);
            in.putStringArrayListExtra("requests", requestStringList);
            in.putExtra("mySelf", mySelf);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteRequest(FriendRequest request) {
        firebaseRequest.child(request.getFriendSeeker() + "+" + request.getFriendAccepter()).removeValue();
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


    private void friendsOrNot() {
        if (!requestList.isEmpty()) {
            for (FriendRequest request : requestList) {
                if (request.getFriendAccepter().equals(mySelf.getName()) && request.getAccepted().equals("false")) {
                    requestStringList.add(request.getFriendSeeker());
                    vibrator.vibrate(200);
                    Toast.makeText(MainActivity.this, request.getFriendSeeker() + ", wants to be Friends, Go to myFriend menu!", Toast.LENGTH_SHORT).show();
                } else if (request.getFriendSeeker().equals(mySelf.getName()) && request.getAccepted().equals("true")) {
                    deleteRequest(request);
                    db.putInformation(db, friendMap.get(request.getFriendAccepter()));
                    //does it work?
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void getUsersFromFDB() {
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String namePath = postSnapshot.getKey() + "/name";
                    String emailPath = postSnapshot.getKey() + "/email";
                    String passwordPath = postSnapshot.getKey() + "/password";
                    String latitudePath = postSnapshot.getKey() + "/latitude";
                    String longitudePath = postSnapshot.getKey() + "/longitude";

                    String tempName = dataSnapshot.child(namePath).getValue().toString();
                    String tempEmail = dataSnapshot.child(emailPath).getValue().toString();
                    String tempPassword = dataSnapshot.child(passwordPath).getValue().toString();
                    double tempLatitude = Double.parseDouble(dataSnapshot.child(latitudePath).getValue().toString());
                    double tempLongitude = Double.parseDouble(dataSnapshot.child(longitudePath).getValue().toString());

                    User tempUser = new User(tempName, tempEmail, tempPassword, tempLatitude, tempLongitude);
                    friendMap.put(tempName, tempUser);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void getRequestsFromFDB() {
        requestList.clear();
        requestStringList.clear();
        firebaseRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String seekerPath = postSnapshot.getKey() + "/seeker";
                    String accepterPath = postSnapshot.getKey() + "/accepter";
                    String booleanPath = postSnapshot.getKey() + "/boolean";

                    String tempSeeker = dataSnapshot.child(seekerPath).getValue().toString();
                    String tempAccepter = dataSnapshot.child(accepterPath).getValue().toString();
                    String tempBoolean = dataSnapshot.child(booleanPath).getValue().toString();

                    FriendRequest friendRequest = new FriendRequest();
                    friendRequest.setFriendSeeker(tempSeeker);
                    friendRequest.setFriendAccepter(tempAccepter);
                    friendRequest.setAccepted(tempBoolean);
                    requestList.add(friendRequest);
                }
                friendsOrNot();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
