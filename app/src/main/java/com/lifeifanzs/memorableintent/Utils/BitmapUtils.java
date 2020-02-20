package com.lifeifanzs.memorableintent.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.text.BoringLayout;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class BitmapUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity, int destWidth, int destHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        boolean isUri = path.contains("content://media");
//        boolean isMiUi=path.contains("miui");


//        if (isUri&&!isMiUi) {
//            try {
//
//                BitmapFactory.decodeStream(activity
//                        .getContentResolver()
//                        .openInputStream(Uri.parse(path)), null, options);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
            BitmapFactory.decodeFile(path, options);
//        }



        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale :
                    widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;



        Bitmap bitmap=null;
//        if (isUri) {
//            try {
//                bitmap = BitmapFactory.decodeStream(activity
//                        .getContentResolver()
//                        .openInputStream(Uri.parse(path)), null, options);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
            bitmap = BitmapFactory.decodeFile(path, options);
//        }


        if (srcWidth/inSampleSize > destWidth) {
            float widthScale = destWidth / (srcWidth/inSampleSize);
            Matrix matrix=new Matrix();
            matrix.postScale(widthScale,widthScale);

            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }

        return bitmap;
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, activity, size.x-96, size.y);
    }

    public static Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string=Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

}
