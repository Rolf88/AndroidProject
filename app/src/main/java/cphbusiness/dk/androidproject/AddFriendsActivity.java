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

public class AddFriendsActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private Firebase firebaseRequest;
    private static final String FRIENDREQ_URL = "https://friend-request-5555.firebaseio.com";
    private ListView listView;
    private List<User> friendList;
    private DatabaseOperation db;
    private Context ctx;
    private Intent i;
    private User mySelf1;
    private ArrayList<String> stringList;
    private ArrayAdapter adapter;
    private SMSService smsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        firebaseRequest = new Firebase(FRIENDREQ_URL);

        smsService = new SMSService(this);
        ctx = this;
        db = new DatabaseOperation(ctx);
        listView = (ListView) findViewById(R.id.listViewFf);
        friendList = new ArrayList();
        i = getIntent();
        mySelf1 = (User) i.getSerializableExtra("mySelf");
        stringList = i.getStringArrayListExtra("userNames");

        getUsers();

        adapter = new ArrayAdapter<String>(AddFriendsActivity.this, android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User tempUser = new User();
                boolean gotTheUser = false;
                for (User user : friendList) {
                    if (user.getName().equals(stringList.get((int) id))) {
                        tempUser = user;
                        gotTheUser = true;
                        break;
                    } else {
                        gotTheUser = false;
                    }
                }

                if (gotTheUser) {
                    FriendRequest friendRequest = new FriendRequest();
                    friendRequest.setFriendSeeker(mySelf1.getName());
                    friendRequest.setFriendAccepter(tempUser.getName());
                    friendRequest.setAccepted("false");
                    sendRequestToDB(friendRequest);
                    smsService.sendSMS(mySelf1,tempUser);
                }

                finish();
            }
        });
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

                    User tempUser = new User(tempName, tempEmail, tempPassword, tempPhoneno,tempLatitude, tempLongitude);
                    friendList.add(tempUser);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
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
}
