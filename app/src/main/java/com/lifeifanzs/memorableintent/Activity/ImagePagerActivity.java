package com.lifeifanzs.memorableintent.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.Fragment.ShowImageFragment;
import com.lifeifanzs.memorableintent.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImagePagerActivity extends AppCompatActivity {

    private static final String EXTRA_FILE_BITMAP =
            "com.lifeifanzs.memorableintent.file_bitmap";
    private static final String EXTRA_IMAGE_BITMAP =
            "com.lifeifanzs.memorableintent.image_bitmap";
    private static final String EXTRA_IMAGE_ID =
            "com.lifeifanzs.memorableintent.image_id";
    private static final String EXTRA_IMAGE_COUNT =
            "com.lifeifanzs.memorableintent.image_count";
    private static final String EXTRA_MEMORY_ID =
            "com.lifeifanzs.memorableintent.memory_id";

    private ViewPager mViewPager;
    private Memory mMemory;
    private List<String> mImages;


    public static Intent newIntent(Context packageContext
            , int imageId, int imageCount, UUID memoryId) {
        Intent intent = new Intent(packageContext, ImagePagerActivity.class);
//        intent.putExtra(EXTRA_IMAGE_BITMAP, imageStr);
        intent.putExtra(EXTRA_MEMORY_ID, memoryId);
        intent.putExtra(EXTRA_IMAGE_ID, imageId);
        intent.putExtra(EXTRA_IMAGE_COUNT, imageCount);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置全屏显示（隐藏状态栏）

        setContentView(R.layout.activity_showimage_pager);//绑定布局文件

        mViewPager = findViewById(R.id.image_view_pager);

//        final String imageStr = (String) getIntent().getSerializableExtra(EXTRA_IMAGE_BITMAP);
        final int imageId = (int) getIntent().getSerializableExtra(EXTRA_IMAGE_ID);
        final int imageCount = (int) getIntent().getSerializableExtra(EXTRA_IMAGE_COUNT);

//        UUID memoryId = (UUID) getIntent().getSerializableExtra(EXTRA_MEMORY_ID);
//        mMemory = MemoryLab.get(this).getMemory(memoryId);
//        mImages = new ArrayList<>();
//        for (int i = 0; i < imageCount; i++) {
//            String image = mMemory.getImages().get(i);
//            if (image != null) {
//                mImages.add(image);
//            }
//        }


        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return imageCount;
            }


            int begin=imageId;
            int rightPosition=begin;
            boolean beginIsRight;
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return ShowImageFragment.newInstance(
                        position, imageCount, mImages.get(position));
            }

        });

        mViewPager.setCurrentItem(imageId);

    }
}
