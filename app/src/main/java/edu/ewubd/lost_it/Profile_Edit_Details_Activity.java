package edu.ewubd.lost_it;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Profile_Edit_Details_Activity extends AppCompatActivity {

    private TextView peUserName, peEmail, btnCancel, btnSave;
    private EditText peName, peSurname, peDob, peInstitute, pePhoneNum, peCurrentLocation, peHomeAddress,
            peFbLink, peInstaLink, peTwitterLink, peWAppNum, peUserImageUrl;
    private Toolbar toolbar;
    FirebaseAuth fAuth;
    DatabaseReference df;
    private ProgressBar edit_progress_bar;
    String NAME, SURNAME, DOB, INSTITUTE, EMAIL, PHONE, CURRENT_ADDRESS, HOME_ADDRESS,
            FB_LINK, INSTA_LINK, TWITTER_LINK, WHATS_APP, USER_IMAGE_URL;
    String value_of_key = "";
    private ImageView Imv_logo;

    private static final int PICK_IMAGE_REQUEST_PROFILE = 1;
    private TextView upload_profile_image;
    private TextView post;
    private TextView tv_post_page_user_name, button_upload_post_image;
    private ImageView edit_profile_details_img_upload;
    private ProgressBar upload_ProgressBar;
    private Uri upload_ImageUri;
    private StorageReference photo_Storage;
    private DatabaseReference photo_Database;
    private DatabaseReference users_Database;
    private StorageTask photo_Upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit_profile_details_img_upload = findViewById(R.id.edit_profile_details_img_upload);
        tv_post_page_user_name = findViewById(R.id.tv_post_page_user_name);
        button_upload_post_image = findViewById(R.id.button_upload_post_image);
        upload_ProgressBar = findViewById(R.id.progress_bar);
        Imv_logo = findViewById(R.id.Imv_logo);
        edit_progress_bar = findViewById(R.id.edit_progress_bar);

        Imv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_Edit_Details_Activity.this, All_Home_Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        df = FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        df.orderByChild("email")
                .equalTo(fAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    value_of_key = childSnapshot.getKey();
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

        peUserName = findViewById(R.id.pe_username);
        peName = findViewById(R.id.pe_name);
        peSurname = findViewById(R.id.pe_surname);
        peDob = findViewById(R.id.pe_dob);
        peInstitute = findViewById(R.id.pe_institute);
        peEmail = findViewById(R.id.pe_email);
        pePhoneNum = findViewById(R.id.pe_phone);
        peCurrentLocation = findViewById(R.id.pe_currentLocation);
        peHomeAddress = findViewById(R.id.pe_home);
        peFbLink = findViewById(R.id.pe_fbLink);
        peInstaLink = findViewById(R.id.pe_instaLink);
        peTwitterLink = findViewById(R.id.pe_twitterLink);
        peWAppNum = findViewById(R.id.pe_wAppNum);

        btnCancel = findViewById(R.id.pe_cancelBtn);
        btnSave = findViewById(R.id.pe_saveBtn);

        showUserProfileDetails();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean s_upload_profile_img_now = upload_profile_img_now();
                boolean nameChanged = isNameChanged();
                boolean surnameChanged = isSurnameChanged();
                boolean dOBisChanged = isDOBisChanged();
                boolean  instituteChanged =  isInstituteChanged();
                boolean phoneNumChanged = isPhoneNumChanged();
                boolean currentLocationChanged = isCurrentLocationChanged();
                boolean homeAddressChanged = isHomeAddressChanged();
                boolean fbLinkChanged = isFbLinkChanged();
                boolean instaLinkChanged= isInstaLinkChanged();
                boolean twitterLinkChanged = isTwitterLinkChanged();
                boolean whatsAppLinkChanged = isWhatsAppLinkChanged();


                boolean a = isNameChanged();

                if(nameChanged || surnameChanged || dOBisChanged ||
                        instituteChanged || phoneNumChanged || currentLocationChanged ||
                        homeAddressChanged || fbLinkChanged || instaLinkChanged ||
                        twitterLinkChanged || whatsAppLinkChanged || s_upload_profile_img_now){
                    Toast.makeText(Profile_Edit_Details_Activity.this, "User Profile updated", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(Profile_Edit_Details_Activity.this, "No data Changed", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Profile_Edit_Details_Activity.this, Profile_Details_Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_PROFILE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            upload_ImageUri = data.getData();
            Picasso.with(this).load(upload_ImageUri).into(edit_profile_details_img_upload);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private boolean upload_profile_img_now() {

        if (upload_ImageUri != null) {

            photo_Storage = FirebaseStorage.getInstance().getReference("userphoto");
            StorageReference fileReference = photo_Storage.child(System.currentTimeMillis()
                    + "." + getFileExtension(upload_ImageUri));
            fileReference.putFile(upload_ImageUri)
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
                                            edit_progress_bar.setProgress(0);
                                        }
                                    }, 500);
                                    df.child(value_of_key).child("userImageUrl").setValue(uri.toString());

                                    Toast.makeText(Profile_Edit_Details_Activity.this, "Profile Picture Uploaded", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile_Edit_Details_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    edit_progress_bar.setProgress((int) progress);

                }
            });
            return true;
        }
        else {
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void showUserProfileDetails() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "");
        df.orderByChild("email").equalTo(user_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value_key = "";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    value_key = childSnapshot.getKey();
                }
                if (snapshot.exists()) {
                    NAME = snapshot.child(value_key).child("name").getValue(String.class);
                    SURNAME = snapshot.child(value_key).child("surname").getValue(String.class);
                    DOB = snapshot.child(value_key).child("dob").getValue(String.class);
                    INSTITUTE = snapshot.child(value_key).child("institute").getValue(String.class);
                    EMAIL = snapshot.child(value_key).child("email").getValue(String.class);
                    PHONE = snapshot.child(value_key).child("phone").getValue(String.class);
                    CURRENT_ADDRESS = snapshot.child(value_key).child("currentAddress").getValue(String.class);
                    HOME_ADDRESS = snapshot.child(value_key).child("homeAddress").getValue(String.class);
                    FB_LINK = snapshot.child(value_key).child("fb").getValue(String.class);
                    INSTA_LINK = snapshot.child(value_key).child("insta").getValue(String.class);
                    TWITTER_LINK = snapshot.child(value_key).child("twitter").getValue(String.class);
                    WHATS_APP = snapshot.child(value_key).child("whatsApp").getValue(String.class);
                    USER_IMAGE_URL = snapshot.child(value_key).child("userImageUrl").getValue(String.class);

                    peName.setText(NAME);
                    peSurname.setText(SURNAME);
                    peDob.setText(DOB);
                    peInstitute.setText(INSTITUTE);
                    peEmail.setText(EMAIL);
                    pePhoneNum.setText(PHONE);
                    peCurrentLocation.setText(CURRENT_ADDRESS);
                    peHomeAddress.setText(HOME_ADDRESS);
                    peFbLink.setText(FB_LINK);
                    peInstaLink.setText(INSTA_LINK);
                    peTwitterLink.setText(TWITTER_LINK);
                    peWAppNum.setText(WHATS_APP);

                    Picasso.with(Profile_Edit_Details_Activity.this)
                            .load(USER_IMAGE_URL)
                            .fit()
                            .centerCrop()
                            .into(edit_profile_details_img_upload);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean isNameChanged() {
        if (!NAME.equals(peName.getText().toString().trim())) {
            String text_name = peName.getText().toString().trim();
            if (text_name.length() == 0) {
                peName.setError("Name field is required");
                return false;
            }
            if (text_name.length() < 3 ||  text_name.length() > 40) {
                peName.setError("Name length should be in between 3 to 40 character");
                return false;
            }
            df.child(value_of_key).child("name").setValue(peName.getText().toString().trim());
            return true;
        }else{
            return false;
        }
    }

    private boolean isSurnameChanged() {
        if(!SURNAME.equals(peSurname.getText().toString().trim())){
            String text_SURNAME = peSurname.getText().toString().trim();
            if(text_SURNAME.length() == 0){
                peName.setError("Surname field is required");
                return false;
            }
            if(text_SURNAME.length() <= 2 ||  text_SURNAME.length() > 10){
                peName.setError("Surname length should be in between 2 to 10");
                return false;
            }
            df.child(value_of_key).child("surname").setValue(peSurname.getText().toString().trim());
            return true;
        }else
            return false;
    }

    private boolean isDOBisChanged(){
        if(!DOB.equals(peDob.getText().toString().trim())){
            df.child(value_of_key).child("dob").setValue(peDob.getText().toString().trim());
            DOB = peDob.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isInstituteChanged(){
        if(!INSTITUTE.equals(peInstitute.getText().toString().trim())){
            df.child(value_of_key).child("institute").setValue(peInstitute.getText().toString().trim());
            INSTITUTE = peInstitute.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isPhoneNumChanged(){
        if(!PHONE.equals(pePhoneNum.getText().toString().trim())){
            df.child(value_of_key).child("phone").setValue(pePhoneNum.getText().toString().trim());
            PHONE = pePhoneNum.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isCurrentLocationChanged(){
        if(!CURRENT_ADDRESS.equals(peCurrentLocation.getText().toString().trim())){
            df.child(value_of_key).child("currentAddress").setValue(peCurrentLocation.getText().toString().trim());
            CURRENT_ADDRESS = peCurrentLocation.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isHomeAddressChanged(){
        if(!HOME_ADDRESS.equals(peHomeAddress.getText().toString().trim())){
            df.child(value_of_key).child("homeAddress").setValue(peHomeAddress.getText().toString().trim());
            HOME_ADDRESS = peHomeAddress.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isFbLinkChanged(){
        if(!FB_LINK.equals(peFbLink.getText().toString().trim())){
            df.child(value_of_key).child("fb").setValue(peFbLink.getText().toString().trim());
            FB_LINK = peFbLink.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isInstaLinkChanged(){
        if(!INSTA_LINK.equals(peInstaLink.getText().toString().trim())){
            df.child(value_of_key).child("insta").setValue(peInstaLink.getText().toString().trim());
            INSTA_LINK = peInstaLink.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isTwitterLinkChanged(){
        if(!TWITTER_LINK.equals(peTwitterLink.getText().toString().trim())){
            df.child(value_of_key).child("twitter").setValue(peTwitterLink.getText().toString().trim());
            PHONE = peTwitterLink.getText().toString().trim();
            return true;
        }else
            return false;
    }

    private boolean isWhatsAppLinkChanged(){
        if(!WHATS_APP.equals(peWAppNum.getText().toString().trim())){
            df.child(value_of_key).child("whatsApp").setValue(peWAppNum.getText().toString().trim());
            WHATS_APP = peWAppNum.getText().toString().trim();
            return true;
        }else
            return false;
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
