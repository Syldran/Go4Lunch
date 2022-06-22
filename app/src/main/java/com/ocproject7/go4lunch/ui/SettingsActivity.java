package com.ocproject7.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivitySettingsBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String RANKBY = "rankby";
    public static final String RADIUS = "radius";
    private static final String TAG = "TAG_SettingsActivity";

    SharedPreferences sharedpreferences;

    ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();


        if (sharedpreferences.getString(RANKBY, null) == null) {
            if (mBinding.radioButton.isChecked()) {
                editor.putString(RANKBY, "prominence").apply();
            } else {
                mBinding.radioButton2.setChecked(true);
                editor.putString(RANKBY, "distance").apply();
            }
        } else {
            Log.d(TAG, "onCreate: preferences non null");
            if (Objects.equals(sharedpreferences.getString(RANKBY, null), "prominence")){
                Log.d(TAG, "onCreate: == prominence");
                mBinding.radioButton.setChecked(true);
            } else {
                Log.d(TAG, "onCreate: == distance");
                mBinding.radioButton2.setChecked(true);
            }
        }
        Log.d(TAG, "onCreate: "+sharedpreferences.getString(RANKBY, null));




        mBinding.tfRadius.setSuffixText(" m");
        mBinding.etRadius.setText(String.valueOf(sharedpreferences.getInt(RADIUS, 1500)));

        mBinding.btnSave.setOnClickListener(v -> {
            String text = mBinding.etRadius.getText().toString();
            Log.d(TAG, "onCreate: edit text value "+text);
            editor.putInt(RADIUS, Integer.parseInt(text)).apply();
            if (mBinding.rgRankBy.getCheckedRadioButtonId() == R.id.radioButton){
                sharedpreferences.edit().putString(RANKBY, "prominence").apply();
                Log.d(TAG, "setRadioListener: prominence");
            } else if (mBinding.rgRankBy.getCheckedRadioButtonId() == R.id.radioButton2) {
                sharedpreferences.edit().putString(RANKBY, "distance").apply();
                Log.d(TAG, "setRadioListener: distance");
            }
            finish();
        });
    }
}