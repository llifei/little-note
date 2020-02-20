package com.lifeifanzs.memorableintent.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;

public class FeedbackActivity extends NoActionBarActivity {

    private Theme mTheme;
    private ThemeLab mThemeLab;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mThemeLab=ThemeLab.getThemeLab(this);
        mTheme=mThemeLab.getTheme();
        setStatusAndBackgroundColor(mTheme);

        setContentView(R.layout.activity_feedback);

    }
}
