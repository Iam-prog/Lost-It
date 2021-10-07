package edu.ewubd.lost_it;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;


public class SignUpActivity extends Activity {

    private EditText nameTF, surnameTF, emailTF, passwordTF, confirmPasswordTF;
    private TextView signUp;
    private CheckBox cbLogin, cbEmail;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    DatabaseReference databaseReference;
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseReference = FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        nameTF = findViewById(R.id.etName);
        surnameTF = findViewById(R.id.etSurname);
        emailTF = findViewById(R.id.etEmail);
        passwordTF = findViewById(R.id.etPassword);
        confirmPasswordTF = findViewById(R.id.etConfirmPassword);
        signUp = findViewById(R.id.btnSignUp1);
        cbEmail = findViewById(R.id.cbEmail);
        cbLogin = findViewById(R.id.cbLogin);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        // Doesn't Check both Checkboxes.
        cbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbEmail.setChecked(false);
            }
        });
        cbEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbLogin.setChecked(false);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTF.getText().toString().trim();
                String password = passwordTF.getText().toString().trim();
                String confirmPassword = confirmPasswordTF.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailTF.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    emailTF.setError("Password is required");
                    return;
                }

                if(password.length() < 4){
                    emailTF.setError("Password must be >= 6 Digits");
                    return;
                }

                if(TextUtils.isEmpty(confirmPassword)){
                    emailTF.setError("Password is required");
                }

                if(!password.equals(confirmPassword)){
                    confirmPasswordTF.setError("Password is did not match");
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            saveData();

                            String email = emailTF.getText().toString().trim();
                            String password = passwordTF.getText().toString().trim();

                            prefsEditor.putString("user_email", email);
                            prefsEditor.putString("user_password", password);

                            if(cbLogin.isChecked()){
                                prefsEditor.putString("remember", "login");
                            }else if(cbEmail.isChecked()){
                                prefsEditor.putString("remember", "user_Email");
                            }else{
                                prefsEditor.putString("remember", null);
                            }
                            prefsEditor.commit();

                            Intent i = new Intent(SignUpActivity.this, All_Home_Activity.class);
                            startActivity(i);
                            finishAffinity();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Error -> " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void saveData(){
        String name = nameTF.getText().toString().trim();
        String surname = surnameTF.getText().toString().trim();
        String email = emailTF.getText().toString().trim();
        String password = passwordTF.getText().toString().trim();
        String key = databaseReference.push().getKey();

        String dob = "";
        String institute = "";
        String phone = "";
        String currentAddress = "";
        String homeAddress = "";
        String fb = "";
        String instagram = "";
        String twitter = "";
        String whatsApp = "";
        String userImageUrl = "https://firebasestorage.googleapis.com/v0/b/lost-it-a15e2.appspot.com/o/post%2F1631205941538.png?alt=media&token=653a9e19-2462-4715-8947-c44a2e605b30";

        UserHelperClass user = new UserHelperClass(name, surname, email, password, dob, institute, phone, currentAddress,
                homeAddress, fb, instagram, twitter, whatsApp, userImageUrl);

        databaseReference.child(key).setValue(user);
        Toast.makeText(SignUpActivity.this, "User details added", Toast.LENGTH_SHORT).show();
    }
}