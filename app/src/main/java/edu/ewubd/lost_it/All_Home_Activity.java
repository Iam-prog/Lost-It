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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class All_Home_Activity extends AppCompatActivity implements PostAdapter.OnItemClickListener {

    private RecyclerView rv_home_all_post;
    private PostAdapter post_adapter;
    private TextView tv_post_bar;
    private ImageView Imv_user_img;
    private ProgressBar post_progressCircle;
    private DatabaseReference databaseReference;
    private ValueEventListener post_value_lisener;
    private FirebaseStorage all_storage;

    private List<PostClass> post_class;
    private FirebaseAuth fAuth;
    private Toolbar toolbar;
    private ImageView Imv_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv_home_all_post = findViewById(R.id.rv_home_all_post);
        rv_home_all_post.setHasFixedSize(true);
        rv_home_all_post.setLayoutManager(new LinearLayoutManager(this));
        tv_post_bar = findViewById(R.id.tv_post_bar);
        Imv_user_img = findViewById(R.id.Imv_user_img);
        Imv_logo = findViewById(R.id.Imv_logo);

        Imv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(All_Home_Activity.this, All_Home_Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        tv_post_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(All_Home_Activity.this, Post_Page_Activity.class);
                startActivity(i);
            }
        });

        Imv_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(All_Home_Activity.this, Profile_Activity.class);
                startActivity(i);
            }
        });

        fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").
                orderByChild("email")
                .equalTo(fAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value_of_key = "";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    value_of_key = childSnapshot.getKey();
                }
                if (snapshot.exists()) {
                    String img_url= snapshot.child(value_of_key).child("userImageUrl").getValue(String.class);
                    Picasso.with(All_Home_Activity.this)
                            .load(img_url)
                            .fit()
                            .centerCrop()
                            .into(Imv_user_img);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        post_progressCircle = findViewById(R.id.progress_circle);
        post_class = new ArrayList<>();

        post_adapter = new PostAdapter(All_Home_Activity.this, post_class);

        rv_home_all_post.setAdapter(post_adapter);

        post_adapter.setOnItemClickListener(All_Home_Activity.this);

        all_storage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Post");

        Query post_count_q = databaseReference.orderByChild("post_counter");

        post_value_lisener =  post_count_q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post_class.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PostClass post= postSnapshot.getValue(PostClass.class);
                    post.setPost_key(postSnapshot.getKey());
                    post_class.add(post);
                }
                post_adapter.notifyDataSetChanged();

                post_progressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(All_Home_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                post_progressCircle.setVisibility(View.INVISIBLE);
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
        fAuth = FirebaseAuth.getInstance();
        String user_email_id = fAuth.getCurrentUser().getEmail();

        PostClass select_item = post_class.get(position);
        String  selected_key = select_item.getPost_key();

        StorageReference ima_ref = all_storage.getReferenceFromUrl(select_item.getPost_imageUrl());

        if (user_email_id.equals(select_item.getUser_Email())) {
            ima_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    databaseReference.child(selected_key).removeValue();
                    Toast.makeText(All_Home_Activity.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(All_Home_Activity.this, "You Can Not Delete this Post", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(post_value_lisener);
}
}