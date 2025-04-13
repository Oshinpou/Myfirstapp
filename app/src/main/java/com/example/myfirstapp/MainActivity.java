package com.example.myfirstapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

// MainActivity.java

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;

public class MainActivity extends AppCompatActivity {

    private RewardedAd rewardedAd;
    private Handler handler;
    private Runnable adRunnable;

    // Replace with your AdMob Rewarded Ad Unit ID
    private static final String AD_UNIT_ID = "ca-app-pub-1204724319985727/2280769703";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Replace with your layout file

        // Initialize AdMob SDK
        MobileAds.initialize(this, initializationStatus -> {});

        // Load first ad
        loadRewardedAd();

        // Schedule ad display every 2 minutes
        handler = new Handler();
        adRunnable = new Runnable() {
            @Override
            public void run() {
                showRewardedAd();
                handler.postDelayed(this, 2 * 60 * 1000); // every 2 minutes
            }
        };
        handler.post(adRunnable);
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, AD_UNIT_ID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Toast.makeText(MainActivity.this, "Ad failed to load.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(MainActivity.this, rewardItem -> {
                int rewardAmount = rewardItem.getAmount(); // e.g., 1 point
                addPointsToUser(rewardAmount);
                loadRewardedAd(); // Load next ad
            });
        } else {
            loadRewardedAd(); // Try to load again if not ready
        }
    }

    private void addPointsToUser(int points) {
        SharedPreferences prefs = getSharedPreferences("RewardPrefs", MODE_PRIVATE);
        int currentPoints = prefs.getInt("points", 0);
        int updatedPoints = currentPoints + points;

        prefs.edit().putInt("points", updatedPoints).apply();
        Toast.makeText(this, "You earned " + points + " points! Total: " + updatedPoints, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(adRunnable); // Stop ads on destroy
    }
}
