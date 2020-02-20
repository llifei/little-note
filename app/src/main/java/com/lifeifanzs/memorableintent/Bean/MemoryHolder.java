package com.lifeifanzs.memorableintent.Bean;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lifeifanzs.memorableintent.Activity.DetailsActivity;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.Utils.TimeUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;
import static com.lifeifanzs.memorableintent.Utils.TimeUtils.setMemoryTime;

public class MemoryHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private Memory mMemory;//每一个Holder都有一个实例化对象
    public int mposition;
    private boolean mIsDeleteMenu;
    private boolean mIsSelectAll;
    private List<Integer>mDePolist;

    public CheckBox mSolvedCheckBox;
    public CheckBox mIsDeleteCheckBox;
    private TextView mDateTextView;
    private Button mNoteSign;
    private TextView mTitle;

    public ImageView mDeleteImageView;
    public ImageView mZhidingImageView;
    public View mZhidingSign;
    private ImageView mRemindSign;


    public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item_memory, parent, false));
        //第一个参数是将其与item布局文件相关联

        mSolvedCheckBox = itemView.findViewById(R.id.meno_title);
        mIsDeleteCheckBox = itemView.findViewById(R.id.memory_select_delete);
        mIsDeleteCheckBox.setVisibility(View.GONE);
        mDateTextView = itemView.findViewById(R.id.item_date);
        mTitle =itemView.findViewById(R.id.item_title);

    }

    private void initNoteSign() {
        mNoteSign=itemView.findViewById(R.id.note_sign);
        updateColor();
    }

    private void updateColor(){
        Resources r=sMainActivity.getResources();
        Drawable d=null;
        int color=0;
        switch (mMemory.getSign()){
            case "BLUE":
                d=r.getDrawable(R.drawable.blue_point_shape);
                color=r.getColor(R.color.colorLightBlue);
                break;
            case "GREEN":
                d=r.getDrawable(R.drawable.green_point_shape);
                color=r.getColor(R.color.colorGreen);
                break;
            case "PURPLE":
                d=r.getDrawable(R.drawable.purple_point_shape);
                color=r.getColor(R.color.colorPurple);
                break;
        }
        MemoryLab.get(sMainActivity).updateMemory(mMemory);
        mNoteSign.setBackground(d);
        mZhidingSign.setBackgroundColor(color);
    }

    /**
     * 每次都调用bind(Memory)方法来显示每一个Memory
     *
     * @param memory
     */
    public void bind(Memory memory, boolean isDeleteMenu) {
        boolean isDelete=false;
        mMemory = memory;
        mIsDeleteMenu = isDeleteMenu;

        initZhidingSign();
        initRemndSign();
        initNoteSign();
        setDateTextView();
        initTitle();

        if (mIsDeleteMenu) {//如果点击了编辑（要多选）
            setEditMode();//设置为编辑模式
        }else{
            itemView.setOnClickListener(this);//进入详细Memory的监听
            setCheckBoxAndSign();//设置正常的mTitleCheckbox
        }

        mDeleteImageView =itemView.findViewById(R.id.item_delete);
    }

    //初始化remind alarm sign
    private void initRemndSign() {
        mRemindSign=itemView.findViewById(R.id.item_remind_sign);
        if(!mMemory.isNote()) {
            mRemindSign.setVisibility(mMemory.isRemind()? View.VISIBLE: View.GONE );
        }else{
            mRemindSign.setVisibility(View.GONE);
        }

        mRemindSign.setImageDrawable(mMemory.isSolved()?sMainActivity
                .getResources().getDrawable(R.drawable.alarm_gray)
                :sMainActivity.getResources().getDrawable(R.drawable.alarm));
    }

    //初始化标题
    private void initTitle() {
        mTitle.setText(getTitle());
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setTextColor(sMainActivity.getResources()
                .getColor(mMemory.isSolved()?R.color.colorGray:R.color.colorPrimaryDark));
    }

    //初始化置顶标志
    private void initZhidingSign() {
        mZhidingImageView = itemView.findViewById(R.id.item_zhiding);
//        mZhidingImageView.setText(mMemory.isZhiding()?R.string.
//                item_zhiding_cancel:R.string.item_zhiding);
        mZhidingSign=itemView.findViewById(R.id.item_zhiding_sign);
        mZhidingImageView.setImageDrawable(sMainActivity.getResources()
                        .getDrawable(mMemory.isZhiding()?R.drawable.
                                cancel_zhiding:R.drawable.zhiding));
        mZhidingSign.setVisibility(mMemory.isZhiding()?View.VISIBLE:View.GONE);
    }

    /**
     * 设置编辑模式
     */
    public void setEditMode(){
        mIsDeleteCheckBox.setVisibility(View.VISIBLE);
        mSolvedCheckBox.setVisibility(View.GONE);
        mNoteSign.setVisibility(View.GONE);
    }

    /**
     * 设置时间显示区
     */
    private void setDateTextView(){
        if(!mMemory.isNote()) {
            String memoryTime = setMemoryTime(mMemory);//判断获取设置时间

            mDateTextView.setText(memoryTime);
            int mDateColor = 0;
            Resources r=sMainActivity.getResources();
            if (!mMemory.isSolved()&&mMemory.isOver()) {
                mDateColor = r.getColor(R.color.colorRed);
            } else if (mMemory.isSolved()&&mMemory.isOver()) {
                mDateColor = r.getColor(R.color.colorDeepGreen);
            } else {
                mDateColor = r.getColor(mMemory.isSolved()?R.color.colorGray:
                        R.color.colorLightDark2);
            }
            mDateTextView.setTextColor(mDateColor);
            //设置时间及其颜色
        }else{
            TimeUtils.setToday(new Date());
            String noteTime= TimeUtils.setNoteTime(mMemory);
            mDateTextView.setText(noteTime);
        }
    }

    /**
     * 设置正常的标题显示checkbox
     */
    private void setCheckBoxAndSign() {
        if(mMemory.isNote()) {
            mNoteSign.setVisibility(View.VISIBLE);
            setNoteSignClickListener();
            mSolvedCheckBox.setVisibility(View.GONE);
        }else {
            mNoteSign.setVisibility(View.GONE);
            mSolvedCheckBox.setVisibility(View.VISIBLE);
            if (mMemory.isSolved() != mSolvedCheckBox.isChecked()) {
                mSolvedCheckBox.setChecked(mMemory.isSolved());
            } else {
                mSolvedCheckBox.setChecked(mSolvedCheckBox.isChecked());
            }
        }
    }

    private void setNoteSignClickListener() {
        mNoteSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources r=sMainActivity.getResources();
                Drawable d=null;
                int color=0;
                switch (mMemory.getSign()){
                    case "BLUE":
                        d=r.getDrawable(R.drawable.green_point_shape);
                        color=r.getColor(R.color.colorGreen);
                        mMemory.setSign("GREEN");
                        break;
                    case "GREEN":
                        d=r.getDrawable(R.drawable.purple_point_shape);
                        color=r.getColor(R.color.colorPurple);
                        mMemory.setSign("PURPLE");
                        break;
                    case "PURPLE":
                        d=r.getDrawable(R.drawable.blue_point_shape);
                        color=r.getColor(R.color.colorLightBlue);
                        mMemory.setSign("BLUE");
                        break;
                }
                MemoryLab.get(sMainActivity).updateMemory(mMemory);
                mNoteSign.setBackground(d);
                mZhidingSign.setBackgroundColor(color);
            }
        });
    }


    /**
     * 获取要显示的标题
     *
     * @return
     */
    private String getTitle() {
        String title="";
        if(mMemory.getTitle()!=null&&!mMemory.getTitle().equals(""))
            title= "   " + mMemory.getTitle();
        else if(mMemory.getDetail()!=null&&mMemory.getDetail()!=""){
            title="   " + mMemory.getDetail();
            Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
            Matcher m = p.matcher(title);
            SpannableString ss=new SpannableString(title);

            while (m.find()) {
                String rep=m.group();
                title=title.replace(rep,"[图片]");
            }
        }
        return title;
    }


    /**
     * 设置启动详细MemoryActivity的onClick事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = DetailsActivity.newIntent(sMainActivity, mMemory.getId());
        sMainActivity.startActivity(intent);
        mposition = getAdapterPosition();
    }


}
