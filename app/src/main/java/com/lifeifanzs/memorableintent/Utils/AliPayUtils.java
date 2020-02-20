package com.lifeifanzs.memorableintent.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.net.URISyntaxException;

public class AliPayUtils {

    private static Context mApplicationContext;


    public static void setApplicationContext(Context context) {
        mApplicationContext = context;
    }


    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     *
     * @return 支付宝客户端是否已安装
     */
    public static boolean hasInstalledAlipayClient() {
        String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
        PackageManager pm = mApplicationContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 支付宝转账
     * @param activity
     * **/
    public static void openALiPay(Activity activity, String paycode) {
        String url1 = "intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F" + paycode + "%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        //String url1=activity.getResources().getString(R.string.alipay);
        Intent intent = null;
        if (hasInstalledAlipayClient()) {
            try {
                intent = Intent.parseUri(url1, Intent.URI_INTENT_SCHEME);
                activity.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(mApplicationContext, "出错啦", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mApplicationContext, "您未安装支付宝哦！(>ω･* )ﾉ", Toast.LENGTH_SHORT).show();
        }
    }
}
