package edu.ewubd.lost_it;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import edu.ewubd.lost_it.R;


public class Post_Page_Activity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView post;
    private TextView tv_post_page_user_name;
    private Button button_upload_post_image;
    private EditText post_details;
    private ImageView post_image_View;
    private  ImageView Imv_back_logo;
    private ImageView Imv_post_page_user_img;
    private ProgressBar post_ProgressBar;
    private Uri post_ImageUri;
    private StorageReference post_Storage;
    private DatabaseReference post_Database;
    private DatabaseReference users_Database;
    private StorageTask post_Upload_task;

    private long count_post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page_view);

        Imv_post_page_user_img = findViewById(R.id.Imv_post_page_user_img);
        post = findViewById(R.id.tv_post);
        tv_post_page_user_name = findViewById(R.id.tv_post_page_user_name);
        button_upload_post_image = findViewById(R.id.button_upload_post_image);
        post_details = findViewById(R.id.et_post_details);
        post_image_View = findViewById(R.id.Imv_post_img);
        post_ProgressBar = findViewById(R.id.progress_bar);
        Imv_back_logo = findViewById(R.id.Imv_back_logo);

        post_Storage = FirebaseStorage.getInstance().getReference("post");
        post_Database = FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Post");
        FirebaseAuth fAuth;
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
                    String name = snapshot.child(value_of_key).child("name").getValue(String.class);
                    String email = snapshot.child(value_of_key).child("email").getValue(String.class);
                    String surname = snapshot.child(value_of_key).child("surname").getValue(String.class);
                    String USER_IMAGE_URL = snapshot.child(value_of_key).child("userImageUrl").getValue(String.class);

                    tv_post_page_user_name.setText(name);
                    Picasso.with(Post_Page_Activity.this)
                            .load(USER_IMAGE_URL)
                            .fit()
                            .centerCrop()
                            .into(Imv_post_page_user_img);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        button_upload_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_Upload_task != null && post_Upload_task.isInProgress()){
                    Toast.makeText(Post_Page_Activity.this, "Upload is in progress", Toast.LENGTH_SHORT).show();
                }else {
                    post_now();
                }
            }
        });

        Imv_back_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Imv_post_page_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Post_Page_Activity.this, Profile_Activity.class);
                startActivity(i);

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            post_ImageUri = data.getData();
            Picasso.with(this).load(post_ImageUri).into(post_image_View);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void post_now() {

        post_Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    count_post = snapshot.getChildrenCount();
                }else {
                    count_post = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        List<UserHelperClass> userlist;
        userlist = new ArrayList<>();
        SharedPreferences.Editor prefsEditor;
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "");

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (post_ImageUri != null && (post_details.getText().toString().trim()).length() != 0 ) {
            StorageReference fileReference = post_Storage.child(System.currentTimeMillis()
                    + "." + getFileExtension(post_ImageUri));
            post_Upload_task = fileReference.putFile(post_ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            post_ProgressBar.setProgress(0);
                                        }
                                    }, 500);
                                    PostClass p = new PostClass(count_post,user_email, date, post_details.getText().toString().trim(),
                                            uri.toString());
                                    String postId = post_Database.push().getKey();
                                    post_Database.child(postId).setValue(p);
                                    Toast.makeText(Post_Page_Activity.this, "Posted", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Post_Page_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            post_ProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            if(post_ImageUri == null) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
            else if((post_details.getText().toString().trim()).length() == 0) {
                Toast.makeText(Post_Page_Activity.this, "Description Missing", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Post_Page_Activity.this, "Nothing to Post.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}