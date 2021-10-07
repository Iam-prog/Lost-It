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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Profile_Activity extends AppCompatActivity implements PostAdapter.OnItemClickListener {

    private RecyclerView rv_profile_all_post;
    private PostAdapter post_adapter;
    private TextView tv_profile_post_bar;
    private TextView tv_profile_email;
    private TextView tv_profile_user_name;
    private TextView tv_profile_details;
    private TextView tv_profile_activity;
    private TextView tv_profile_job_or_studies;
    private TextView tv_profile_current_town_city;
    private ImageView Imv_profile_user_img;
    private ImageView iv_profile_image_view;
    private Toolbar toolbar;
    private ImageView Imv_logo;
    private FirebaseAuth fAuth;

    private FirebaseStorage all_storage;

    private ValueEventListener post_value_lisener;

    private ProgressBar post_progress_Circle;
    private DatabaseReference databaseReference;
    private List<PostClass> post_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_view);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_profile_email = findViewById(R.id.tv_profile_email);
        iv_profile_image_view = findViewById(R.id.iv_profile_image_view);
        tv_profile_user_name = findViewById(R.id.tv_profile_user_name);
        tv_profile_details = findViewById(R.id.tv_profile_details);
        tv_profile_activity = findViewById(R.id.tv_profile_activity);
        tv_profile_current_town_city = findViewById(R.id.tv_profile_current_town_city);
        tv_profile_job_or_studies = findViewById(R.id.tv_profile_job_or_studies);
        Imv_profile_user_img = findViewById(R.id.Imv_profile_user_img);
        Imv_logo = findViewById(R.id.Imv_logo);

        Imv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Activity.this, All_Home_Activity.class);
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
                    String INSTITUTE = snapshot.child(value_of_key).child("institute").getValue(String.class);
                    String CURRENT_ADDRESS = snapshot.child(value_of_key).child("currentAddress").getValue(String.class);
                    String USER_IMAGE_URL = snapshot.child(value_of_key).child("userImageUrl").getValue(String.class);

                    tv_profile_email.setText(email);
                    tv_profile_current_town_city.setText(CURRENT_ADDRESS);
                    tv_profile_job_or_studies.setText(INSTITUTE);
                    tv_profile_user_name.setText(name + " ( " + surname + " ) ");

                    Picasso.with(Profile_Activity.this)
                            .load(USER_IMAGE_URL)
                            .fit()
                            .centerCrop()
                            .into(iv_profile_image_view);
                    Picasso.with(Profile_Activity.this)
                            .load(USER_IMAGE_URL)
                            .fit()
                            .centerCrop()
                            .into(Imv_profile_user_img);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        rv_profile_all_post = findViewById(R.id.rv_profile_all_post);
        rv_profile_all_post.setHasFixedSize(true);
        rv_profile_all_post.setLayoutManager(new LinearLayoutManager(this));

        tv_profile_post_bar = findViewById(R.id.tv_profile_post_bar);


        tv_profile_post_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Activity.this, Post_Page_Activity.class);
                startActivity(i);
            }
        });

        tv_profile_details .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Activity.this, Profile_Details_Activity.class);
                startActivity(i);
            }
        });

        tv_profile_activity .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Profile_Activity.this, Police_Activity.class);
//                startActivity(i);
            }
        });


        post_progress_Circle = findViewById(R.id.profile_progress_circle);
        post_class = new ArrayList<>();
        post_adapter = new PostAdapter(Profile_Activity.this, post_class);
        rv_profile_all_post.setAdapter(post_adapter);

        post_adapter.setOnItemClickListener(Profile_Activity.this);

        all_storage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");


        post_value_lisener =  FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Post").
                orderByChild("user_Email")
                .equalTo(user_email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                post_class.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PostClass post= postSnapshot.getValue(PostClass.class);

                    post.setPost_key(postSnapshot.getKey());

                    post_class.add(post);
                }

                post_adapter.notifyDataSetChanged();
                post_progress_Circle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                post_progress_Circle.setVisibility(View.INVISIBLE);
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





    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

        PostClass select_item = post_class.get(position);
        String  selected_key = select_item.getPost_key();

        StorageReference ima_ref = all_storage.getReferenceFromUrl(select_item.getPost_imageUrl());
        ima_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference.child(selected_key).removeValue();
                Toast.makeText(Profile_Activity.this, "Post Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(post_value_lisener);
    }

}