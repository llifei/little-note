package com.lifeifanzs.memorableintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lifeifanzs.memorableintent.Activity.ImagePagerActivity;
import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.Fragment.DatePickerFragment;
import com.lifeifanzs.memorableintent.Fragment.TimePickerFragment;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.Utils.CalendarUtils;
import com.lifeifanzs.memorableintent.Utils.CopyFileUtils;
import com.lifeifanzs.memorableintent.Utils.BitmapUtils;
import com.lifeifanzs.memorableintent.Utils.ThemeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DetailsFragment_copy extends Fragment {

    private static final String ARG_MEMORY_ID = "memory_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_IMAGE = 3;
    public static final int CODE_FOR_WRITE_PERMISSION = 123;
    public static final String EXTRA_CAMERA = "camera";
    public static final String EXTRA_IMAGE_ID = "imageid";
    private static final String ARG_ISNEW = "isnew";

    //新建fragment中的各个组件
    private Memory mMemory;
    private EditText mDetailField;
    private TextView mDateTextView;
    private Toolbar mEditToolbar;
    private TextView mTimeTextView;
    private List<ImageButton> mImageButtons;
    private List<Uri> mUris;
    private List<Bitmap> mBitmaps;
    private int mImageCount;
    private int mImageId = 0;
    private int mButtonId;

    private List<File> mPhotoFileList;
    private List<String> mImages;

    private Theme mTheme;
    private ThemeLab mThemeLab;
    private String mThemeColor;

    private Button mNoteSign;
    private EditText mTitleField;
    private CheckBox mSolvedCheckBox;
    private boolean mIsOver;

    private TextView mRemindTextView;
    private ImageView mAlarmView;
    private long mStartTime;

//    /**
//     * 被Activity调用创建可以保存UUID的fragment
//     *
//     * @param memoryId
//     * @return
//     */
//    public static DetailsFragment_copy newInstance(UUID memoryId) {
//        Bundle args = new Bundle();//键值对对象
//        args.putSerializable(ARG_MEMORY_ID, memoryId);//添加价值对
//
//        DetailsFragment_copy fragment = new DetailsFragment_copy();//新建Fragment
//        fragment.setArguments(args);//为Fragment设置argument
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        UUID memoryId = (UUID) getArguments().getSerializable(ARG_MEMORY_ID);
//        //获取Activity的intent中保存的memoryId
//
//
//        mMemory = MemoryLab.get(getActivity()).getMemory(memoryId);
//        //通过id获得指定memory
//
////        try {
////            mMemory= (Memory) mOldMemory.clone();
////        } catch (CloneNotSupportedException e) {
////            e.printStackTrace();
////        }
//
//        mThemeLab = ThemeLab.getThemeLab(getActivity());
//        mTheme = mThemeLab.getTheme();
//        mThemeColor = mTheme.getColor();
//
//        mImageCount = mMemory.getCount();//获取图片数量
//        mBitmaps = new ArrayList<>();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        MemoryLab.get(getActivity())
//                .updateMemory(mMemory);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!mThemeColor.equals("DISPLAY")) {
//            mEditToolbar.setBackgroundColor(Color.
//                    parseColor(ThemeUtils.getColor(mThemeColor)));
//        } else {
//            mEditToolbar.getBackground().mutate().setAlpha(0);//设置为透明
//        }
//        mImageCount = mMemory.getCount();//获取图片数量
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        //根据 fragment_memory.xml文件 新建视图
//        View v = inflater.inflate(mMemory.isNote() ? R.layout.fragment_note
//                : R.layout.fragment_memory, container, false);
//
//        initToolBar(v);
//        initImage(v);
//        initTextViews(v);
//
//
//        if (mMemory.isNote()) {
//            initNoteView(v);
//        } else {
//            initMemoryViews(v);
//        }
//
//
//        return v;
//    }
//
//    private void initTextViews(View v) {
//        mTitleField = v.findViewById(R.id.meno_title);//根据id获取文本区组件
//        mTitleField.setText(mMemory.getTitle());//设置标题
//
//        mDetailField = v.findViewById(R.id.item_detail);//获取事件内容编辑区
//        mDetailField.setText(mMemory.getDetail());//设置内容
//        setViewListener();//设置编辑区域监听
//
//        mDateTextView = v.findViewById(R.id.item_date);//获取日期显示区
//        mTimeTextView = v.findViewById(R.id.item_time);//获取时间显示区
//    }
//
//    private void initMemoryTime() {
//        mDateTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fm = getFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment
//                        .newInstance(mMemory.getDate());
//                dialog.setTargetFragment(DetailsFragment_copy.this, REQUEST_DATE);
//                dialog.show(fm, DIALOG_DATE);
//            }
//        });//为日期设置可弹出窗口的监听
//
//        mTimeTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fm = getFragmentManager();
//                TimePickerFragment dialog = TimePickerFragment
//                        .newInstance(mMemory.getDate());
//                dialog.setTargetFragment(DetailsFragment_copy.this, REQUEST_TIME);
//                dialog.show(fm, DIALOG_TIME);
//            }
//        });
//    }
//
//    private void setMemoryTime() {
//        updateItemTime();//更新mMemory时间
//        String date = getMemoryDate(mMemory.getDate());
//        mDateTextView.setText(date);
//        mTimeTextView.setText(mMemory.getTime());
//        mMemory.setOver(mIsOver);
//        updateTimeColor();//更新时间显示颜色
//    }
//
//    //更新mMemory时间
//    private void updateItemTime() {
//        Date date = mMemory.getDate();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        String time = mMemory.getTime();
//        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
//        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
//
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        mMemory.setDate(calendar.getTime());
//    }
//
//    /**
//     * 获取 xx月xx日
//     *
//     * @param date
//     * @return
//     */
//    private String getMemoryDate(Date date) {
//
//        Date td = new Date();
//        Calendar calendar = Calendar.getInstance();
//        Calendar tcalendar = Calendar.getInstance();
//        calendar.setTime(date);
//        tcalendar.setTime(td);
//        int memoryYear = calendar.get(Calendar.YEAR);
//        int todayYear = tcalendar.get(Calendar.YEAR);
//        int memoryMonth = calendar.get(Calendar.MONTH);
//        int todayMonth = tcalendar.get(Calendar.MONTH);
//        int memoryDay = calendar.get(Calendar.DAY_OF_MONTH);
//        int todayDay = tcalendar.get(Calendar.DAY_OF_MONTH);
//        mIsOver = (memoryYear < todayYear
//                || ((memoryYear == todayYear && memoryMonth == todayMonth) && (memoryDay < todayDay))
//                || (memoryYear == todayYear && memoryMonth < todayMonth));
//        if (calendar.get(Calendar.YEAR) != tcalendar.get(Calendar.YEAR)) {
//            return (String) DateFormat.format("yyyy年MM月dd日", date);
//        } else {
//            return (String) DateFormat.format("MM月dd日", date);
//        }
//    }
//
//    //更新时间显示颜色
//    private void updateTimeColor() {
//        Resources r = getActivity().getResources();
//        int color = 0;
//        if (mMemory.isOver()) {
//            color = r.getColor(mMemory.isSolved() ?
//                    R.color.colorDeepGreen : R.color.colorRed
//            );
//        } else {
//            color = r.getColor(R.color.colorPrimaryDark);
//        }
//        mDateTextView.setTextColor(color);
//        mTimeTextView.setTextColor(color);
//    }
//
//    private void initMemoryViews(View v) {
//        //初始化各个组件
//        mSolvedCheckBox = v.findViewById(R.id.solved_checkbox);//获取CheckBox组件
//        mSolvedCheckBox.setChecked(mMemory.isSolved());//设置解决状态
//        mRemindTextView = v.findViewById(R.id.memory_remind);
//        mAlarmView = v.findViewById(R.id.memory_alarm);
//
//        setMemoryTime();//设置MemoryTime
//        setSolvedListener();//设置SolvedCheckBox监听
//        initMemoryTime();//初始化时间显示
//        initRemindView();//初始化显示 提醒sign
//
//    }
//
//    //初始化显示 提醒sign
//    private void initRemindView() {
//        updateRemindView();
//        if (mMemory.isRemind()) addCalendarRemind();
//        setRemindClickListener();
//    }
//
//    //更新 提醒sign
//    private void updateRemindView() {
//        mAlarmView.setVisibility(mMemory.isRemind() ? View.VISIBLE : View.GONE);
//        mRemindTextView.setText(mMemory.isRemind() ? R.string.memory_remind
//                : R.string.memory_noremind);
//    }
//
//    //设置RemindView点击监听
//    private void setRemindClickListener() {
//        mRemindTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setRemindClick();
//            }
//        });
//        mAlarmView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setRemindClick();
//            }
//        });
//    }
//
//
//    //设置RemindView点击监听的事件
//    private void setRemindClick() {
//        mMemory.setRemind(!mMemory.isRemind());
//        if (mMemory.isRemind()) addCalendarRemind();
//        else deleteCalendarRemind(mStartTime);
//        updateRemindView();
//    }
//
//    private void setSolvedListener() {
//        //为其设置监听器
//        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                mMemory.setSolved(isChecked);
//                updateTimeColor();//更新时间显示颜色
//            }
//        });
//    }
//
//
//    private void setViewListener() {
//        final List<String> linshiTitle = new ArrayList<>();
//        final List<String> linshiDetail = new ArrayList<>();
//        //为其设置监听器
//        mTitleField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                linshiTitle.clear();
//                linshiTitle.add(charSequence.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //将用户输入的信息设为标题
//                mMemory.setTitle(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                System.out.println(linshiTitle.get(0));
//                System.out.println(!mMemory.getTitle().equals(linshiTitle.get(0)) && mMemory.isRemind());
//                if (!mMemory.getTitle().equals(linshiTitle.get(0)) && mMemory.isRemind()) {
//                    deleteCalendarRemind(linshiTitle.get(0));
//                    addCalendarRemind();
//                }
//            }
//        });
//
//        mDetailField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                linshiDetail.clear();
//                linshiDetail.add(charSequence.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                mMemory.setDetail(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(!linshiDetail.get(0).equals(mMemory.getDetail())&&mMemory.isRemind()){
//                    deleteCalendarRemind(mMemory.getTitle());
//                    addCalendarRemind();
//                }
//            }
//        });
//
//
//    }
//
//    private void initImage(View v) {
//        mImageButtons = new ArrayList<>();
//        mImageButtons.add((ImageButton) v.findViewById(R.id.camera_imageButton1));
//        mImageButtons.add((ImageButton) v.findViewById(R.id.camera_imageButton2));
//        mImageButtons.add((ImageButton) v.findViewById(R.id.camera_imageButton3));
//        for (int i = 0; i < mImageButtons.size(); i++) {
//            mImageButtons.get(i).setBackgroundColor(Color.
//                    parseColor(ThemeUtils.getColor(mThemeColor)));//设置按钮背景色
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mImageButtons.get(i).setElevation(5);//设置边框线
//            }
//
//            mImageButtons.get(i).setVisibility(View.GONE);
//        }
//
//        mPhotoFileList = new ArrayList<>();
//        mPhotoFileList.add(MemoryLab.get(getActivity()).getPhotoFile(mMemory, mImageId));
//
//
//        mUris = new ArrayList<>();
//        mImages = new ArrayList<>();
//        for (int i = 0; i < mImageCount; i++) {
//            mImageId = i;
//            String uristr = mMemory.getUris().get(i);
//            String imagestr = mMemory.getImages().get(i);
//            if (uristr != null) {
//                Uri uri = Uri.parse(uristr);
//                mUris.add(uri);
//            }
//            if (imagestr != null)
//                mImages.add(imagestr);
//            //获取照片文件位置
//            updatePhotoView();
//        }
//
//    }
//
//    private void initToolBar(View v) {
//        mEditToolbar = v.findViewById(mMemory.isNote() ?
//                R.id.item_toolbar : R.id.item_toolbar);//获取toolbar
//        mEditToolbar.inflateMenu(R.menu.fragment_memory);
//        mEditToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().finish();
//                MemoryLab.get(getActivity())
//                        .updateMemory(mMemory);
//            }
//        });//设置工具栏的返回监听
//
//        mEditToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.memory_toolbar_delete:
//                        getActivity().finish();
//                        MemoryLab.get(getActivity()).deleteMemory(mMemory);
//                        return true;
//                    case R.id.memory_insert_image:
//                        if (mImageCount < 3) {
//                            startImageActivity(mImageCount);
//                        } else {
//                            Toast.makeText(getActivity(), "目前只支持插入3张图片", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                        return true;
//                }
//                return false;
//            }
//        });
//
//    }
//
//    private void initNoteView(View v) {
//        setNoteTime();//设置时间
//        updateItemTime();//更新mMemory时间
//        initNoteSign(v);//初始化NoteSign
//    }
//
//    private void initNoteSign(View v) {
//        mNoteSign = v.findViewById(R.id.note_set_sign);
//        Drawable mdraw = null;
//        switch (mMemory.getSign()) {
//            case "BLUE":
//                mdraw = getResources().getDrawable(R.drawable.blue_point_shape);
//                break;
//            case "RED":
//                mdraw = getResources().getDrawable(R.drawable.purple_point_shape);
//                break;
//            case "ORANGE":
//                mdraw = getResources().getDrawable(R.drawable.green_point_shape);
//                break;
//        }
//        mNoteSign.setBackground(mdraw);
//
//        mNoteSign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Drawable d = null;
//                switch (mMemory.getSign()) {
//                    case "BLUE":
//                        d = getResources().getDrawable(R.drawable.green_point_shape);
//                        mMemory.setSign("ORANGE");
//                        break;
//                    case "ORANGE":
//                        d = getResources().getDrawable(R.drawable.purple_point_shape);
//                        mMemory.setSign("RED");
//                        break;
//                    case "RED":
//                        d = getResources().getDrawable(R.drawable.blue_point_shape);
//                        mMemory.setSign("BLUE");
//                        break;
//                }
//                mNoteSign.setBackground(d);
//            }
//        });
//
//    }
//
//    /**
//     * 更新显示照片
//     */
//    private void updatePhotoView() {
//        ImageButton mPhotoImageButton = mImageButtons.get(mImageId);
//        final File mPhotoFile = mPhotoFileList.get(mImageId);
//        if (mPhotoImageButton != null) {   //判断imageButton是否为空
//            if (mPhotoFile == null || !mPhotoFile.exists()) {   //如果对应文件为null或不存在
//                if (mUris.get(mImageId) != null) {//如果该位置存在Uri
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = BitmapFactory.decodeStream(getActivity()
//                                .getContentResolver()
//                                .openInputStream(Uri.parse(mImages.get(mImageId))));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    mBitmaps.add(bitmap);
//                } else {//如果既不存在Uri，也不存在照片文件
//                    mPhotoImageButton.setImageDrawable(getResources().getDrawable(R.drawable.camera));
//                }
//            } else {        //若照片文件存在
//                Bitmap bitmap = BitmapUtils.getScaledBitmap(
//                        mPhotoFile.getPath(), getActivity());//根据文件路径新建位图对象
//                mBitmaps.add(bitmap);
//            }
//            setBitmapAndImageOnClick(mImageId);
//        }
//
//        //获取相机按钮对象
//
//        setVisibility(0, 0);
//        setVisibility(1, 1);
//        setVisibility(2, 2);
//
//    }
//
//    /**
//     * 设置bitmap和ImageDialog以及更新imageCount和mMemory.count
//     *
//     * @param mImageId
//     */
//    private void setBitmapAndImageOnClick(final int mImageId) {
//        final ImageButton mPhotoImageButton = mImageButtons.get(mImageId);//获取指定位置按钮
//        if (mBitmaps.size() > mImageId) {
//            mPhotoImageButton.setImageBitmap(mBitmaps.get(mImageId));//为imagebutton设置填充图片对象
//
//            mPhotoImageButton.setOnClickListener(new View.OnClickListener() {//此时设置可查看图片的监听器
//                @Override
//                public void onClick(View view) {
//
//                    mButtonId = mImageButtons.indexOf(mPhotoImageButton);//获取按钮的id
//                    String imageStr = mImages.get(mImageId);//获取图片uri或文件路径字符串
//                    Intent intent = ImagePagerActivity.newIntent(getActivity()
//                            , mButtonId, mImageCount, mMemory.getId());
//                    //设置intent，并传入参数
//                    startActivityForResult(intent, REQUEST_IMAGE);
//                    //启动intent关联的ImageViewActivity
//                }
//            });
//        }
//    }
//
//    /**
//     * 根据图片个数设置后面两个imageButton可见性与可用性
//     *
//     * @param imageCount
//     * @param imageId
//     */
//    private void setVisibility(int imageCount, int imageId) {
//        ImageButton photoImageButton = mImageButtons.get(imageId);//获取ImageButton
//        photoImageButton.setVisibility(mImageCount > imageCount ? View.VISIBLE : View.GONE);
//        //如果实际图片数量大于指定图片数量，则设其为可见，否则设为不可见
//
//        if (mImageCount >= imageCount) {//如果其为可见
//            if (mPhotoFileList.size() == imageId)
//                //如果照片文件列表长度与该ImageButton的id（imageid）相等
//                // 则文件列表增加一个以该id为参数新建的文件
//                mPhotoFileList.add(MemoryLab.get(getActivity()).getPhotoFile(mMemory, imageId));
//        }
//    }
//
//    /**
//     * 设置时间
//     */
//    private void setNoteTime() {
//        String date = getNoteDate(mMemory.getDate());
//        mDateTextView.setText(date);
//        mTimeTextView.setText(mMemory.getTime());
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            return;
//        }
//        if (requestCode == REQUEST_DATE) {
//            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
//            mMemory.setDate(date);
//            mDateTextView.setText(getNoteDate(date));
//            updateRemind();//更新日历提醒事件
//
//        } else if (requestCode == REQUEST_TIME) {
//            String time = (String) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
//            mTimeTextView.setText(time);
//            mMemory.setTime(time);
//            updateRemind();//更新日历提醒事件
//
//        } else if (requestCode == REQUEST_CAMERA) {
//            Uri uri = FileProvider.getUriForFile(getActivity(),
//                    "com.lifeifanzs.memorableintent.fileprovider",
//                    mPhotoFileList.get(mImageId));
//
//            getActivity().revokeUriPermission(uri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            //相机已经保存文件，就再次调用权限关闭文件的访问
//
//            Uri mUri = null;
//            String imagestr;
//            if (null != data) { //如果intent中保存有数据，即打开的是相册
//                mUri = data.getData();//获取数据即Uri
//                mMemory.setUriStr(mImageId, mUri.toString());//保存Uri到模型层
//                imagestr = mUri.toString();
//            } else {
//                imagestr = mPhotoFileList.get(mImageId).toString();
//            }
//
//            mMemory.setImageStr(mImageId, imagestr);//保存imagestr到模型层
//            mImages.add(imagestr);//图片列表中保存imagestr
//            mImageCount++;//图片数量+1
//            mMemory.setCount(mImageCount);//保存图片数量到模型层
//            mUris.add(mUri);//mUri列表中添加mUri（打开相机时为null）
//            updatePhotoView();//更新视图
//
//        } else if (requestCode == REQUEST_IMAGE) {
//
//
//            mButtonId = Integer.parseInt(data.getStringExtra(EXTRA_IMAGE_ID));
//            //获取删除位置
//            deleteImage(mButtonId);
//            //删除图片
//        }
//
//    }
//
//    //更新日历提醒事件
//    private void updateRemind() {
//        setMemoryTime();
//        if (mMemory.isRemind()) {
//            deleteCalendarRemind(mStartTime);
//            addCalendarRemind();
//        }
//    }
//
//    /**
//     * 删除指定位置图片
//     *
//     * @param deleteposition
//     */
//    private void deleteImage(int deleteposition) {
//        mButtonId = deleteposition;
//        if (mUris.size() > mButtonId) { //如果该位置是uri
//            mUris.remove(mButtonId);    //从uri列表中移除
////                mMemory.getUris().set(mButtonId, null);
//        }
//
//
//        try {
//            movePhoto();    //移动照片
//        } catch (Exception e) {
//        } finally {
//
//            for (int i = mButtonId; i < mImages.size() - 1; i++) {
//                //从删除位置开始循环到图片总数量-1
//
//                File photoFile = new File(mPhotoFileList.get(i).toString());
//                //获取每个位置的照片文件
//
//                if (photoFile != null && photoFile.exists()) {//如果该文件存在
//                    mImages.set(i, photoFile.toString());//把mImages中i位置设为该文件路径
//                    mMemory.setImageStr(i, photoFile.toString());//同步到模型层
//                } else {
//                    mImages.set(i, mImages.get(i + 1));//否则，把mImages中i+1提前到i
//                    mMemory.setImageStr(i, mImages.get(i + 1));//同步到模型层
//                }
//            }
//            mImages.remove(mImages.size() - 1);//移除mImages中最后一个元素
//            mMemory.setImageStr(mImages.size(), null);//同步到模型层
//
//
//            mBitmaps.remove(mButtonId);//mBitmaps中移除删除位置元素
//            mImageCount--;//图片总数量-1
//            mMemory.setCount(mImageCount);//同步数量到模型层
//            for (int i = 0; i <= mImageCount; i++) {//为每一个视图设置Bitmap
//                setBitmapAndImageOnClick(i);
//                setVisibility(0, 0);
//                setVisibility(1, 1);
//                setVisibility(2, 2);
//            }
//        }
//    }
//
//
//    /**
//     * 移动照片 从删除位置开始往后看，如果有照片，就把它复制为前一个照片文件
//     * 删除最后一个照片文件的内容
//     */
//    private void movePhoto() {
//        int Photocount = 0;
//        int begin = 99;
//        for (int j = 0; j < mPhotoFileList.size(); j++) {
//            if (mPhotoFileList.get(j) != null
//                    && mPhotoFileList.get(j).exists()
//            ) {
//                Photocount++;
//            }
//        }
//
//        for (int k = mButtonId; k < mPhotoFileList.size(); k++) {
//            if (mPhotoFileList.get(k) != null
//                    && mPhotoFileList.get(k).exists()
//            ) {
//                begin = k < begin ? k : begin;
//            }
//        }
//
//        if (begin == mButtonId)
//            mPhotoFileList.get(mButtonId).delete();
//
//        if (mPhotoFileList.get(mButtonId) != null
//                && mPhotoFileList.get(mButtonId).exists()) {
//            try {
//                mPhotoFileList.get(mButtonId).createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (Photocount > 0) {
//            for (int i = begin - 1; i < begin + Photocount - 1; i++) {
//                try {
//                    if (mPhotoFileList.get(i + 1) != null
//                            && mPhotoFileList.get(i + 1).exists()
//                    ) {
//                        mPhotoFileList.get(i).delete();
//                        mPhotoFileList.get(i).createNewFile();
//                        CopyFileUtils.copyFile(mPhotoFileList
//                                .get(i + 1), mPhotoFileList.get(i));
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            mPhotoFileList.get(begin + Photocount - 1).delete();
//        }
//    }
//
//    /**
//     * 启动相机或相册
//     *
//     * @param imageId
//     */
//    private void startImageActivity(int imageId) {
//        final Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);//创建选择器Intent
//        Intent albumIntent = new Intent(Intent.ACTION_PICK);//创建相册Intent
//        albumIntent.setDataAndType(MediaStore.Images
//                .Media.EXTERNAL_CONTENT_URI, "image/*");//设置数据和文件类型
//        chooserIntent.putExtra(Intent.EXTRA_INTENT, albumIntent);
//
//        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //创建相机Intent
//        PackageManager packageManager = getActivity().getPackageManager();
//        //创建得到PackageManager对象
//        final boolean canTakePhoto = mPhotoFileList.get(imageId) != null &&
//                captureIntent.resolveActivity(packageManager) != null;
//        //判断相机Intent是否可用
//
//        mImageId = imageId;
//
//        if (canTakePhoto) {//判断相机Intent是否可用
//            Uri uri = FileProvider.getUriForFile(getActivity(),
//                    "com.lifeifanzs.memorableintent.fileprovider", mPhotoFileList.get(mImageId));
//            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//            List<ResolveInfo> cameraActivities = getActivity()
//                    .getPackageManager().queryIntentActivities(captureIntent,
//                            PackageManager.MATCH_DEFAULT_ONLY);
//
//            for (ResolveInfo activity : cameraActivities) {
//                getActivity().grantUriPermission(activity.activityInfo.packageName,
//                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }
//
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
//        }
//        startActivityForResult(chooserIntent, REQUEST_CAMERA);
//    }
//
//
//    private String getNoteDate(Date date) {
//        Date td = new Date();
//        Calendar calendar = Calendar.getInstance();
//        Calendar tcalendar = Calendar.getInstance();
//        calendar.setTime(date);
//        tcalendar.setTime(td);
//        if (calendar.get(Calendar.YEAR) != tcalendar.get(Calendar.YEAR)) {
//            return (String) DateFormat.format("yyyy年MM月dd日", date);
//        } else {
//            return (String) DateFormat.format("MM月dd日", date);
//        }
//    }
//
//    /**
//     * 删除日历提醒事件
//     *
//     * @param startTime
//     */
//    private void deleteCalendarRemind(long startTime) {
//        CalendarUtils.deleteCalendarEventRemind(getActivity(), mMemory.getTitle()
//                , mMemory.getDetail(), startTime, null);
//    }
//
//    private void deleteCalendarRemind(String title) {
//        CalendarUtils.deleteCalendarEventRemind(getActivity(), title
//                , mMemory.getDetail(), mMemory.getDate().getTime(), null);
//    }
//
//    /**
//     * 添加日历提醒事件
//     */
//    private void addCalendarRemind() {
//        mStartTime = mMemory.getDate().getTime();
//        CalendarUtils.addCalendarEventRemind(getActivity(), mMemory.getTitle()
//                , mMemory.getDetail(), mStartTime, mStartTime
//                        + 60000, 5, null);
//    }
}