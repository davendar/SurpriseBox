package com.inmobi.surprise.lib.android.login;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.MainActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import sdk.adenda.lockscreen.AdendaAgent;
import sdk.adenda.lockscreen.fragments.AdendaFragmentInterface;

/**
 * Created by davendar.ojha on 7/7/16.
 */


public class FirstFragment extends Fragment implements AdendaFragmentInterface {

    ScreenStateReceiver mReceiver;
    boolean mScreenOn;

    int i = 5;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_login_frag, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdendaAgent.setUnlockType(getActivity().getApplicationContext(), AdendaAgent.ADENDA_UNLOCK_TYPE_GLOWPAD);

        final TextView tvAppName = (TextView) view.findViewById(R.id.tvAppName);


        ImageView iv = (ImageView) view.findViewById(R.id.ivAppLogo);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface surpriseTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coheadline.ttf");
                tvAppName.setText(tvAppName.getText().toString().toLowerCase());
                tvAppName.setTypeface(surpriseTypeFace);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", .1f, 1f);
                fadeIn.setDuration(2000);
                AnimatorSet mAnimationSet = new AnimatorSet();
                mAnimationSet.play(fadeIn);
                mAnimationSet.start();
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intializeReceiver();
    }

    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mScreenOn = false;
                Log.e("ScreenStateReceiver", "OFF");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                mScreenOn = true;
                Log.e("ScreenStateReceiver", "ON");
                i++;
                if (i > 10) {
                    getActivity().finish();
                }
            }
        }
    }


    private void intializeReceiver() {
        // Instantiate receiver
        mReceiver = new ScreenStateReceiver();
        // Create Screen On and Screen Off filters for BroadcastReceiver
        IntentFilter screenFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        // Register screen filters
        getActivity().registerReceiver(mReceiver, screenFilter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public boolean expandOnRotation() {
        return false;
    }

    @Override
    public Intent getActionIntent() {
        return new Intent(getActivity(), MainActivity.class);
    }

    @Override
    public boolean coverEntireScreen() {
        return true;
    }

    @Override
    public Pair<Integer, Integer> getGlowpadResources() {
        return null;
    }

    @Override
    public boolean getStartHelperForResult() {
        return false;
    }

    @Override
    public void onActionFollowedAndLockScreenDismissed() {
        getActivity().finish();
    }
}
