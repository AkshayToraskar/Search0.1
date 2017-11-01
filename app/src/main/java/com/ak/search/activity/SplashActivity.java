package com.ak.search.activity;

/**
 * splash activity which check local language and login status and proceed further depending on that
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ak.search.R;
import com.ak.search.app.SessionManager;
import com.ak.search.realm_model.DataCollection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.ivGifImg)
    ImageView ivGifImg;

    Locale myLocale;
    public static int SPLASH_TIME_OUT = 1000;
    private SessionManager sessionManager;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        realm = Realm.getDefaultInstance();

        ButterKnife.bind(this);
        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.getLanguage().equals("mr")) {
            setLocale("mr");
        } else if (sessionManager.getLanguage().equals("en")) {
            setLocale("en");
        }

        if (sessionManager.isDeleteOldData()) {
            manageOldData();
        } else {
            callHandler();
        }
    }

    public void callHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if (sessionManager.isLoggedIn()) {
                    if (sessionManager.getLoginType() == 3) {
                        Intent i = new Intent(SplashActivity.this, SelectSurveyActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    //check and delete old data
    public void manageOldData() {

        List<DataCollection> dataCollectionList = realm.where(DataCollection.class).findAll();
        Log.v("no of days", " " + sessionManager.getInDays());
        for (final DataCollection dataCollection : dataCollectionList) {

            if (isOldData(dataCollection.getTimestamp())) {
                Log.v("Data is :", " Old");

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        dataCollection.deleteFromRealm();
                    }
                });


            }


        }

        callHandler();

    }


    public boolean isOldData(String timeStamp) {

        boolean isOld = false;
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy:hh.mm.ss");
        Date dt1 = null;
        try {
            dt1 = df.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date dt2 = new Date();
        long diff = dt2.getTime() - dt1.getTime();
        //long diffSeconds = diff / 1000 % 60;
        //long diffMinutes = diff / (60 * 1000) % 60;
        //long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));

        Log.v("no of days", " " + sessionManager.getInDays());
        if (diffInDays > sessionManager.getInDays()) {
            isOld = true;
        }
        return isOld;
    }

}
