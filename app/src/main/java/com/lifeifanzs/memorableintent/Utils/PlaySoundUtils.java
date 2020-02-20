package com.lifeifanzs.memorableintent.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.SoundPool;

import com.lifeifanzs.memorableintent.R;

public class PlaySoundUtils {


    private static SoundPool soundPool;
    private static int soundID;

    @SuppressLint("NewApi")
    public static void initSound(Activity activity) {
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(activity, R.raw.solved, 1);
    }


    public static void playSound() {
        soundPool.play(
                soundID,
                0.1f,      //左耳道音量【0~1】
                0.5f,      //右耳道音量【0~1】
                0,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }
}
