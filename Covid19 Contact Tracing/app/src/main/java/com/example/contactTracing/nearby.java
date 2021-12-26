package com.example.contactTracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.contactTracing.model.DiffHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.igenius.customcheckbox.CustomCheckBox;

public class nearby extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        DatabaseReference nref = FirebaseDatabase.getInstance().getReference("diffdate")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DiffHelper os = snapshot.getValue(DiffHelper.class);
                String numb = os.getDiff();
                int x = Integer.parseInt(numb);
                if(x<14){
                    LinearLayout l = findViewById(R.id.lpx);
                    l.setVisibility(View.VISIBLE);

                    LinearLayout l2 = findViewById(R.id.lp2);
                    l2.setVisibility(View.GONE);
                    String id = "l" + x;
                    int resID = getResources().getIdentifier(id, "id", getPackageName());
                    TextView t = findViewById(resID);
                    t.setVisibility(View.VISIBLE);
                }else{
                    LinearLayout l = findViewById(R.id.lpx);
                    l.setVisibility(View.GONE);
                    LinearLayout l2 = findViewById(R.id.lp2);
                    l2.setVisibility(View.VISIBLE);
                }
                for(int i = 1; i<= x; i++){
                    if(i<15) {
                        String id = "i" + i;
                        int resID = getResources().getIdentifier(id, "id", getPackageName());
                        CustomCheckBox osx = (CustomCheckBox) findViewById(resID);
                        osx.setChecked(true, true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
        this.finish();
        System.exit(0);

    }
}