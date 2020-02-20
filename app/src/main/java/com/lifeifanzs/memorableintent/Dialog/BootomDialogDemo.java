package com.lifeifanzs.memorableintent.Dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.Utils.AliPayUtils;
import com.lifeifanzs.memorableintent.Utils.WeiXinPayUtils;

import java.io.File;
import java.io.InputStream;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;

public class BootomDialogDemo extends BootomDialog {

    private TextView mWeixinTextView;
    private TextView mAliPayTextView;
    private Activity mActivity;
    private RadioGroup mRadios;
    private TextView mDonateText;
    private RadioButton mOne;
    private RadioButton mThree;
    private RadioButton mDisplay;
    private int mWXRid = 0;
    private int mALSid = 0;


    public BootomDialogDemo(Context context) {
        super(context);
        mActivity = (Activity) context;
    }

    @Override
    protected void onCreate() {
        setContentView(R.layout.dialog_donate);
        mWeixinTextView = findViewById(R.id.donate_weixin);
        mAliPayTextView = findViewById(R.id.donate_alipay);

        initRadios();

        mWeixinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateByWeixin();
            }
        });

        mAliPayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateByAliPay();
            }
        });
    }

    private void initRadios() {
        mRadios = findViewById(R.id.donate_radios);
        mOne = findViewById(R.id.donate_one);
        mThree = findViewById(R.id.donate_three);
        mDisplay = findViewById(R.id.donate_display);
        mRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == mOne.getId()) {
                    mWXRid = R.raw.weixin_one;
                    mALSid = R.string.paycode_renyi;
                } else if (checkedId == mThree.getId()) {
                    mWXRid = R.raw.weixin_three;
                    mALSid = R.string.paycode_renyi;
                } else if (checkedId == mDisplay.getId()) {
                    mWXRid = R.raw.weixin_renyi;
                    mALSid = R.string.paycode_renyi;
                }
            }
        });

    }

    private void donateByWeixin() {
        if (mWXRid != 0) {
            Toast.makeText(mActivity, R.string.donate_weixinRemind, Toast.LENGTH_SHORT).show();
            InputStream weixinQrIs = mActivity.getResources().openRawResource(mWXRid);
            String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "littlenote" + File.separator +
                    "lifeifanzs_weixin.png";
//            String qrPath = "data/data/com.lifeifanzs.memorableintent/lifeifanzs_weixin.png";

            WeiXinPayUtils.saveDonateQrImage2SDCard(qrPath, BitmapFactory.decodeStream(weixinQrIs));
            WeiXinPayUtils.donateViaWeiXin(mActivity, qrPath);
        } else {
            Toast.makeText(mActivity, R.string.donate_remind, Toast.LENGTH_SHORT).show();
        }
    }

    private void donateByAliPay() {
        if(mALSid!=0) {
            String payCode=mActivity.getResources().getString(mALSid);
            Toast.makeText(mActivity, "感谢您的捐赠！٩(๑❛ᴗ❛๑)۶", Toast.LENGTH_SHORT).show();
            if (AliPayUtils.hasInstalledAlipayClient()) {
                AliPayUtils.openALiPay(sMainActivity, payCode);
            }
        }else{
            Toast.makeText(mActivity, R.string.donate_remind, Toast.LENGTH_SHORT).show();
        }
    }
}
