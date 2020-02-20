package com.lifeifanzs.memorableintent.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.lifeifanzs.memorableintent.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class    TimePickerFragment extends DialogFragment {

    public static final String ARG_TIME = "time";

    public static final String EXTRA_TIME =
            "com.lifeifanzs.memorableintent.time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Date date= (Date) getArguments().getSerializable(ARG_TIME);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mTimePicker.setIs24HourView(true);
                                int hour=mTimePicker.getHour();
                                int minute=mTimePicker.getMinute();
                                String shour=hour+"";
                                String smin=minute+"";
                                if(hour<10) shour=0+shour;
                                if(minute<10) smin=0+smin;
                                sendResult(Activity.RESULT_OK,shour+":"+smin);
                            }
                        })
                .create();
    }

    /**
     * 发送数据
     * @param resultCode
     * @param time
     */
    private void sendResult(int resultCode,String time){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_TIME,time);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
