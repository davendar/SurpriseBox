package com.inmobi.surprise;

import com.inmobi.surprise.lib.android.SplashActivity;
import com.inmobi.surprise.lib.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by davendar.ojha on 8/9/16.
 */
public class LoaderActivity extends Activity {

    static {
        Constants.APPLICATION_ID = "aabbccddeeffgghhiijjkkllmmnnoopp";
        Constants.APP_P_ID = 1234567892614L;
        Constants.BRAND_P_ID = 1234567892614L;
        Constants.COMMERCE_P_ID = 1234567892614L;
        Constants.SPONSOR_P_ID = 1234567892614L;
        Constants.FAN_P_ID = "aabbccddeeffgg_hhiijjkkllmmnnoopp";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(LoaderActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
