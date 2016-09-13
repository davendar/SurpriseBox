package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.login.SexPrefChangeCallback;
import com.inmobi.surprise.lib.notification.NotificationScheduler;
import com.inmobi.surprise.lib.util.Constants;
import com.inmobi.surprise.lib.util.OnSwipeTouchListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/12/16.
 */
public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener, SexPrefChangeCallback {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NotificationScheduler notificationScheduler;


    Spinner spinner;
    RelativeLayout rlSpinnerParent;
    RelativeLayout spinnerAgeParent;
    Switch switchButton;
    TextView left_text, right_text;
    ImageView settingsBackground;
    RelativeLayout rlBackParent;
    ImageView big_red;
    ImageView ivLimitNotif;
    String[] ageRange;
    ArrayAdapter<String> dataAdapter;
    RelativeLayout rlTermsParent;
    TextView tvAppName;
    RelativeLayout settingsParent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injectors.initialize(getApplicationContext());
        Injectors.APP_COMPONENT.inject(this);
        setContentView(R.layout.settings);
        settingsParent = (RelativeLayout) findViewById(R.id.settingsParent);
        settingsParent.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {

            }

            @Override
            public void onSwipeLeft() {
                finishActivity();
            }

            @Override
            public void onSwipeTop() {

            }

            @Override
            public void onSwipeBottom() {

            }
        });


        tvAppName = (TextView) findViewById(R.id.tvAppName);
        Typeface surpriseTypeFace = Typeface.createFromAsset(getAssets(), "fonts/coheadline.ttf");
        tvAppName.setText(tvAppName.getText().toString().toLowerCase());
        tvAppName.setTypeface(surpriseTypeFace);
        settingsBackground = (ImageView) findViewById(R.id.settingsBackground);
        rlBackParent = (RelativeLayout) findViewById(R.id.rlBackParent);
        rlBackParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerAgeParent = (RelativeLayout) findViewById(R.id.spinnerAgeParent);
        rlSpinnerParent = (RelativeLayout) findViewById(R.id.rlSpinnerParent);
        rlSpinnerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });
        left_text = (TextView) findViewById(R.id.left_text);
        right_text = (TextView) findViewById(R.id.right_text);

        //Sex prefs
        switchButton = (Switch) findViewById(R.id.switchButton);
        boolean isFemale = sharedPreferences.getBoolean(Constants.SEX_PREF, Constants.DEFAULT_SEX_PREF);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSexPrefChanged(isChecked);
                sharedPreferences.edit().putBoolean(Constants.SEX_PREF, isChecked).apply();
            }
        });
        switchButton.setChecked(isFemale);
        onSexPrefChanged(isFemale);

        // Big Red View
//        big_red = (ImageView) findViewById(R.id.big_red);
//        big_red.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finishActivity();
//            }
//        });


        // Creating adapter for spinner
        ageRange = getResources().getStringArray(R.array.age_range);
        dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, ageRange);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        int selectedRange = sharedPreferences.getInt(Constants.AGE_RANGE, 0);
        spinner.setSelection(selectedRange);
        spinner.setOnItemSelectedListener(this);
        ivLimitNotif = (ImageView) findViewById(R.id.ivLimitNotif);
        boolean isLimited = sharedPreferences.getBoolean(Constants.LIMIT_NOTIF, false);
        setNotifSwitch(isLimited);
        ivLimitNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLimited = sharedPreferences.getBoolean(Constants.LIMIT_NOTIF, false);
                sharedPreferences.edit().putBoolean(Constants.LIMIT_NOTIF, !isLimited).apply();
                setNotifSwitch(!isLimited);
                notificationScheduler.scheduleNotificationService();
            }
        });
        rlTermsParent = (RelativeLayout) findViewById(R.id.rlTermsParent);
        rlTermsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TcActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setNotifSwitch(boolean isLimited) {
        if (!isLimited) {
            ivLimitNotif.setImageResource(R.drawable.switch_off);
        } else {
            ivLimitNotif.setImageResource(R.drawable.switch_on);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_right_to_left);
    }

    @Override
    public void onSexPrefChanged(boolean isFemale) {
        if (isFemale) {
            right_text.setVisibility(View.GONE);
            left_text.setVisibility(View.VISIBLE);
            Picasso.with(SettingsActivity.this).load(R.drawable.third_frag_background_female)
                    .into(settingsBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                            Animation fadeOut = new AlphaAnimation(0.5f, 1);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(200);
                            settingsBackground.startAnimation(fadeOut);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            right_text.setVisibility(View.VISIBLE);
            left_text.setVisibility(View.GONE);
            Picasso.with(SettingsActivity.this).load(R.drawable.third_frag_background_male)
                    .into(settingsBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                            Animation fadeOut = new AlphaAnimation(0.5f, 1);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(200);
                            settingsBackground.startAnimation(fadeOut);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sharedPreferences.edit().putInt(Constants.AGE_RANGE, position).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
