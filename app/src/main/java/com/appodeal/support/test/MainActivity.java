package com.appodeal.support.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String APP_KEY = "280572650a076420aff44edb7c91ba9f7cf717f6c3c4e3f8";
    private static final String KEY_TIMER = "TIMER";
    private static final String KEY_BANNERSHOWN = "BANNERSHOWN";
    private static final String KEY_ADDIS = "ADDIS";
    private static final String KEY_TIMEUP = "TIMEUP";
    private static final String KEY_BTNCLCKD = "BTNCLCKD";
    private NativeAdapter nativeAdapter;
    private AppodealWrapperAdapter appodealWrapperAdapter;
    private LinearLayoutManager linearLayoutManager;
    private CountDownTimer cdTimer;
    private TextView cdTimerTV;
    private Button disableShowAdButton;
    private RecyclerView nativeAdRV;
    private long millisIF = 30000;
    private long millisUF;
    private int numberofNAds = 3;
    boolean consent=false;
    boolean isBannerShown=false;
    boolean isAdDisabled=false;
    boolean isTimeUp=false;

    private void setTimer(long millisInFuture){
        cdTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cdTimerTV.setText(Long.toString(millisUntilFinished/1000));
                millisUF= millisUntilFinished;
                if(!isBannerShown && (millisIF - millisUntilFinished)>=5000)
                    hideBanner();
            }
            @Override
            public void onFinish() {
                cdTimerTV.setText(Integer.toString(0));
                millisUF=0;
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

    private void initList() {
        List<Integer> publisherList = new ArrayList<>();

        int lastValue = nativeAdapter.getLastValue();
        for (int i = 1; i <= 20; i++) {
            publisherList.add(i + lastValue);
        }
        nativeAdapter.addPack(publisherList);
    }
    private void showNativeAdRV() {
        nativeAdRV.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        disableShowAdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isTimeUp && !isAdDisabled)
                    disableAd();
                showNativeAdRV();
                disableShowAdButton.setEnabled(false);
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
            public void onBannerShown() {if (cdTimer==null)setTimer(millisIF);}
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
            public void onInterstitialClosed() {setTimer(millisIF);}
            @Override
            public void onInterstitialExpired() {}
        });

        Appodeal.setNativeCallbacks(new NativeCallbacks() {
            @Override
            public void onNativeLoaded() {}

            @Override
            public void onNativeFailedToLoad() {}

            @Override
            public void onNativeShown(NativeAd nativeAd) {}

            @Override
            public void onNativeClicked(NativeAd nativeAd) {}

            @Override
            public void onNativeExpired() {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER);
        Appodeal.onResume(this, Appodeal.INTERSTITIAL);
        Appodeal.onResume(this, Appodeal.NATIVE);
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
        disableShowAdButton=findViewById(R.id.disableShowAdButton);
        nativeAdRV = findViewById(R.id.nativeAdRV);

        if (savedInstanceState != null) {
            millisUF=savedInstanceState.getLong(KEY_TIMER, 0);
            if(millisUF==0)
                millisUF=millisIF;
            isAdDisabled=savedInstanceState.getBoolean(KEY_ADDIS,false);
            isBannerShown=savedInstanceState.getBoolean(KEY_BANNERSHOWN,false);
            isTimeUp=savedInstanceState.getBoolean(KEY_TIMEUP,false);
            if (!isTimeUp && isAdDisabled) {
                disableShowAdButton.setEnabled(false);
                cdTimerTV.setVisibility(View.GONE);
                nativeAdRV.setVisibility(View.VISIBLE);
            }
            else if(!isTimeUp && !isAdDisabled) {
                disableShowAdButton.setEnabled(true);
                nativeAdRV.setVisibility(View.GONE);
                cdTimerTV.setVisibility(View.VISIBLE);
                setTimer(millisUF);
            }
            else if(isTimeUp && savedInstanceState.getBoolean(KEY_BTNCLCKD, false)) {
                disableShowAdButton.setEnabled(false);
                nativeAdRV.setVisibility(View.VISIBLE);
                cdTimerTV.setVisibility(View.VISIBLE);
                setTimer(millisUF);
            }
            else if (isTimeUp && !savedInstanceState.getBoolean(KEY_BTNCLCKD, false)) {
                disableShowAdButton.setEnabled(true);
                nativeAdRV.setVisibility(View.GONE);
                cdTimerTV.setVisibility(View.VISIBLE);
                setTimer(millisUF);
            }
        }

        setListener();
        Appodeal.setTesting(true);
        Appodeal.initialize(this, APP_KEY, Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.NATIVE, consent);
        setCallBacks();
        if (!isBannerShown)
            Appodeal.show(this, Appodeal.BANNER_TOP);
        Appodeal.cache(MainActivity.this, Appodeal.NATIVE, numberofNAds);
        nativeAdapter = new NativeAdapter(new ArrayList<Integer>());
        initList();
        appodealWrapperAdapter = new AppodealWrapperAdapter(nativeAdapter, 2, 1);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        nativeAdRV.setLayoutManager(linearLayoutManager);
        nativeAdRV.setAdapter(appodealWrapperAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_TIMER, millisUF);
        outState.putBoolean(KEY_ADDIS, isAdDisabled);
        outState.putBoolean(KEY_BANNERSHOWN, isBannerShown);
        outState.putBoolean(KEY_TIMEUP, isTimeUp);
        outState.putBoolean(KEY_BTNCLCKD, !disableShowAdButton.isEnabled());
        if(cdTimer!=null) cdTimer.cancel();
    }
}