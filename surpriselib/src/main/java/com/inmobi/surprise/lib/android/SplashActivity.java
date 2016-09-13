package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.login.LoginActivity;
import com.inmobi.surprise.lib.util.Constants;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/10/16.
 */
public class SplashActivity extends Activity {

    @Inject
    SharedPreferences sharedPreferences;

    Handler handler;
    RelativeLayout rlLogoParent;
    AnimatorSet mAnimationSet;
    private boolean isVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        handler = new Handler(Looper.getMainLooper());
        rlLogoParent = (RelativeLayout) findViewById(R.id.rlLogoParent);
        TextView tvAppName = (TextView) findViewById(R.id.tvAppName);
        Typeface surpriseTypeFace = Typeface.createFromAsset(getAssets(), "fonts/coheadline.ttf");
        tvAppName.setText(tvAppName.getText().toString().toLowerCase());
        tvAppName.setTypeface(surpriseTypeFace);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        mAnimationSet = new AnimatorSet();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && isVisible) {
                    Injectors.initialize(getApplicationContext());
                    Injectors.APP_COMPONENT.inject(SplashActivity.this);
                    if (!sharedPreferences.getBoolean(Constants.ACCEPTED_TERMS, false)) {
                        Picasso.with(SplashActivity.this).load(R.drawable.first_frag_background).fetch();
                        Picasso.with(SplashActivity.this).load(R.drawable.second_frag_background).fetch();
                        Picasso.with(SplashActivity.this).load(R.drawable.third_frag_background_female).fetch();
                        Picasso.with(SplashActivity.this).load(R.drawable.third_frag_background_male).fetch();
                    }
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(rlLogoParent, View.ALPHA, 1f, .01f);
                    fadeIn.setDuration(800);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlLogoParent, View.SCALE_X, 1f, 0f);
                    scaleX.setDuration(800);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlLogoParent, View.SCALE_Y, 1f, 0f);
                    scaleY.setDuration(800);
                    long start = System.currentTimeMillis();
                    Log.e("TIME TAKEN", "" + (System.currentTimeMillis() - start));
                    mAnimationSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Intent intent;
                            if (sharedPreferences.getBoolean(Constants.ACCEPTED_TERMS, false)) {
                                intent = new Intent(SplashActivity.this, AdPreloadActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, LoginActivity.class);
                            }
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Log.e("ANIMAT", "CANCELLED NOW");
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mAnimationSet.playTogether(fadeIn, scaleX, scaleY);
                    mAnimationSet.start();
                }
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        cancelAnimation();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        cancelAnimation();
        super.onStop();
    }

    private void cancelAnimation() {
        try {
            mAnimationSet.removeAllListeners();
            mAnimationSet.cancel();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            //TODO Log
        }
    }


}

