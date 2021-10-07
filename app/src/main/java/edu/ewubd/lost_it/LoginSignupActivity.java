package edu.ewubd.lost_it;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginSignupActivity extends Activity {

    private TextView LogIn, SignUp;
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
        String remember = sharedPreferences.getString("remember", "");

        String user_Email = "";
        if(remember.equals("login")){
            fAuth = FirebaseAuth.getInstance();
            if(fAuth.getCurrentUser() != null){
                Intent i = new Intent(LoginSignupActivity.this, All_Home_Activity.class);
                System.out.println("Test "+fAuth.getCurrentUser().getEmail());
                startActivity(i);
                finishAffinity();
            }
        }else if (remember.equals("user_Email")){
            Intent i = new Intent(LoginSignupActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_login_signup);

        LogIn = findViewById(R.id.btn_Login_h);
        SignUp = findViewById(R.id.btn_Signup_h);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginSignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginSignupActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }
}