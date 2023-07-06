package com.ocproject7.go4lunch.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String RANKBY = "rankby";
    public static final String RADIUS = "radius";

    SharedPreferences sharedpreferences;

    ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (sharedpreferences.getString(RANKBY, "prominence").equals("prominence")) {
            mBinding.radioButton.setChecked(true);
        } else {
            mBinding.radioButton2.setChecked(true);
        }

        mBinding.radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mBinding.tfRadius.setVisibility(View.GONE);
                } else {
                    mBinding.tfRadius.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.tfRadius.setSuffixText(" m");
        mBinding.etRadius.setText(String.valueOf(sharedpreferences.getInt(RADIUS, 1500)));

        mBinding.btnSave.setOnClickListener(v -> {
            String text = mBinding.etRadius.getText().toString();
            editor.putInt(RADIUS, Integer.parseInt(text)).apply();
            if (mBinding.rgRankBy.getCheckedRadioButtonId() == R.id.radioButton) {
                sharedpreferences.edit().putString(RANKBY, "prominence").apply();
            } else if (mBinding.rgRankBy.getCheckedRadioButtonId() == R.id.radioButton2) {
                sharedpreferences.edit().putString(RANKBY, "distance").apply();
            }
            finish();
        });
    }
}