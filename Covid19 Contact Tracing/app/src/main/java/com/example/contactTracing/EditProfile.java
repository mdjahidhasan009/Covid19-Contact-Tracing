package com.example.contactTracing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditProfile extends AppCompatActivity {
    Vibrator x;
    TextView name,email,age,blood,loc,contact,contact1;
    private ProgressDialog pDialog;
    String uid;
    Button upd;
    ImageView imdp;
    public String a1,b1,c1,d1,blood11,Token;
    FirebaseFirestore noti;
    String value,value0;
    private ImageView ix;
    private Button chooser,no,yes;
    int TAKE_IMAGE_CODE = 1501;
    Dialog dialog2;
    FirebaseUser user;
    EditText fname, lname;
    Button updater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar a = getSupportActionBar();
        a.hide();

        pDialog = new ProgressDialog(EditProfile.this);
        pDialog.setTitle("Updating");
        pDialog.setMessage("Please wait");

        x = (Vibrator)this.getSystemService( Context.VIBRATOR_SERVICE);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();
        noti = FirebaseFirestore.getInstance();
        imdp = findViewById(R.id.dp001);
        fname = findViewById(R.id.age2);
        lname = findViewById(R.id.age3);
        updater = findViewById(R.id.upd2);




        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbRefFirstTimeCheck = databaseReference.child("User").child(uid);
        dbRefFirstTimeCheck.keepSynced(true);
        dbRefFirstTimeCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    value = dataSnapshot.child("fname").getValue(String.class);
                    value0 = dataSnapshot.child("lname").getValue(String.class);
                    fname.setText(value);
                    lname.setText(value0);
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(imdp);
        }







        updater.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String a1 = fname.getText().toString();
                String c1 = lname.getText().toString();

                pDialog.show();

                if(c1.equals( "" )  || a1.equals( "" )){
                    c1 = name.getText().toString();
                }

                Log.e("Info", value + " " + value0);

                String finalC = c1;
                FirebaseDatabase.getInstance().getReference("User")
                        .child(uid).child("lname")
                        .setValue(c1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pDialog.show();
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("fname").setValue(a1);
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(a1 + " " + finalC).build();

                            user.updateProfile(profileUpdates);

                            Toast.makeText(EditProfile.this,"Values updated!",Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Intent i = new Intent(EditProfile.this, Dashboard.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            finish();

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(EditProfile.this,"Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } );


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
                        imdp.setImageBitmap(bitmap);
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
        startActivity(new Intent(EditProfile.this, Dashboard.class));
    }
}