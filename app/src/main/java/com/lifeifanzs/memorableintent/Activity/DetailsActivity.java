package com.lifeifanzs.memorableintent.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.Fragment.DetailsFragment;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.Utils.CalendarUtils;
import com.lifeifanzs.memorableintent.Utils.ThemeUtils;

import java.util.List;
import java.util.UUID;

import static com.lifeifanzs.memorableintent.Fragment.DetailsFragment.CODE_FOR_WRITE_PERMISSION;


public class DetailsActivity extends NoActionBarActivity {

    private static final String EXTRA_MEMORY_ID =
            "com.lifeifanzs.android.memorableintent.memory_id";
    private static final String EXTRA_ISNEW=
            "com.lifeifanzs.android.memorableintent.memory_isNew";

    private Memory mMemory;
    private MemoryLab mMemoryLab;
    private List<Memory> mMemories;//新建数据集
    private String mThemeColor;
    private Theme mTheme;
    private ThemeLab mThemeLab;
    private boolean isNote;
    private Toolbar mEditToolbar;
    private Fragment mFragment;
    private UUID memoryId;

    public static Intent newIntent(Context packageContext, UUID memoryId) {
        Intent intent = new Intent(packageContext, DetailsActivity.class);
        intent.putExtra(EXTRA_MEMORY_ID, memoryId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeLab = ThemeLab.getThemeLab(this);
        mTheme=mThemeLab.getTheme();
        mThemeColor = mTheme.getColor();
        setStatusAndBackgroundColor(mTheme);//设置状态栏颜色

        setContentView(R.layout.activity_memory);//与layout中的配置文件相关联

        memoryId = (UUID) getIntent().getSerializableExtra(EXTRA_MEMORY_ID);
        //获取intent中保存的memoryId

        initEditToolbar();

        mMemoryLab=MemoryLab.get(this);
        mMemory=mMemoryLab.getMemory(memoryId);
        isNote=mMemory.isNote();
//        if(isNote)
//            mMemories=MemoryLab.get(this).getNotes();
//        else
//            mMemories = MemoryLab.get(this).getMemories();//获取数据集
        FragmentManager fm = getSupportFragmentManager();//获取FragmentManager
        mFragment=fm.findFragmentById(R.id.memory_fragment_container);

        if(mFragment==null){
            mFragment= DetailsFragment.newInstance(memoryId);
//            else    fragment=MemoryFragment.newInstance(memoryId);
            fm.beginTransaction()
                    .add(R.id.memory_fragment_container,mFragment)
                    .commit();
        }


//        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
//            @Override
//            public Fragment getItem(int position) {
//                Memory memory = mMemories.get(position);//获取指定位置的memory
//                if(isNote)
//                    return DetailsFragment.newInstance(memory.getId(),isNew);
//                else
//                    return MemoryFragment.newInstance(memory.getId(),isNew);
//                //返回保存有指定memoryId的MemoryFragment实例
//            }
//
//
//            @Override
//            public int getCount() {
//                return mMemories.size();
//            }
//        });

//        for (int i = 0; i < mMemories.size(); i++) {
//            if (mMemories.get(i).getId().equals(memoryId)) {
//                mViewPager.setCurrentItem(i);
//                mMemory=mMemories.get(i);
//                break;
//            }
//        }
    }

    private void initEditToolbar() {
        mEditToolbar=findViewById(R.id.item_toolbar);
        if (!mThemeColor.equals("DISPLAY")) {
            mEditToolbar.setBackgroundColor(Color.
                    parseColor(ThemeUtils.getColor(mThemeColor)));
        } else {
            mEditToolbar.getBackground().mutate().setAlpha(0);//设置为透明
        }

        mEditToolbar.inflateMenu(R.menu.fragment_memory);
        mEditToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                mMemoryLab
                        .updateMemory(mMemory);
            }
        });//设置工具栏的返回监听

        mEditToolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.memory_toolbar_delete:
                        finish();
                        mMemory=mMemoryLab.getMemory(memoryId);
                        if(mMemory.isRemind())  deleteCalendarRemind(mMemory.getTitle());
                        mMemoryLab.deleteMemory(mMemory);
                        return true;
                    case R.id.memory_insert_image:
                        DetailsFragment fragment= (DetailsFragment) mFragment;
                        fragment.startImageActivity();

                        return true;
                }
                return false;
            }
        });

    }

    private void deleteCalendarRemind(String title) {
        CalendarUtils.deleteCalendarEventRemind(this, title
                , mMemory.getDetail(), mMemory.getDate().getTime(), null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMemory!=null){
            CalendarUtils.updateEvent(this,mMemory);
        }
    }
}
