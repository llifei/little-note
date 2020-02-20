package com.lifeifanzs.memorableintent.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.Utils.CropPictureUtils;
import com.lifeifanzs.memorableintent.Utils.MiuiAlbumUtils;
import com.lifeifanzs.memorableintent.Utils.ThemeUtils;
import com.lifeifanzs.memorableintent.R;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;

public class    ThemeActivity extends NoActionBarActivity {

    private static final int REQUEST_ALBUM = 0;
    private static final int REQUEST_CROP = 1;

    private Theme mTheme;
    private ThemeLab mThemeLab;
    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private Button mWhiteButton;
    private Button mBlackButton;
    private Button mBlueButton;
    private Button mPinkButton;
    private Button mGreenButton;
    private String mThemeColor;



    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ThemeActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThemeLab = ThemeLab.getThemeLab(this);
        mTheme = mThemeLab.getTheme();
        mThemeColor = mTheme.getColor();

        setStatusAndBackgroundColor(mTheme);

        setContentView(R.layout.activity_theme);

        mToolbar = findViewById(R.id.theme_toolbar);
        System.out.println(mThemeColor);
        if (!mThemeColor.equals("DISPLAY")) {
            mToolbar.setBackgroundColor(Color
                    .parseColor(ThemeUtils.getColor(mThemeColor)));
        } else {
            mToolbar.getBackground().mutate().setAlpha(0);//设置toolbar为透明
        }
        mToolbar.inflateMenu(R.menu.activity_theme);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });//设置返回监听

        mRadioGroup = findViewById(R.id.theme_radioGroup);
        mBlueButton = findViewById(R.id.button_blue);
        mWhiteButton = findViewById(R.id.button_white);
        mBlackButton = findViewById(R.id.button_black);
        mGreenButton = findViewById(R.id.button_green);
        mPinkButton = findViewById(R.id.button_pink);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId == mWhiteButton.getId()) {
                    mThemeColor = "WHITE";
                } else if (checkedId == mBlackButton.getId()) {
                    mThemeColor = "BLACK";
                } else if (checkedId == mBlueButton.getId()) {
                    mThemeColor = "BLUE";
                } else if (checkedId == mGreenButton.getId()) {
                    mThemeColor = "GREEN";
                } else if (checkedId == mPinkButton.getId()) {
                    mThemeColor = "PINK";
                }
                mTheme.setColor(mThemeColor);
                setStatusAndBackgroundColor(mTheme);
                mToolbar.setBackgroundColor(Color
                        .parseColor(ThemeUtils.getColor(mThemeColor)));
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.confirm_theme:
                        returnMainAcitivity();
                        return true;
                    case R.id.display_theme:
                        getImage();

                    default:
                        return false;
                }
            }
        });

    }

    /**
     * 返回主页
     */
    private void returnMainAcitivity(){
        mThemeLab.updateTheme(mTheme);
        sMainActivity.setStatusAndBackgroundColor(mTheme);
//        sMainActivity.setSelectPointColor(mTheme);
        sMainActivity.collBack();
        finish();
    }

    private void getImage() {
//        Intent albumIntent = new Intent(Intent.ACTION_PICK);
//        albumIntent.setDataAndType(MediaStore.Images
//                .Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(albumIntent, REQUEST_ALBUM);
        CropPictureUtils.getByAlbum(this);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap=CropPictureUtils.onActivityResult(this,requestCode,resultCode,data);
        Uri uri=CropPictureUtils.onActivityResult(this,requestCode,resultCode,data);
        if(resultCode==Activity.RESULT_OK){
            if(uri!=null) {
                mTheme.setColor("DISPLAY");
                mTheme.setUri(uri.toString());
                returnMainAcitivity();//返回主页
            }
        }
    }

}
