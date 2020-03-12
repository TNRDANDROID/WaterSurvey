package com.nic.watersurveyform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BuildConfig;

import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.databinding.SplashScreenBinding;
import com.nic.watersurveyform.helper.AppVersionHelper;
import com.nic.watersurveyform.utils.Utils;
import com.nic.watersurveyform.windowpreferences.WindowPreferencesManager;


public class SplashScreen extends AppCompatActivity implements
        AppVersionHelper.myAppVersionInterface {
    private TextView textView;
    private Button button;
    private static int SPLASH_TIME_OUT = 2500;
    private PrefManager prefManager;
    public SplashScreenBinding splashScreenBinding;
    Animation smalltobig, fleft, fhelper,stb2;
    final Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreenBinding = DataBindingUtil.setContentView(this, R.layout.splash_screen);
        splashScreenBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());

        prefManager = new PrefManager(this);
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("production")) {
            if (Utils.isOnline()) {
                checkAppVersion();
            } else {
                showSignInScreen();

            }
        } else {
            showSignInScreen();
        }

        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        stb2 = AnimationUtils.loadAnimation(this, R.anim.stb2);
        fleft = AnimationUtils.loadAnimation(this, R.anim.fleft);
        fhelper = AnimationUtils.loadAnimation(this, R.anim.fhelper);
        splashScreenBinding.ivSplash.startAnimation(smalltobig);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
splashScreenBinding.ivlogo.setVisibility(View.VISIBLE);
                splashScreenBinding.ivlogo.startAnimation(stb2);
            }
        }, 500);


        splashScreenBinding.ivSubtitle.setTranslationX(400);

        splashScreenBinding.ivSubtitle.setAlpha(0);

        splashScreenBinding.ivSubtitle.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1200).start();



    }


    private void showSignInScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, LoginScreen.class);

                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, SPLASH_TIME_OUT);
    }


    private void checkAppVersion() {
        new AppVersionHelper(this, SplashScreen.this).callAppVersionCheckApi();
    }

    @Override
    public void onAppVersionCallback(String value) {
        if (value.length() > 0 && "Update".equalsIgnoreCase(value)) {
            startActivity(new Intent(this, AppUpdateDialog.class));
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            showSignInScreen();
        }

    }

}
