package edu.ewubd.lost_it;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Profile_Details_Activity extends AppCompatActivity {

    private TextView profile_details_user_name;
    private TextView profile_details_email;
    private TextView profile_details_name;
    private TextView profile_details_surname;
    private TextView profile_details_job_edu;
    private TextView profile_details_dob;
    private TextView profile_details_phone;
    private TextView profile_details_current_add;
    private TextView profile_details_home;
    private TextView profile_details_fb;
    private TextView profile_details_isntagram_link;
    private TextView profile_details_twitter;
    private TextView profile_details_whatsapp_number;
    private TextView btn_edit_profile_details;
    private Toolbar toolbar;
    private ImageView Imv_logo;

    private ImageView profile_details_img_upload;


    private ProgressBar post_progress_Circle;
    private DatabaseReference databaseReference;
    private List<PostClass> post_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profile_details_user_name = findViewById(R.id.profile_details_user_name);
        profile_details_name= findViewById(R.id.profile_details_name);
        profile_details_surname= findViewById(R.id.profile_details_surname);
        profile_details_dob = findViewById(R.id.profile_details_dob);
        profile_details_job_edu= findViewById(R.id.profile_details_job_edu);
        profile_details_email = findViewById(R.id.profile_details_email);
        profile_details_phone= findViewById(R.id.profile_details_phone);
        profile_details_current_add= findViewById(R.id.profile_details_current_add);
        profile_details_home= findViewById(R.id.profile_details_home);
        profile_details_fb= findViewById(R.id.profile_details_fb_link);
        profile_details_isntagram_link = findViewById(R.id.profile_details_isntagram_link);
        profile_details_twitter= findViewById(R.id.profile_details_twitter);
        profile_details_whatsapp_number= findViewById(R.id.pprofile_details_whatsapp_number);

        profile_details_img_upload = findViewById(R.id.profile_details_img_upload);
        Imv_logo = findViewById(R.id.Imv_logo);

        Imv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Details_Activity.this, All_Home_Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "");

        //         Start
        // fAuth.getCurrentUser().getEmail()
        FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").
                orderByChild("email")
                .equalTo(user_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value_of_key = "";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    value_of_key = childSnapshot.getKey();
                }
                if (snapshot.exists()) {
                    String name = snapshot.child(value_of_key).child("name").getValue(String.class);
                    String email = snapshot.child(value_of_key).child("email").getValue(String.class);
                    String surname = snapshot.child(value_of_key).child("surname").getValue(String.class);
                    String dob = snapshot.child(value_of_key).child("dob").getValue(String.class);
                    String institute = snapshot.child(value_of_key).child("institute").getValue(String.class);
                    String phone = snapshot.child(value_of_key).child("phone").getValue(String.class);
                    String currentAddress = snapshot.child(value_of_key).child("currentAddress").getValue(String.class);
                    String homeAddress = snapshot.child(value_of_key).child("homeAddress").getValue(String.class);
                    String fb = snapshot.child(value_of_key).child("fb").getValue(String.class);
                    String instagram = snapshot.child(value_of_key).child("insta").getValue(String.class);
                    String twitter = snapshot.child(value_of_key).child("twitter").getValue(String.class);
                    String whatsApp= snapshot.child(value_of_key).child("whatsApp").getValue(String.class);
                    String img_url= snapshot.child(value_of_key).child("userImageUrl").getValue(String.class);

                    profile_details_email.setText(email);
                    profile_details_current_add.setText(currentAddress);
                    profile_details_dob.setText(dob);
                    profile_details_isntagram_link.setText(instagram);
                    profile_details_fb.setText(fb);
                    profile_details_home.setText(homeAddress);
                    profile_details_job_edu.setText(institute);
                    profile_details_phone.setText(phone);
                    profile_details_surname.setText(surname);
                    profile_details_twitter.setText(twitter);
                    profile_details_name.setText(name);
                    profile_details_user_name.setText(name+ " ( "+ surname+ " )");
                    profile_details_whatsapp_number.setText(whatsApp);

                    Picasso.with(Profile_Details_Activity.this)
                            .load(img_url)
                            .fit()
                            .centerCrop()
                            .into(profile_details_img_upload);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btn_edit_profile_details= findViewById(R.id.btn_edit_profile_details);

        btn_edit_profile_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Details_Activity.this, Profile_Edit_Details_Activity.class);
                startActivity(i);
            }
        });
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