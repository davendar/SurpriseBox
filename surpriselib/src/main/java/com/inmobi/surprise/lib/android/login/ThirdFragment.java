package com.inmobi.surprise.lib.android.login;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.AdPreloadActivity;
import com.inmobi.surprise.lib.android.Injectors;
import com.inmobi.surprise.lib.android.TcActivity;
import com.inmobi.surprise.lib.util.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import sdk.adenda.lockscreen.AdendaAgent;
import sdk.adenda.widget.AdendaButtonCallback;

import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/7/16.
 */
public class ThirdFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @Inject
    SharedPreferences sharedPreferences;


    Spinner spinner;
    RelativeLayout click;
    RelativeLayout rlSpinnerParent;
    Switch switchButton;
    TextView left_text, right_text;
    TextView tvByTapping_terms;
    String[] ageRange;
    ArrayAdapter<String> dataAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.third_login_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injectors.APP_COMPONENT.inject(this);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        rlSpinnerParent = (RelativeLayout) view.findViewById(R.id.rlSpinnerParent);
        rlSpinnerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });
        left_text = (TextView) view.findViewById(R.id.left_text);
        right_text = (TextView) view.findViewById(R.id.right_text);
        tvByTapping_terms = (TextView) view.findViewById(R.id.tvByTapping_terms);
        tvByTapping_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TcActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });


        switchButton = (Switch) view.findViewById(R.id.switchButton);
        boolean isFemale = sharedPreferences.getBoolean(Constants.SEX_PREF, Constants.DEFAULT_SEX_PREF);
        switchButton.setChecked(isFemale);
        setTextViewVisibility(isFemale);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextViewVisibility(isChecked);
                onSexPrefChanged(isChecked);
                sharedPreferences.edit().putBoolean(Constants.SEX_PREF, isChecked).apply();
            }
        });


        // Creating adapter for spinner
        ageRange = getResources().getStringArray(R.array.age_range);
        dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_textview, ageRange);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        int position = sharedPreferences.getInt(Constants.AGE_RANGE, 0);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(this);

        click = (RelativeLayout) view.findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                click.setOnClickListener(null);
                sharedPreferences.edit().putBoolean(Constants.ACCEPTED_TERMS, true).apply();
                setupAdenda(getActivity().getApplicationContext());
                Intent intent = new Intent(getActivity(), AdPreloadActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                click.setOnClickListener(this);
            }
        });
    }

    private void setTextViewVisibility(boolean isFemale) {
        if (isFemale) {
            right_text.setVisibility(View.GONE);
            left_text.setVisibility(View.VISIBLE);
        } else {
            right_text.setVisibility(View.VISIBLE);
            left_text.setVisibility(View.GONE);
        }
    }


    private void onSexPrefChanged(boolean isFemale) {
        if (getActivity() instanceof SexPrefChangeCallback) {
            ((SexPrefChangeCallback) getActivity()).onSexPrefChanged(isFemale);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sharedPreferences.edit().putInt(Constants.AGE_RANGE, position).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupAdenda(Context context) {
        boolean isLockscreenEnabled = sharedPreferences.getBoolean(Constants.DEF_LOCKSCREEN_STATE_PREF,
                Constants.DEF_LOCKSCREEN_STATE);
        if (isLockscreenEnabled) {
            AdendaAgent.setEnableAds(context, true);
            // AdendaAgent.setUnlockType(getApplicationContext(), AdendaAgent.ADENDA_UNLOCK_TYPE_DEFAULT);
            int adendaRatio = sharedPreferences.getInt(Constants.DEF_ADENDA_RATIO_PREF,
                    Constants.DEF_ADENDA_RATIO);
            AdendaAgent.setAdendaRatio(context, adendaRatio);
            AdendaAgent.LockScreenHelper lockScreenHelper = new AdendaAgent.LockScreenHelper(context,
                    new AdendaButtonCallback() {
                        @Override
                        public String getUserId() {
                            return "123456";
                        }

                        @Override
                        public String getUserGender() {
                            boolean isFemale = sharedPreferences.getBoolean(Constants.SEX_PREF, Constants.DEFAULT_SEX_PREF);
                            return isFemale ? "f" : "m";
                        }

                        @Override
                        public String getUserDob() {
                            return "12345678";
                        }

                        @Override
                        public float getUserLatitude() {
                            return 0;
                        }

                        @Override
                        public float getUserLongitude() {
                            return 0;
                        }

                        @Override
                        public void onPreOptIn() {
                            Log.e("AdendaButton", "onPreOptIn");
                        }

                        @Override
                        public void onPreOptOut() {

                        }

                        @Override
                        public void onPostOptIn() {
                            Log.e("AdendaButton", "onPostOptIn");
                        }

                        @Override
                        public void onPostOptOut() {

                        }
                    });
            lockScreenHelper.startLockscreen();
        }
    }
}
