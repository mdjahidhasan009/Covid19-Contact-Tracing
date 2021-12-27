package com.example.contactTracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.example.contactTracing.model.DateHelper;
import com.example.contactTracing.model.Guard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    TextView tvName;
    ImageView ivDisplayPicture;
    ImageView ivEdit, ivRefresh;
    FloatingActionButton fabLogout;
    ProgressDialog progressDialog, progressDialog2;
    TextView tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths,
            tvAffectedCountries, tvWarnMessage;
    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;
    Switch swIsolationMode, swCovidGuard;
    ImageView ivIsolationDayCount;
    ImageView ivPositivePossibility;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        //All initialization
        initUI();
        addAllClickEventListener();
        fetchData();  //For showing the covid situation of the world

        DatabaseReference myrefs = FirebaseDatabase.getInstance().getReference("use")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myrefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myrefs.keepSynced(true);
                if(snapshot.exists()){
                    Guard os = snapshot.getValue(Guard.class);
                    String status = os.getStatus();
                    if(status.equals("on")){
                        swCovidGuard.setChecked(true);
                        startService(new Intent(getApplicationContext(), TraceService.class));
                    } else if(status.equals("off")){
                        swCovidGuard.setChecked(false);
                        stopService(new Intent(getApplicationContext(), TraceService.class));
                    } else{
                        FirebaseDatabase.getInstance().getReference("use")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("status").setValue("off");
                        stopService(new Intent(getApplicationContext(), TraceService.class));
                    }
                } else if(!snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("use")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("off");
                    stopService(new Intent(getApplicationContext(), TraceService.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Guard")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference nref = FirebaseDatabase.getInstance().getReference("date")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myref.keepSynced(true);
                if(snapshot.exists()){
                    Guard os = snapshot.getValue(Guard.class);
                    String status = os.getStatus();
                    if(status.equals("on")){
                        swIsolationMode.setChecked(true);
                        nref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    DateHelper d = snapshot.getValue(DateHelper.class);
                                    String dateStr = d.getDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        Date date1 = sdf.parse(dateStr);
                                        Date date2 = sdf.parse(date);
                                        long difference = Math.abs(date2.getTime() - date1.getTime());
                                        long differenceDates = (difference / (24 * 60 * 60 * 1000))+1;
                                        String dayDifference = Long.toString(differenceDates);
                                        FirebaseDatabase.getInstance().getReference("diffdate")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("diff").setValue(dayDifference);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ivIsolationDayCount.setVisibility(View.VISIBLE);
                        tvWarnMessage.setVisibility(View.GONE);
                    } else if(status.equals("off")){
                        swIsolationMode.setChecked(false);
                        ivIsolationDayCount.setVisibility(View.GONE);
                        tvWarnMessage.setVisibility(View.VISIBLE);
                    } else{
                        FirebaseDatabase.getInstance().getReference("Guard")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("status").setValue("off");
                        ivIsolationDayCount.setVisibility(View.GONE);
                        tvWarnMessage.setVisibility(View.VISIBLE);
                    }
                } else if(!snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("Guard")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("off");
                    ivIsolationDayCount.setVisibility(View.GONE);
                    tvWarnMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String mAuth = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            tvName.setText(mAuth);
            if (user.getPhotoUrl() != null) {
                Glide.with(Dashboard.this)
                        .load(user.getPhotoUrl())
                        .into(ivDisplayPicture);
            }
        }
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Dashboard.this);
        progressDialog.setTitle("Running");
        progressDialog.setMessage("Signing out");

        progressDialog2 = new ProgressDialog(Dashboard.this);
        progressDialog2.setTitle("Please wait");
        progressDialog2.setMessage("Refreshing");

        tvName = findViewById(R.id.name);
        ivDisplayPicture = findViewById(R.id.img);
        fabLogout = findViewById(R.id.fab);
        ivRefresh = findViewById(R.id.imu);
        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvAffectedCountries = findViewById(R.id.tvAffectedCountries);

        simpleArcLoader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);
        swIsolationMode = findViewById(R.id.guard);
        swCovidGuard = findViewById(R.id.crud);
        ivIsolationDayCount = findViewById(R.id.navigate);
        tvWarnMessage = findViewById(R.id.warnMessage);

        ivPositivePossibility = findViewById(R.id.cont);
        ivEdit = findViewById(R.id.rdit);
    }

    private void addAllClickEventListener() {
        ivPositivePossibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, TraceActivity.class));
            }
        });

        ivIsolationDayCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, nearby.class));
            }
        });

        swCovidGuard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swCovidGuard.isChecked()){
                    FirebaseDatabase.getInstance().getReference("use")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("on");
                    startService(new Intent(getApplicationContext(), TraceService.class));
                }
                else {
                    FirebaseDatabase.getInstance().getReference("use")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("off");
                    stopService(new Intent(getApplicationContext(), TraceService.class));
                }
            }
        });

        swIsolationMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swIsolationMode.isChecked()){
                    FirebaseDatabase.getInstance().getReference("Guard")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("on");
                    FirebaseDatabase.getInstance().getReference("date")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("date").setValue(date);
                    FirebaseDatabase.getInstance().getReference("diffdate")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("diff").setValue("1");
                    ivIsolationDayCount.setVisibility(View.VISIBLE);
                    tvWarnMessage.setVisibility(View.GONE);
                }
                else {
                    FirebaseDatabase.getInstance().getReference("Guard")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("status").setValue("off");
                    FirebaseDatabase.getInstance().getReference("date")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference("diffdate")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                    ivIsolationDayCount.setVisibility(View.GONE);
                    tvWarnMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog2.show();
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String mAuth = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    tvName.setText(mAuth);
                    if (user.getPhotoUrl() != null) {
                        Glide.with(Dashboard.this)
                                .load(user.getPhotoUrl()).into(ivDisplayPicture);
                    }
                }
                progressDialog2.dismiss();
            }
        });
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Dashboard.this, Login.class));
                progressDialog.dismiss();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dashboard.this.finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(Dashboard.this, EditProfile.class));
            }
        });
    }

    private void fetchData() {
        String url  = "https://corona.lmao.ninja/v2/all/";

        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());

                    tvCases.setText(jsonObject.getString("cases"));
                    tvRecovered.setText(jsonObject.getString("recovered"));
                    tvCritical.setText(jsonObject.getString("critical"));
                    tvActive.setText(jsonObject.getString("active"));
                    tvTodayCases.setText(jsonObject.getString("todayCases"));
                    tvTotalDeaths.setText(jsonObject.getString("deaths"));
                    tvTodayDeaths.setText(jsonObject.getString("todayDeaths"));
                    tvAffectedCountries.setText(jsonObject.getString("affectedCountries"));

                    pieChart.addPieSlice(new PieModel("Cases",
                            Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
                    pieChart.addPieSlice(new PieModel("Recoverd",
                            Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                    pieChart.addPieSlice(new PieModel("Deaths",
                            Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                    pieChart.addPieSlice(new PieModel("Active",
                            Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
                    pieChart.startAnimation();

                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleArcLoader.stop();
                simpleArcLoader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(Dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
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
        Dashboard.this.finish();
        System.exit(0);

    }
    public void goTrackCountries(View view) {
        startActivity(new Intent(getApplicationContext(),AffectedCountries.class));
    }
}