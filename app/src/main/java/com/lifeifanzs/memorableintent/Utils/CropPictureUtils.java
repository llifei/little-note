package com.lifeifanzs.memorableintent.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CropPictureUtils {

    private static final int REQUEST_ALBUM = 0;
    private static final int REQUEST_CROP = 1;

    private static final String lsimg="file:///sdcard/temp.jpg";

    public static void getByAlbum(Activity act) {
        try {
            new File(act.getFilesDir(),lsimg).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI, "image/*");
        act.startActivityForResult(albumIntent, REQUEST_ALBUM);
    }

    public static Uri onActivityResult(Activity act, int requestCode, int resultCode,
                                          Intent data) {
        return onActivityResult(act, requestCode, resultCode, data, 0, 0, 0, 0);
    }

    public static Uri onActivityResult(Activity act, int requestCode, int resultCode, Intent data
            , int width, int height, int aspectX, int aspectY) {
        Uri u = null;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            switch (requestCode) {
                case REQUEST_ALBUM:
//                    uri = data.getData();
                    uri= MiuiAlbumUtils.geturi(data,act);
                    act.startActivityForResult(crop(act,uri, width, height
                            , aspectX, aspectY), REQUEST_CROP);
                    break;
                case REQUEST_CROP:
//                    bitmap=dealCrop(act);
                    u=Uri.parse(lsimg);

                    break;
            }
        }
        return u;
    }

    private static Intent crop(Activity act,Uri uri, int width, int height, int aspectX, int aspectY) {
        Display mDisplay = act.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        mDisplay.getMetrics(outMetrics);


        if(width==0&&height==0){
            width=outMetrics.widthPixels;
            height=outMetrics.heightPixels;
        }
        if(aspectX==0&&aspectY==0){
            aspectX=width;
            aspectY=height;
        }
        Intent cropIntent=new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(uri,"image/*");
        cropIntent.putExtra("crop",true);
        cropIntent.putExtra("aspectX",aspectX);
        cropIntent.putExtra("aspectY",aspectY);
        cropIntent.putExtra("outputX",width);
        cropIntent.putExtra("outputY",height);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.parse(lsimg));
        cropIntent.putExtra("outputFormat","JPEG");

        cropIntent.putExtra("noFaceDetection",true);
        cropIntent.putExtra("return-data",false);
        return cropIntent;
    }
}

