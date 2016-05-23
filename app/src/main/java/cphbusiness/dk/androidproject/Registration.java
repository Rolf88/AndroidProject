package cphbusiness.dk.androidproject;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private EditText editName;
    private EditText editMail;
    private EditText editPass;
    private Button btn;
    private boolean isUsed;
    private boolean hasRunned;
    private User mySelf1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        editName = (EditText) findViewById(R.id.editTextName);
        editMail = (EditText) findViewById(R.id.editTextEmail);
        editPass = (EditText) findViewById(R.id.editTextPassword);
        btn = (Button) findViewById(R.id.btn);
        isUsed = false;
        hasRunned = false;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewLogin();
            }
        });
    }

    private void addNewLogin() {
        String name = editName.getText().toString();
        String email = editMail.getText().toString();
        String password = editPass.getText().toString();


        if (name != null && email != null && password != null) {
            mySelf1 = new User(name, email, password, 0.0, 0.0);
        } else {
            Toast.makeText(Registration.this, "You need to fill out the forms", Toast.LENGTH_SHORT).show();
        }

        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean tempIsUsed = false;
                boolean hasRunned = false;
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

                    if (tempUser.getEmail().equals(mySelf1.getEmail())) {
                        tempIsUsed = true;
                        hasRunned = true;
                        changeHasRunned(hasRunned);
                        changeIsUsed(tempIsUsed);
                        break;
                    } else {
                        tempIsUsed = false;
                        hasRunned = true;
                        changeHasRunned(hasRunned);
                        changeIsUsed(tempIsUsed);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(Registration.this, firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        if (hasRunned) {
            hasRunned = false;
            if (!isUsed) {
                sendToDB();
                finish();
            } else if(isUsed){
                editMail.setText(null);
                Toast.makeText(Registration.this, "Mail allready exsist, type in another email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendToDB() {
        Firebase putRef = firebaseRef.child(mySelf1.getName());
        Map<String, Object> map = new HashMap();
        map.put("name", mySelf1.getName());
        map.put("email", mySelf1.getEmail());
        map.put("password", mySelf1.getPassword());
        map.put("latitude", new Double(mySelf1.getLatitude()).toString());
        map.put("longitude", new Double(mySelf1.getLongitude()).toString());
        putRef.setValue(map);
    }

    private void changeIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    private void changeHasRunned(boolean hasRunned) {
        this.hasRunned = hasRunned;
    }
}
