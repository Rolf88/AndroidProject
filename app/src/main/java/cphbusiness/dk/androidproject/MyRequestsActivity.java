package cphbusiness.dk.androidproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRequestsActivity extends AppCompatActivity {
    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private Firebase firebaseRequest;
    private static final String FRIENDREQ_URL = "https://friend-request-5555.firebaseio.com";
    private Intent i;
    private ArrayList<String> requestStringList;
    private User mySelf;
    private ListView listView;
    private List<User> friendList;
    private DatabaseOperation db;
    private Context ctx;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        firebaseRequest = new Firebase(FRIENDREQ_URL);
        friendList = new ArrayList();

        getUsers();

        i = getIntent();
        requestStringList = i.getStringArrayListExtra("requests");
        mySelf = (User) i.getSerializableExtra("mySelf");
        listView = (ListView) findViewById(R.id.listViewMyF);
        ctx = this;
        db = new DatabaseOperation(ctx);
        adapter = new ArrayAdapter<String>(MyRequestsActivity.this, android.R.layout.simple_list_item_1, requestStringList);


        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (User user : friendList) {
                    if (user.getName().equals(requestStringList.get((int) id))) {
                        FriendRequest request = new FriendRequest();
                        request.setAccepted("true");
                        request.setFriendAccepter(mySelf.getName());
                        request.setFriendSeeker(user.getName());
                        db.putInformation(db, user);
                        sendRequestToDB(request);
                        Intent i = new Intent();
                        i.putExtra("requestName", user.getName());
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
            }
        });
    }

    private void sendRequestToDB(FriendRequest friendRequest) {
        String myBoolean = friendRequest.getAccepted();
        String seeker = friendRequest.getFriendSeeker();
        String accepter = friendRequest.getFriendAccepter();

        Firebase putRef = firebaseRequest.child(seeker + "+" + accepter);
        Map<String, String> map = new HashMap();
        map.put("seeker", seeker);
        map.put("accepter", accepter);
        map.put("boolean", myBoolean);
        putRef.setValue(map);
    }

    private void getUsers() {
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String namePath = postSnapshot.getKey() + "/name";
                    String emailPath = postSnapshot.getKey() + "/email";
                    String passwordPath = postSnapshot.getKey() + "/password";
                    String phonenoPath = postSnapshot.getKey() + "/phoneno";
                    String latitudePath = postSnapshot.getKey() + "/latitude";
                    String longitudePath = postSnapshot.getKey() + "/longitude";

                    String tempName = dataSnapshot.child(namePath).getValue().toString();
                    String tempEmail = dataSnapshot.child(emailPath).getValue().toString();
                    String tempPassword = dataSnapshot.child(passwordPath).getValue().toString();
                    String tempPhoneno = dataSnapshot.child(phonenoPath).getValue().toString();
                    double tempLatitude = Double.parseDouble(dataSnapshot.child(latitudePath).getValue().toString());
                    double tempLongitude = Double.parseDouble(dataSnapshot.child(longitudePath).getValue().toString());

                    User tempUser = new User(tempName, tempEmail, tempPassword, tempPhoneno, tempLatitude, tempLongitude);
                    friendList.add(tempUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
