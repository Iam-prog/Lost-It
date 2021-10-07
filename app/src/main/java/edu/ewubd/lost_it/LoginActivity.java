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
import com.google.firebase.database.annotations.NotNull;


public class LoginActivity extends Activity {

    private EditText  emailTF, passwordTF;
    TextView LogIn;
    private CheckBox rememberEmail, rememberLogin;
    private FirebaseAuth fAuth;
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
        String remember = sharedPreferences.getString("remember", "");

        String user_Email = "";
         if(remember.equals("user_Email")){
            user_Email= sharedPreferences.getString("user_email", "");
        }

        setContentView(R.layout.activity_login);

        emailTF = findViewById(R.id.et_email_l);
        emailTF.setText(user_Email);
        passwordTF = findViewById(R.id.et_Password_l);
        LogIn = findViewById(R.id.btn_Login_l);
        rememberLogin = findViewById(R.id.cb_Login_l);
        rememberEmail = findViewById(R.id.cb_email_l);

        fAuth = FirebaseAuth.getInstance();
        //If "remember" have "user_email" then make "Remember Email" checkbox true.
        if(remember.equals("user_Email")){
            rememberEmail.setChecked(true);
        }

        // Doesn't Check both Checkboxes.
        rememberLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberEmail.setChecked(false);
            }
        });
        rememberEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberLogin.setChecked(false);
            }
        });

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTF.getText().toString().trim();
                String password = passwordTF.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailTF.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordTF.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    passwordTF.setError("Password must be >= 6 Digits");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                            String email = emailTF.getText().toString().trim();
                            String password = passwordTF.getText().toString().trim();

                            prefsEditor.putString("user_email", email);
                            prefsEditor.putString("user_password", password);

                            if(rememberLogin.isChecked()){
                                prefsEditor.putString("remember", "login");
                            }else if(rememberEmail.isChecked()){
                                prefsEditor.putString("remember", "user_Email");
                            }else{
                                prefsEditor.putString("remember", null);
                            }
                            prefsEditor.commit();
                            startActivity(new Intent(getApplicationContext(), All_Home_Activity.class));
                            finishAffinity();
                        }else {
                            Toast.makeText(LoginActivity.this, "Error -> " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}