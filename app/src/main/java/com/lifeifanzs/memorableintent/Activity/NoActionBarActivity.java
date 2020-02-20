package com.lifeifanzs.memorableintent.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.R;

import java.io.FileNotFoundException;

public abstract class NoActionBarActivity extends AppCompatActivity {

    private static final String EXTRA_THEME = "theme";
    private String mtheme = "WHITE";


    /**
     * 设置状态栏及其字体颜色
     */
    public int setStatusAndBackgroundColor(Theme mTheme) {
        String mcolor=mTheme.getColor();
        String muristr=mTheme.getUri();
        Resources resources = getResources();
        Intent data = getIntent();
        boolean isDisplay;
        int statusColor;
        Drawable backgroundDrawable = null;
        switch (mcolor) {
            case "WHITE":
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_white);
                statusColor = resources.getColor(R.color.colorWhite);
                break;
            case "BLACK":
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_black);
                statusColor = resources.getColor(R.color.colorBlack);
                break;
            case "BLUE":
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_blue);
                statusColor = resources.getColor(R.color.colorBlue2);
                break;
            case "GREEN":
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_green);
                statusColor = resources.getColor(R.color.colorGreen2);
                break;
            case "PINK":
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_pink);
                statusColor = resources.getColor(R.color.colorPink2);
                break;
            case "DISPLAY":
                isDisplay = true;
                if(muristr!=null){
                    Uri mUri = Uri.parse(muristr);
                    backgroundDrawable=uriToDrawable(mUri);
                }
                statusColor = resources.getColor(R.color.colorTransparent);
                break;
            default:
                backgroundDrawable = resources.getDrawable(R.drawable.bkcolor_white);
                statusColor = resources.getColor(R.color.colorPrimary);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

//            if(mTheme.isStatusColorIsLight()) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                //设置状态栏字体为黑色
//            }

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //添加Flag把状态栏设置为可绘制模式

            getWindow().setStatusBarColor(statusColor);
            //设置状态栏颜色

            getWindow().setBackgroundDrawable(backgroundDrawable);
            //设置activity背景
        }

        return statusColor;

    }

    private Drawable uriToDrawable(Uri mUri){
//        String[] filePathColum = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(
//                mUri, filePathColum, null, null, null);
//        cursor.moveToFirst();
//        int columIndex = cursor.getColumnIndex(filePathColum[0]);
//        String path = cursor.getString(columIndex);
//
//        Bitmap bitmap = BitmapFactory.decodeFile(path);

        Bitmap bitmap=null;
        try {
            bitmap= BitmapFactory.decodeStream(this
                    .getContentResolver().openInputStream(mUri));

            DisplayMetrics dm=getResources().getDisplayMetrics();
            System.out.println("screen"+dm.heightPixels+"×"+dm.widthPixels);
            bitmap=setBitmap(bitmap,dm.heightPixels,dm.widthPixels);
            System.out.println("bitmap:"+bitmap.getHeight()+"×"+bitmap.getWidth());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new BitmapDrawable(bitmap);
    }

    private Bitmap setBitmap(Bitmap bitmap,int newHeight,int newWidth){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return  Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

    }
}

