package com.lifeifanzs.memorableintent.Bean;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Memory implements Cloneable {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDetail;
    private String mTime;
    private boolean mSolved = false;
    private String mSign="BLUE";
    private boolean isNote;
    private boolean mOver;
    private boolean mZhiding;
    private Date mZhidingdate;
    private boolean mRemind;



    public boolean isRemind() {
        return mRemind;
    }

    public void setRemind(boolean remind) {
        mRemind = remind;
    }

    public Date getZhidingdate() {
        return mZhidingdate;
    }

    public void setZhidingdate(Date zhidingdate) {
        mZhidingdate = zhidingdate;
    }

    public boolean isZhiding() {
        return mZhiding;
    }

    public void setZhiding(boolean zhiding) {
        mZhiding = zhiding;
    }

    public String getSign() {
        return mSign;
    }

    public void setSign(String sign) {
        mSign = sign;
    }

    public boolean isOver() {
        return mOver;
    }

    public void setOver(boolean over) {
        mOver = over;
    }


    public boolean isNote() {
        return isNote;
    }

    public void setNote(boolean note) {
        isNote = note;
    }



    public Memory() {
        this(UUID.randomUUID());
    }

    public Memory(UUID id) {
        mId = id;
        mDate = new Date();

        String time = (String) DateFormat.format("HH:mm"
                , mDate);
        setTime(time);
    }


    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFilename(int id) {
        return "IMG_" + getId().toString() + id + ".jpg";
    }

    public void setId(UUID id) {
        mId = id;
    }


    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
