package cphbusiness.dk.androidproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriends extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private String serverIp;
    private ListView listView;
    private List<User> friendList;
    private DatabaseOperation db;
    private Context ctx;
    private Intent i;
    private User mySelf1;
    private ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);
        ctx = this;
        db = new DatabaseOperation(ctx);
        listView = (ListView) findViewById(R.id.listViewFf);
        friendList = new ArrayList();
        i = getIntent();
        mySelf1 = (User) i.getSerializableExtra("mySelf");
        stringList = i.getStringArrayListExtra("userNames");

        myRun.run();

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
                Toast.makeText(AddFriends.this, "gotTheUser:" + gotTheUser, Toast.LENGTH_SHORT).show();
                if (gotTheUser) {
                    db.putInformation(db, tempUser);
                }

                finish();
            }
        });
    }

    Runnable myRun = new Runnable() {
        @Override
        public void run() {
            getUsers();
            try {

                Thread.sleep(1500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listView.setAdapter(new ArrayAdapter<String>(AddFriends.this, android.R.layout.simple_list_item_1, stringList));
            listView.setTextFilterEnabled(true);
        }
    };

    private void getUsers() {
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
                    friendList.add(tempUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
