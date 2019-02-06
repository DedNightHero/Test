package com.appodeal.support.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.BannerCallbacks;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String APP_KEY = "280572650a076420aff44edb7c91ba9f7cf717f6c3c4e3f8";
    private CountDownTimer cdTimer;
    private TextView cdTimerTV;
    private Button disableAdButton;
    private long millisInFuture = 30000;
    boolean consent=false;
    boolean isBannerShown=false;
    boolean isAdDisabled=false;
    boolean isTimeUp=false;

    private void setTimer(){
        cdTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cdTimerTV.setText(Long.toString(millisUntilFinished/1000));
                if(!isBannerShown && (millisInFuture - millisUntilFinished)>=5000)
                    hideBanner();
            }
            @Override
            public void onFinish() {
                cdTimerTV.setText(Integer.toString(0));
                if (Appodeal.isLoaded(Appodeal.INTERSTITIAL) && !isAdDisabled){
                    Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                    isTimeUp=true;
                }
            }
        }.start();
    }

    private void hideBanner(){
        Appodeal.hide(this ,Appodeal.BANNER);
        isBannerShown=true;
    }

    private void disableAd() {
        isAdDisabled=true;
        cdTimerTV.setVisibility(View.GONE);
    }

    private void setListener() {
        disableAdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isTimeUp && !isAdDisabled)
                    disableAd();
            }
        });
    }
    private void setCallBacks() {
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {}
            @Override
            public void onBannerFailedToLoad() {}
            @Override
            public void onBannerShown() {setTimer();}
            @Override
            public void onBannerClicked() {}
            @Override
            public void onBannerExpired() {}
        });

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {}
            @Override
            public void onInterstitialFailedToLoad() {}
            @Override
            public void onInterstitialShown() {}
            @Override
            public void onInterstitialClicked() {}
            @Override
            public void onInterstitialClosed() {setTimer();}
            @Override
            public void onInterstitialExpired() {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER);
        Appodeal.onResume(this, Appodeal.INTERSTITIAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23 && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Appodeal.requestAndroidMPermissions(this, new AppodealPermissionCallbacks(this));
        }
        else
            consent=true;

        cdTimerTV=findViewById(R.id.cdTimerTV);
        disableAdButton=findViewById(R.id.disableAdButton);
        setListener();
        Appodeal.setTesting(true);
        Appodeal.initialize(this, APP_KEY, Appodeal.INTERSTITIAL | Appodeal.BANNER, consent);
        setCallBacks();
        Appodeal.show(this, Appodeal.BANNER_TOP);
    }
}