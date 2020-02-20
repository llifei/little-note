package com.lifeifanzs.memorableintent.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.lifeifanzs.memorableintent.R;


public abstract class BootomDialog extends Dialog {
    public BootomDialog(Context context){
        super(context);
        init();
    }

    protected void init() {
        Window win = this.getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);
        onCreate();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.BottomInAndOutStyle;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
//        win.setBackgroundDrawableResource(android.R.color.transparent);
    }
    protected abstract void onCreate();

}
