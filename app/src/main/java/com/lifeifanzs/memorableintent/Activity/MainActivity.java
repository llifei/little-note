package com.lifeifanzs.memorableintent.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.Dialog.BootomDialogDemo;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.Fragment.MemoryListFragment;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.Utils.AliPayUtils;

import java.util.Date;
import java.util.List;

import static com.lifeifanzs.memorableintent.Activity.ActivityFactory.sMainActivity;
import static com.lifeifanzs.memorableintent.Fragment.DetailsFragment.CODE_FOR_WRITE_PERMISSION;

public class MainActivity extends NoActionBarActivity {

    private static final int REQUEST_MEDIA_PROJECTION = 0;
    private static final String ARG_ISNOTE="isnote";
    private Theme mTheme;
    private ThemeLab mThemeLab;
    private boolean mStatusIsLight;
    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private Toolbar mMainToolbar;
    private Toolbar mSelectToolbar;

    private boolean mIsDeleteMenu;
    private boolean mIsSelectAll;

    private MenuItem mSelectAllItem;

    private FloatingActionButton mAddButton;

    private boolean mIsNote = true;

    private Fragment mCurrentFragment;
    private MemoryListFragment mMemoryListFragment;
    private MemoryListFragment mNoteListFragment;

    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        checkPermission();//检查权限

        sMainActivity = this;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
        //关联布局文件

        checkFirstUse();//检查是否第一次运行

        AliPayUtils.setApplicationContext(getApplicationContext());
        initAddButton();//初始化添加按钮菜单
        initTheme();//初始化主题
        initToolBar();//初始化ToolBar
        initFragment();

    }



    private void initFragment() {
        Bundle args1=new Bundle();
        args1.putSerializable(ARG_ISNOTE,true);
        mNoteListFragment = new MemoryListFragment();
        mNoteListFragment.setArguments(args1);

        Bundle args2=new Bundle();
        args2.putSerializable(ARG_ISNOTE,false);
        mMemoryListFragment = new MemoryListFragment();
        mMemoryListFragment.setArguments(args2);

        switchFragment(mIsNote ? mNoteListFragment : mMemoryListFragment);
    }

    /**
     * 初始化fragment
     */
    private void switchFragment(Fragment targetFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            transaction.add(R.id.fragment_container, targetFragment);
        } else {
            transaction.hide(mCurrentFragment)
                    .show(targetFragment);
        }
        mCurrentFragment = targetFragment;
        transaction.commit();

//        if(fragment==null) {
//            fragment = new MemoryListFragment(isNote);
//            fm.beginTransaction()
//                    .add(R.id.fragment_container,fragment)
//                    .commit();
//            mCurrentFragment =fragment;
//        }
    }


    /**
     * 初始化添加按钮菜单
     */
    private void initAddButton() {
        mAddButton = findViewById(R.id.add_button);
        setAddButtonOnClickListener();
    }

    /**
     * 初始化ViewPager
     */
//    private void initPager() {
//        mViewPager = findViewById(R.id.List_ViewPager);
//        //获取ViewPager
//
//        mFragmentList = new ArrayList<>();
//        mFragmentList.add(new MemoryListFragment(true));
//        mFragmentList.add(new MemoryListFragment(false));
//        //初始化FragmentList
//
//        FragmentManager fm = getSupportFragmentManager();
//        //得到FragmentManager对象
//
//        mViewPager.setAdapter(new mFragmentAdapter(fm, mFragmentList));
//        //为ViewPager设置Adapter
//        mViewPager.setCurrentItem(mPagerPosition);
//        //设置上次关闭时的页码
//    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        setMainToolBar();//设置ToolBar
        setSelectToolbar();
        mSelectToolbar.setVisibility(View.GONE);
    }

    /**
     * 初始化主题
     */
    private void initTheme() {
        mThemeLab = mThemeLab.getThemeLab(this);
        mTheme = mThemeLab.getTheme();
        if (mTheme == null) {
            mTheme = new Theme();
            mThemeLab.addTheme(mTheme);
        }//初始化Theme类



        mIsNote = mTheme.getPager() == 0;
        mTheme.setStatusColorIsLight(mStatusIsLight);
        setStatusAndBackgroundColor(mTheme);
        //设置状态栏和应用背景主题
    }


//    /**
//     * //设置ViewPager滑动监听
//     */
//    private void setViewPagerListener() {
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                collBack();
//                mPagerPosition = position;
//                setParams(mSelectPoint, position == 1 ? mDistance : -mDistance);
//                for (int i = 0; i < mPointList.size(); i++)
//                    mPointList.get(i).setVisibility(i == position ?
//                            View.GONE : View.VISIBLE);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }

    /**
     * 初始化底部圆点
     */
//    private void initPoints() {
//        mLeftPoint = findViewById(R.id.left_point);
//        mRightPoint = findViewById(R.id.right_point);
//        mSelectPoint = findViewById(R.id.select_point);
//
//        setSelectPointColor(mTheme);
//
//    mDisplay =getWindow().getWindowManager().getDefaultDisplay();
//
//    DisplayMetrics outMetrics = new DisplayMetrics();
//        mDisplay.getMetrics(outMetrics);
//    mWidth =outMetrics.widthPixels /2;

    //
//        mDistance = 25;
//
//        setParams(mLeftPoint, -mDistance);
//        setParams(mRightPoint, mDistance);
//
//        if (mPagerPosition == 0) {
//            mLeftPoint.setVisibility(View.GONE);
//            setParams(mSelectPoint, -mDistance);
//        } else {
//            mRightPoint.setVisibility(View.GONE);
//            setParams(mSelectPoint, mDistance);
//        }
//        mPointList = new ArrayList<>();
//        mPointList.add(mLeftPoint);
//        mPointList.add(mRightPoint);
//    }
//
//    /**
//     * 设置ButtonParams
//     *
//     * @param point
//     * @param distance
//     */
//    private void setParams(Button point, int distance) {
//        LayoutParams pointparams = (LayoutParams) point.getLayoutParams();
//        pointparams.leftMargin = mWidth + distance;
//        point.setLayoutParams(pointparams);
//    }
//
//    /**
//     * 设置激活状态圆点颜色
//     */
//    public void setSelectPointColor(Theme theme) {
//        mSelectPoint.setBackground(getResources().getDrawable
//                (theme.getColor().equals("WHITE")
//                        ? R.drawable.select_blackpoint_shape
//                        : R.drawable.select_whitepoint_shape
//                ));
//    }
    @Override
    protected void onResume() {
        super.onResume();
        mTheme = mThemeLab.getTheme();
        setStatusAndBackgroundColor(mTheme);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTheme.setPager(mIsNote ? 0 : 1);
        mThemeLab.updateTheme(mTheme);

    }

    /**
     * 设置工具栏
     */
    private void setMainToolBar() {
        mMainToolbar = findViewById(R.id.main_toolbar);
        mMainToolbar.inflateMenu(R.menu.fragment__main_activity);
        mMainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.change_mode://切换模式
                        mIsNote = !mIsNote;
                        switchFragment(mIsNote ? mNoteListFragment : mMemoryListFragment);
                        return true;
                    case R.id.set_theme://更改主题
                        Intent themeIntent = ThemeActivity.newIntent(sMainActivity);
                        startActivity(themeIntent);
                        return true;

//                    case R.id.item_search:


                    case R.id.item_select://编辑条目
                        mIsDeleteMenu = true;
                        mIsSelectAll = false;
                        updateFragment();
                        mMainToolbar.setVisibility(View.GONE);
                        mSelectToolbar.setVisibility(View.VISIBLE);
                        mAddButton.setVisibility(View.GONE);
                        return true;

                    case R.id.pay:
//                        if(AliPayUtils.hasInstalledAlipayClient()){
//                            AliPayUtils.openALiPay(sMainActivity);
//                        }
                        BootomDialogDemo dialog = new BootomDialogDemo(sMainActivity);
                        dialog.show();

                    default:
                        return false;
                }
            }
        });
    }


    private void exchangeMode() {

    }


    /**
     * 设置选择删除的ToolBar
     */
    private void setSelectToolbar() {
        mSelectToolbar = findViewById(R.id.main_select_toolbar);
        mSelectToolbar.inflateMenu(R.menu.acticity_select_delete);
        mSelectToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_select_all:
                        mIsSelectAll = !mIsSelectAll;
                        mSelectAllItem = item;
                        updateSelectAllMenuItem();
                        mIsDeleteMenu = true;
                        updateFragment();
                        return true;

                    case R.id.item_select_delete:
                        MemoryListFragment fragment = (MemoryListFragment)
                                mCurrentFragment;
                        fragment.deleteItem();
                        collBack();
                }

                return false;
            }
        });

        mSelectToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collBack();
            }
        });

    }

    public void updateSelectAllMenuItem() {
        if (mSelectAllItem != null) {
            mSelectAllItem.setIcon(mIsSelectAll ? getResources().getDrawable(R.drawable.checked_red)
                    : getResources().getDrawable(R.drawable.unchecked_gray));
        }
    }

    public void setIsSelectAll(boolean isSelectAll) {
        this.mIsSelectAll = isSelectAll;
    }

    /**
     * 取消编辑状态
     */
    public void collBack() {
        mIsDeleteMenu = false;
        mIsSelectAll = false;
        updateFragment();
        updateSelectAllMenuItem();
        updateUI();
        mAddButton.setVisibility(View.VISIBLE);
    }


    /**
     * 更新显示ToolBar
     */
    public void updateUI() {
        mMainToolbar.setVisibility(View.VISIBLE);
        mSelectToolbar.setVisibility(View.GONE);
    }


    /**
     * 更新当前Fragment
     */
    private void updateFragment() {
        MemoryListFragment fragment = (MemoryListFragment)
                mCurrentFragment;
        if (!mIsSelectAll) fragment.getIsSelectMap().clear();
        fragment.updateUIState(mIsDeleteMenu, mIsSelectAll);
    }

    /**
     * 设置添加条目监听
     */
    private void setAddButtonOnClickListener() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memory memory = new Memory();
                memory.setNote(mIsNote);
                MemoryLab.get(sMainActivity).addMemory(memory);
                Intent memoryIntent = DetailsActivity
                        .newIntent(sMainActivity, memory.getId());
                startActivity(memoryIntent);
            }
        });
    }


    private class mFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;

        public mFragmentAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList == null ?
                    null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsDeleteMenu) {
            collBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查设置权限
     */
    private void checkPermission() {
        int hasReadContactsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadCalendarPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int mkdirPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                CODE_FOR_WRITE_PERMISSION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED
                || mkdirPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                    CODE_FOR_WRITE_PERMISSION);
        }
    }

    /**
     * 检查是否第一次运行
     */
    private void checkFirstUse() {
        SharedPreferences setting=getSharedPreferences("FirstRun",0);
        boolean first_run=setting.getBoolean("First",true);
        if(first_run){
            setting.edit().putBoolean("First",false).commit();
            Memory m1=new Memory();
            m1.setZhiding(true);
            m1.setNote(true);
            m1.setZhidingdate(new Date());
            m1.setSign("GREEN");
            m1.setTitle(getResources().getString(R.string.first_memory_title));
//            m1.setDetail(getResources().getString(R.string.first_memory_detail));
            m1.setDetail("备忘录模式与便签模式有一些小区别：\n" +
                    "\n" +
                    "1. 备忘录条目不具有圆点标记，被完成标记代替。\n" +
                    "\n" +
                    "2. 可以设置时间和提醒。\n" +
                    " 进入编辑区 上方左端分别点击日期和时间可 分别进行设置。  条目默认设为不提醒，可点击上方右端 “不提醒” 字样设置为提醒，再次点击可取消提醒。(添加系统日历事件提醒)\n" +
                    "\n" +
                    "3. 其他与便签模式几乎无差。\n");
            Memory m2=new Memory();
            m2.setNote(true);
            m2.setZhiding(true);
            m2.setZhidingdate(new Date());
            m2.setTitle(getResources().getString(R.string.first_note_title));
//            m2.setDetail(getResources().getString(R.string.first_note_detail));

            m2.setDetail("非常感谢您使用这个便签应用，希望它能使您满意！\n" +
                    "下面是便签的一些说明：\n" +
                    "\n" +
                    " (注意：请谨慎删除条目！删除没有确认提示\n" +
                    "  ！)\n" +
                    "\n" +
                    "1. 列表界面右上方第一个按钮可以切换备忘\n" +
                    "录模式。\n" +
                    "\n" +
                    "2. 列表界面菜单可以：\n" +
                    "     更换主题，主题支持自定义图片；\n" +
                    "     多选删除，一键全选。\n" +
                    "\n" +
                    "3. 列表条目左滑可以置顶或删除。置顶后左\n" +
                    "端有条状颜色标记。\n" +
                    "\n" +
                    "4. 编辑时实时保存，无需确认保存，返回即\n" +
                    "可。\n" +
                    "\n" +
                    "5. 便签的标记默认为 小蓝点，可以点击换色\n" +
                    " ，有三种颜色，分别为蓝、绿、紫。\n" +
                    "\n" +
                    "6. 编辑界面可以插入图片。\n" +
                    "\n" +
                    "7. 若便签内容修改，条目更新时间为最后修改时间。\n" +
                    "  \n");
            MemoryLab.get(this).addMemory(m1);
            MemoryLab.get(this).addMemory(m2);
        }
    }
}
