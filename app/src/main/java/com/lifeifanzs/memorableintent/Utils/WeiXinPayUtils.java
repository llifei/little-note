package com.lifeifanzs.memorableintent.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WeiXinPayUtils {
    private static final String TAG = WeiXinPayUtils.class.getSimpleName();
    private static final String TENCENT_PACKAGE_NAME = "com.tencent.mm";
    private static final String TENCENT_ACTIVITY_BIZSHORTCUT = "com.tencent.mm.action.BIZSHORTCUT";
    private static final String TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT = "LauncherUI.From.Scaner.Shortcut";

    public WeiXinPayUtils() {
    }

    @SuppressLint("WrongConstant")
    private static void gotoWeChatQrScan(@NonNull Activity activity) {
        Intent intent = new Intent("com.tencent.mm.action.BIZSHORTCUT");
        intent.setPackage("com.tencent.mm");
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
        intent.addFlags(343932928);

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException var3) {
            Toast.makeText(activity, "你好像没有安装微信", 0).show();
        }

    }

    private static void sendPictureStoredBroadcast(Context context, String qrSavePath) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri uri = Uri.fromFile(new File(qrSavePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static void saveDonateQrImage2SDCard(@NonNull String qrSavePath, @NonNull Bitmap qrBitmap) {
        File qrFile = new File(qrSavePath);
        File parentFile = qrFile.getParentFile();
        boolean mk=parentFile.exists();
        if (!parentFile.exists()) {
           mk=parentFile.mkdirs();
        }

        try {
            System.out.println(mk);
            FileOutputStream fos = new FileOutputStream(qrFile);
            mk=qrFile.exists();

            qrBitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public static void donateViaWeiXin(Activity activity, String qrSavePath) {
        if (activity != null && !TextUtils.isEmpty(qrSavePath)) {
            sendPictureStoredBroadcast(activity, qrSavePath);
            gotoWeChatQrScan(activity);
        } else {
            Log.d(TAG, "参数为null");
        }
    }

    public static boolean hasInstalledWeiXinClient(Context context) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo info = pm.getPackageInfo("com.tencent.mm", 0);
            return info != null;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public static String getWeiXinClientVersion(Context context) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo info = pm.getPackageInfo("com.tencent.mm", 0);
            return info.versionName;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }
}

