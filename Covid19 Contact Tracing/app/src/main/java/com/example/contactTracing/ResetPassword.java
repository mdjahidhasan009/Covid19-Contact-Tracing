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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText email;
    private Button x;
    String xm;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        email = findViewById(R.id.mail);
        x = findViewById(R.id.btnx);
        pDialog = new ProgressDialog(ResetPassword.this);
        pDialog.setMessage("Please wait");

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                xm = email.getText().toString();

                if(xm.equals("")){
                    email.setError("Please enter your email");
                    email.requestFocus();
                    pDialog.dismiss();
                }
                else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(xm)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pDialog.dismiss();
                                        Toast.makeText(ResetPassword.this, "Password reset link sent successfully to " + xm, Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ResetPassword.this, Login.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        pDialog.dismiss();
                                        Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        Intent i = new Intent(ResetPassword.this, Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
}