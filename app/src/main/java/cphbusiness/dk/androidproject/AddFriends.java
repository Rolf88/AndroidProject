package cphbusiness.dk.androidproject;

import android.content.Context;
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
    private EditText editSearch;
    private ListView listView;
    private Button btnSearch;
    private DatabaseOperation dbOp;
    private Context ctx;
    private List<User> findFriendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editSearch = (EditText) findViewById(R.id.editTextFf);
        listView = (ListView) findViewById(R.id.listViewFf);
        btnSearch = (Button) findViewById(R.id.buttonFf);
        findFriendsList = new ArrayList();
        ctx = this;
        dbOp = new DatabaseOperation(ctx);

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
                    findFriendsList.add(tempUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(AddFriends.this, firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        listView.setAdapter(new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, findFriendsList));
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User tempUser2 = findFriendsList.get((int)id);
                dbOp.putInformation(dbOp,tempUser2);
                finish();
            }
        });
    }

}
