package edu.ewubd.lost_it;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;


public class PoliceStationInfoInputActivity extends AppCompatActivity {

    private EditText pStationName, pStationLocation, pStationNumber;
    private TextView btnSave;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    DatabaseReference databaseReference;

    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private ImageView Imv_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_info_input);

        databaseReference = FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("PoliceStations");

        pStationName = findViewById(R.id.police_stationName);
        pStationLocation = findViewById(R.id.police_stationLocation);
        pStationNumber = findViewById(R.id.police_stationNumber);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Imv_logo = findViewById(R.id.Imv_logo);

        Imv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PoliceStationInfoInputActivity.this, All_Home_Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        btnSave = findViewById(R.id.psbtnSave);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData1();
            }
        });

    }

    public void saveData1(){
        String StationName = pStationName.getText().toString().trim();
        String StationLocation = pStationLocation.getText().toString().trim();
        String StationNumber = pStationNumber.getText().toString().trim();
        String key = databaseReference.push().getKey();

        PoliceInfoHelperClass stations = new PoliceInfoHelperClass(StationName, StationLocation, StationNumber);

        databaseReference.child(key).setValue(stations);
        Toast.makeText(PoliceStationInfoInputActivity.this, "Station details added", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(PoliceStationInfoInputActivity.this, PoliceStationDetailsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == (R.id.menu_logout)){
            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor prefsEditor;

            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            prefsEditor = sharedPreferences.edit();
            prefsEditor.putString("remember", null);
            prefsEditor.commit();

            startActivity(new Intent(getApplicationContext(), LoginSignupActivity.class));
            finish();
        }
        if(item.getItemId() == (R.id.menu_profile)){
            startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
        }
        if(item.getItemId() == (R.id.menu_police)){
            startActivity(new Intent(getApplicationContext(), PoliceStationDetailsActivity.class));
        }
        if(item.getItemId() == (R.id.menu_setting)){
            Toast.makeText(this, "Setting button clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}