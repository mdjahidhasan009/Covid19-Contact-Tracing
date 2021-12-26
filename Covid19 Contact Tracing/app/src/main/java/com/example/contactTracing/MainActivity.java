package com.example.contactTracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class MainActivity extends AwesomeSplash {

    @Override
    public void initSplash ( ConfigSplash configSplash ) {

        ActionBar a = getSupportActionBar();
        a.hide();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        configSplash.setBackgroundColor(R.color.Wheat);
        configSplash.setAnimLogoSplashDuration( 1000 );
        configSplash.setRevealFlagX( Flags.REVEAL_RIGHT );
        configSplash.setRevealFlagX( Flags.REVEAL_TOP );
        configSplash.setLogoSplash( R.drawable.ic_covpost);
        configSplash.setOriginalHeight(50); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(50); //in relation to your svg (path) resource
        configSplash.setPathSplashStrokeSize(3);
        configSplash.setAnimLogoSplashDuration( 1000 );
        configSplash.setPathSplash("");
        configSplash.setAnimLogoSplashTechnique( Techniques.FadeInRight );

        configSplash.setTitleSplash("CovPost : Covid Guard");
        configSplash.setTitleTextColor(R.color.colorPrimary);
        configSplash.setTitleTextSize(25f);
        configSplash.setAnimTitleDuration(1000);
        configSplash.setAnimTitleTechnique(Techniques.FadeInRight);
//        configSplash.setTitleFont("fonts/mochiypopponeregular.ttf");
    }

    @Override
    public void animationsFinished () {
        final String PREF_NAME = "MyPref";

        // getting saved preferences
        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            startActivity(new Intent(MainActivity.this, Logpage.class));
        }else{
            startActivity(new Intent(MainActivity.this, Landing.class));
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
        MainActivity.this.finish();
        System.exit(0);

    }
}