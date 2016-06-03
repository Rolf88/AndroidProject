package cphbusiness.dk.androidproject;

import android.os.Bundle;
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

public class RegistrationActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private static final String FIREBASE_URL = "https://torrid-inferno-4868.firebaseio.com";
    private EditText editName;
    private EditText editMail;
    private EditText editPass;
    private EditText editPhoneno;
    private Button btn;
    private User mySelf1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        editName = (EditText) findViewById(R.id.editTextName);
        editMail = (EditText) findViewById(R.id.editTextEmail);
        editPass = (EditText) findViewById(R.id.editTextPassword);
        editPhoneno = (EditText) findViewById(R.id.editTextPhoneno);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFormIsFilled();
            }
        });
    }

    private void checkNameEmail() {
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isUsed = false;
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
                    String tempPhoneNo = dataSnapshot.child(phonenoPath).getValue().toString();
                    double tempLatitude = Double.parseDouble(dataSnapshot.child(latitudePath).getValue().toString());
                    double tempLongitude = Double.parseDouble(dataSnapshot.child(longitudePath).getValue().toString());

                    User tempUser = new User(tempName, tempEmail, tempPassword, tempPhoneNo, tempLatitude, tempLongitude);

                    if (tempUser.getName().equals(mySelf1.getName()) || tempUser.getEmail().equals(mySelf1.getName())) {
                        isUsed = true;
                        break;
                    } else {
                        isUsed = false;
                    }

                }
                if (!isUsed) {
                    sendToFDB();
                    finish();
                } else if (isUsed) {
                    editMail.setText(null);
                    editName.setText(null);
                    Toast.makeText(RegistrationActivity.this, "Name and Email allready exsist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(RegistrationActivity.this, "Error" + firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFormIsFilled() {
        String name = editName.getText().toString();
        String email = editMail.getText().toString();
        String password = editPass.getText().toString();
        String phoneno = editPhoneno.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneno.isEmpty()) {
            mySelf1 = new User(name, email, password, phoneno, 0.0, 0.0);
            checkNameEmail();
        } else {
            Toast.makeText(RegistrationActivity.this, "You need to fill out the forms", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToFDB() {
        Firebase putRef = firebaseRef.child(mySelf1.getName());
        Map<String, String> map = new HashMap();
        map.put("name", mySelf1.getName());
        map.put("email", mySelf1.getEmail());
        map.put("password", mySelf1.getPassword());
        map.put("phoneno", mySelf1.getPhoneNo());
        map.put("latitude", new Double(mySelf1.getLatitude()).toString());
        map.put("longitude", new Double(mySelf1.getLongitude()).toString());
        putRef.setValue(map);
    }
}
