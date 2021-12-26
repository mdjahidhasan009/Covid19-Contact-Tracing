package com.example.contactTracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText mail,pwd;
    private Button login;
    private TextView reset, reg,resend;
    FirebaseAuth mFirebaseAuth;
    private ProgressDialog pDialog;
    String maill,pinn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage("Authenticating");

        mail = findViewById(R.id.mail);
        pwd = findViewById(R.id.pin);
        login = findViewById(R.id.btn_login);

        reset = findViewById(R.id.link_reset);
        reg = findViewById(R.id.link_signup);
        resend = findViewById(R.id.lesent);

        FirebaseAuth.getInstance().signOut();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToHome = new Intent(Login.this, ResetPassword.class);
                intToHome.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intToHome);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maill = mail.getText().toString().trim();
                pinn = pwd.getText().toString().trim();

                if(maill.isEmpty()){
                    mail.setError("Please enter email id");
                    mail.requestFocus();
                }
                else  if(pinn.isEmpty()){
                    pwd.setError("Please enter your pin");
                    pwd.requestFocus();
                }
                else  if(maill.isEmpty() && pinn.isEmpty()){
                    Toast.makeText(Login.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(maill.isEmpty() && pinn.isEmpty())){
                    pDialog.show();
                    mFirebaseAuth.signInWithEmailAndPassword(maill, pinn).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                pDialog.dismiss();
                                Toast.makeText(Login.this,"Authentication error",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                boolean emailVerified = user.isEmailVerified();
                                if(emailVerified){
                                    Intent intToHome = new Intent(Login.this, Dashboard.class);
                                    intToHome.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intToHome);
                                    finish();
                                }
                                else if(!emailVerified)
                                {
                                    pDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    resend.setVisibility(View.VISIBLE);
                                    Toast.makeText(Login.this,"E - mail is not vatified",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                else{

                    pDialog.dismiss();
                    Toast.makeText(Login.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                        Toast.makeText(Login.this, "Email re-sent successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Login.this, "Your email has been verified! You can Login now.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);

    }
}