package com.example.contactTracing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ChooseProfilePicture extends AppCompatActivity {
    private ImageView ix;
    private Button chooser,no,yes;
    int TAKE_IMAGE_CODE = 1501;
    String uid;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dip);

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        pDialog = new ProgressDialog(ChooseProfilePicture.this);
        pDialog.setCancelable(false);
        pDialog.setTitle("Uploading Display Picture");
        pDialog.setMessage("Please wait");

        ix = findViewById(R.id.image);
        chooser = findViewById(R.id.imageC);
        no = findViewById(R.id.skip);
        yes = findViewById(R.id.done);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("UID", uid);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToHome = new Intent(ChooseProfilePicture.this, Login.class);
                intToHome.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intToHome);
                finish();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToHome = new Intent(ChooseProfilePicture.this, Login.class);
                intToHome.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intToHome);
                finish();
            }
        });
    }
    public void choice(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                        ix.setImageBitmap(bitmap);
                        handleUpload(bitmap);
                    }
                }
            });

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("DP")
                .child(uid + ".jpeg");
        ref.putBytes(b.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pDialog.show();
                        getDownURl(ref);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void getDownURl(StorageReference ref) {
        ref.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d( "onSuccess: ", String.valueOf(uri));
                        setUserUrl(uri);
                    }
                });
    }

    private void setUserUrl(Uri uri) {
        FirebaseUser f = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();


        f.updateProfile(req)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                        no.setVisibility(View.GONE);
                        yes.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
        Intent i = new Intent(ChooseProfilePicture.this, Register.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
}