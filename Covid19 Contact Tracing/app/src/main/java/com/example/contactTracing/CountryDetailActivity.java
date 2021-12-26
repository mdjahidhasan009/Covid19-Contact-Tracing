package com.example.contactTracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


public class CountryDetailActivity extends AppCompatActivity {

    private  int positionCountry;
    TextView tvCountry,tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths, dcon;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        positionCountry = intent.getIntExtra("position",0);

        dcon = findViewById(R.id.dt);
        dcon.setText("Details of "+AffectedCountries.countryModelsList.get(positionCountry).getCountry());


        pieChart = findViewById(R.id.piechart);
        tvCountry = findViewById(R.id.tvCountry);
        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);

        tvCountry.setText(AffectedCountries.countryModelsList.get(positionCountry).getCountry());
        tvCases.setText(AffectedCountries.countryModelsList.get(positionCountry).getCases());
        tvRecovered.setText(AffectedCountries.countryModelsList.get(positionCountry).getRecovered());
        tvCritical.setText(AffectedCountries.countryModelsList.get(positionCountry).getCritical());
        tvActive.setText(AffectedCountries.countryModelsList.get(positionCountry).getActive());
        tvTodayCases.setText(AffectedCountries.countryModelsList.get(positionCountry).getTodayCases());
        tvTotalDeaths.setText(AffectedCountries.countryModelsList.get(positionCountry).getDeaths());
        tvTodayDeaths.setText(AffectedCountries.countryModelsList.get(positionCountry).getTodayDeaths());

        pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
        pieChart.addPieSlice(new PieModel("Recoverd",Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
        pieChart.startAnimation();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
        startActivity(new Intent(getApplicationContext(), AffectedCountries.class));
        this.finish();
        System.exit(0);

    }
}