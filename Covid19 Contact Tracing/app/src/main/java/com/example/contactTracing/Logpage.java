package com.example.contactTracing;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class Logpage extends AppCompatActivity {

    private Dialog dialog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logpage);

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Initialize ViewPager view
        ViewPager viewPager = findViewById(R.id.viewPagerOnBoarding);
        // create ViewPager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Button rude = findViewById(R.id.next);
        LinearLayout lope = findViewById(R.id.Bar);
        rude.setVisibility(View.GONE);
        // Add All Fragments to ViewPager
        viewPagerAdapter.addFragment(new StepOneFragment());
        viewPagerAdapter.addFragment(new StepTwoFragment());
        viewPagerAdapter.addFragment(new StepThreeFragment());
        viewPagerAdapter.addFragment(new StepFourFragment());

        // Set Adapter for ViewPager
        viewPager.setAdapter(viewPagerAdapter);

        rude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3 = new Dialog(Logpage.this);
                dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog3.setContentView(R.layout.popup4);
                dialog3.show();
                Button x = dialog3.findViewById(R.id.upd2);
                WebView y = dialog3.findViewById(R.id.terms);
                y.getSettings().setJavaScriptEnabled(true);
                y.setWebViewClient(new WebViewController());
                y.loadUrl("http://www.websitepolicies.com/policies/view/UHRV2Rsp");

                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String PREF_NAME = "MyPref";

                        // getting saved preferences
                        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);

                        // record the fact that the app has been started at least once
                        settings.edit().putBoolean("my_first_time", false).commit();
                        startActivity(new Intent(Logpage.this, Landing.class));
                    }
                });
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    rude.setVisibility(View.VISIBLE);
                    lope.setVisibility(View.GONE);
                }else{
                    rude.setVisibility(View.GONE);
                    lope.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Setup dot's indicator
        TabLayout tabLayout = findViewById(R.id.tabLayoutIndicator);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    // ViewPager Adapter class
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int i) {
            return mList.get(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        public void addFragment(Fragment fragment) {
            mList.add(fragment);
        }



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
        Logpage.this.finish();
        System.exit(0);

    }
}