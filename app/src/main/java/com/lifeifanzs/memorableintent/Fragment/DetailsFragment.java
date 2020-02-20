package com.lifeifanzs.memorableintent.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.R;
import com.lifeifanzs.memorableintent.SQLite.MemoryLab;
import com.lifeifanzs.memorableintent.SQLite.ThemeLab;
import com.lifeifanzs.memorableintent.Utils.CalendarUtils;
import com.lifeifanzs.memorableintent.Utils.BitmapUtils;
import com.lifeifanzs.memorableintent.Utils.CopyFileUtils;
import com.lifeifanzs.memorableintent.Utils.MiuiAlbumUtils;
import com.lifeifanzs.memorableintent.Utils.PlaySoundUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsFragment extends Fragment {

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
    private TextView mTimeTextView;
    private List<ImageButton> mImageButtons;
    private LinkedList<String> mAlbumPaths;
    private LinkedList<Bitmap> mBitmaps;
    private int mImageCount;
    private int mPictureCount;
    private int mImageId = 0;
    private int mButtonId;

    private LinkedList<File> mPhotoFileList;
    private LinkedList<String> mImages;

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
    private List<Integer> mDelPicPos;
    private Memory mOldMemory;

    /**
     * 被Activity调用创建可以保存UUID的fragment
     *
     * @param memoryId
     * @return
     */
    public static DetailsFragment newInstance(UUID memoryId) {
        Bundle args = new Bundle();//键值对对象
        args.putSerializable(ARG_MEMORY_ID, memoryId);//添加价值对

        DetailsFragment fragment = new DetailsFragment();//新建Fragment
        fragment.setArguments(args);//为Fragment设置argument
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID memoryId = (UUID) getArguments().getSerializable(ARG_MEMORY_ID);
        //获取Activity的intent中保存的memoryId


        mMemory = MemoryLab.get(getActivity()).getMemory(memoryId);
        try {
            mOldMemory = (Memory) mMemory.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //通过id获得指定memory


        mThemeLab = ThemeLab.getThemeLab(getActivity());
        mTheme = mThemeLab.getTheme();
        mThemeColor = mTheme.getColor();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMemory.isNote() && mOldMemory.getDetail() != null && mMemory.getDetail() != null
                && mOldMemory.getTitle() != null && mMemory.getTitle() != null) {
            if (!mOldMemory.getDetail().equals(mMemory.getDetail()) ||
                    !mOldMemory.getTitle().equals(mMemory.getTitle())) {
                Date d = new Date();
                mMemory.setDate(d);
                mMemory.setTime((String) DateFormat.format("HH:mm", d));
            }
        }
        MemoryLab.get(getActivity()).updateMemory(mMemory);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //根据 fragment_memory.xml文件 新建视图
        View v = inflater.inflate(mMemory.isNote() ? R.layout.fragment_note
                : R.layout.fragment_memory, container, false);

        initTextViews(v);


        if (mMemory.isNote()) {
            initNoteView(v);
        } else {
            initMemoryViews(v);
        }


        return v;
    }

    private void initTextViews(View v) {
        mTitleField = v.findViewById(R.id.meno_title);//根据id获取文本区组件
        mTitleField.setText(mMemory.getTitle());//设置标题

        mDetailField = v.findViewById(R.id.item_detail);//获取事件内容编辑区
        mDetailField.setText(mMemory.getDetail());//设置内容
        setViewListener();//设置编辑区域监听

        loadImage();


        mDateTextView = v.findViewById(R.id.item_date);//获取日期显示区
        mTimeTextView = v.findViewById(R.id.item_time);//获取时间显示区
    }

    private void initMemoryTime() {
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mMemory.getDate());
                dialog.setTargetFragment(DetailsFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });//为日期设置可弹出窗口的监听

        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mMemory.getDate());
                dialog.setTargetFragment(DetailsFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });
    }

    private void setMemoryTime() {
        updateItemTime();//更新mMemory时间
        String date = getMemoryDate(mMemory.getDate());
        mDateTextView.setText(date);
        mTimeTextView.setText(mMemory.getTime());
        mMemory.setOver(mIsOver);
        updateTimeColor();//更新时间显示颜色
    }

    //更新mMemory时间
    private void updateItemTime() {
        Date date = mMemory.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String time = mMemory.getTime();
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        mMemory.setDate(calendar.getTime());
    }

    /**
     * 获取 xx月xx日
     *
     * @param date
     * @return
     */
    private String getMemoryDate(Date date) {

        Date td = new Date();
        Calendar calendar = Calendar.getInstance();
        Calendar tcalendar = Calendar.getInstance();
        calendar.setTime(date);
        tcalendar.setTime(td);
        int memoryYear = calendar.get(Calendar.YEAR);
        int todayYear = tcalendar.get(Calendar.YEAR);
        int memoryMonth = calendar.get(Calendar.MONTH);
        int todayMonth = tcalendar.get(Calendar.MONTH);
        int memoryDay = calendar.get(Calendar.DAY_OF_MONTH);
        int todayDay = tcalendar.get(Calendar.DAY_OF_MONTH);
        mIsOver = (memoryYear < todayYear
                || ((memoryYear == todayYear && memoryMonth == todayMonth) && (memoryDay < todayDay))
                || (memoryYear == todayYear && memoryMonth < todayMonth));
        if (calendar.get(Calendar.YEAR) != tcalendar.get(Calendar.YEAR)) {
            return (String) DateFormat.format("yyyy年MM月dd日", date);
        } else {
            return (String) DateFormat.format("MM月dd日", date);
        }
    }

    //更新时间显示颜色
    private void updateTimeColor() {
        Resources r = getActivity().getResources();
        int color = 0;
        if (mMemory.isOver()) {
            color = r.getColor(mMemory.isSolved() ?
                    R.color.colorDeepGreen : R.color.colorRed
            );
        } else {
            color = r.getColor(R.color.colorPrimaryDark);
        }
        mDateTextView.setTextColor(color);
        mTimeTextView.setTextColor(color);
    }

    private void initMemoryViews(View v) {
        //初始化各个组件
        mSolvedCheckBox = v.findViewById(R.id.solved_checkbox);//获取CheckBox组件
        mSolvedCheckBox.setChecked(mMemory.isSolved());//设置解决状态
        mRemindTextView = v.findViewById(R.id.memory_remind);
        mAlarmView = v.findViewById(R.id.memory_alarm);

        setMemoryTime();//设置MemoryTime
        setSolvedListener();//设置SolvedCheckBox监听
        initMemoryTime();//初始化时间显示
        initRemindView();//初始化显示 提醒sign

    }

    //初始化显示 提醒sign
    private void initRemindView() {
        updateRemindView();
        if (mMemory.isRemind()) addCalendarRemind();
        setRemindClickListener();
    }

    //更新 提醒sign
    private void updateRemindView() {
        mAlarmView.setVisibility(mMemory.isRemind() ? View.VISIBLE : View.GONE);
        mRemindTextView.setText(mMemory.isRemind() ? R.string.memory_remind
                : R.string.memory_noremind);
    }

    //设置RemindView点击监听
    private void setRemindClickListener() {
        mRemindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRemindClick();
            }
        });
        mAlarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRemindClick();
            }
        });
    }


    //设置RemindView点击监听的事件
    private void setRemindClick() {
        mMemory.setRemind(!mMemory.isRemind());
        if (mMemory.isRemind() && !mMemory.isSolved()) addCalendarRemind();
        else deleteCalendarRemind(mStartTime);
        MemoryLab.get(getActivity()).updateMemory(mMemory);
        updateRemindView();
    }

    private void setSolvedListener() {
        //为其设置监听器
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) PlaySoundUtils.playSound();
                mMemory.setSolved(isChecked);
                if (mMemory.isRemind() && isChecked) {
                    deleteCalendarRemind(mMemory.getTitle());
                } else if (mMemory.isRemind() && !isChecked) addCalendarRemind();
                updateTimeColor();//更新时间显示颜色
            }
        });
    }


    private void setViewListener() {
        final List<String> linshiTitle = new ArrayList<>();
        final LinkedList<String> linshiDetail = new LinkedList<>();

        final List<String> orignImg = new ArrayList<>();
        final List<String> deledImg = new ArrayList<>();
        if (mDelPicPos == null) mDelPicPos = new ArrayList<>();
        //为其设置监听器
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linshiTitle.clear();
                linshiTitle.add(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //将用户输入的信息设为标题
                mMemory.setTitle(charSequence.toString());
                MemoryLab.get(getActivity()).updateMemory(mMemory);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println(linshiTitle.get(0));
                System.out.println(!mMemory.getTitle().equals(linshiTitle.get(0)) && mMemory.isRemind());
                if (!mMemory.getTitle().equals(linshiTitle.get(0)) && mMemory.isRemind()) {
                    deleteCalendarRemind(linshiTitle.get(0));
                    addCalendarRemind();
                }
            }
        });

        mDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linshiDetail.clear();
                linshiDetail.add(charSequence.toString());

                orignImg.clear();
                deledImg.clear();
                Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
                Matcher m = p.matcher(charSequence.toString());
                while (m.find()) {
                    orignImg.add(m.group());
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mMemory.setDetail(charSequence.toString());
                MemoryLab.get(getActivity()).updateMemory(mMemory);

                if (linshiDetail.getLast().length() > charSequence.toString().length()) {
                    Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
                    Matcher m = p.matcher(charSequence.toString());

                    int imageCount = 0;
                    int pictureCount = 0;
                    while (m.find()) {
                        imageCount++;
                        if (m.group().contains(getActivity().getFilesDir().toString()+"/IMG"))
                            pictureCount++;

                        deledImg.add(m.group());
                    }

                    int delImgPos = -1;
                    int delPicPos = -1;
                    if (imageCount < mImageCount) {
                        for (int j = 0; j < orignImg.size(); j++) {
                            if (deledImg.indexOf(orignImg.get(j)) == -1) {
                                delImgPos = j;
                                if (pictureCount < mPictureCount) {
                                    delPicPos = Integer.parseInt(orignImg.get(j).charAt(
                                            orignImg.get(j).length() - 8) + "");
                                }
                            }
                        }

                        if (delImgPos != -1) {
                            mImages.remove(delImgPos);
                            mBitmaps.remove(delImgPos);
                            if (pictureCount < mPictureCount) {
                                mDelPicPos.add(delPicPos);
                                mPhotoFileList.remove(delPicPos);
                            }
                        }
                    }

                    mImageCount = imageCount;
                    mPictureCount = pictureCount;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!linshiDetail.get(0).equals(mMemory.getDetail()) && mMemory.isRemind()) {
                    deleteCalendarRemind(mMemory.getTitle());
                    addCalendarRemind();
                }
            }
        });
    }


    private void initNoteView(View v) {
        setNoteTime();//设置时间
        updateItemTime();//更新mMemory时间
        initNoteSign(v);//初始化NoteSign
    }

    private void initNoteSign(View v) {
        mNoteSign = v.findViewById(R.id.note_set_sign);
        Drawable mdraw = null;
        switch (mMemory.getSign()) {
            case "BLUE":
                mdraw = getResources().getDrawable(R.drawable.blue_point_shape);
                break;
            case "PURPLE":
                mdraw = getResources().getDrawable(R.drawable.purple_point_shape);
                break;
            case "GREEN":
                mdraw = getResources().getDrawable(R.drawable.green_point_shape);
                break;
        }
        mNoteSign.setBackground(mdraw);

        mNoteSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable d = null;
                switch (mMemory.getSign()) {
                    case "BLUE":
                        d = getResources().getDrawable(R.drawable.green_point_shape);
                        mMemory.setSign("GREEN");
                        break;
                    case "GREEN":
                        d = getResources().getDrawable(R.drawable.purple_point_shape);
                        mMemory.setSign("PURPLE");
                        break;
                    case "PURPLE":
                        d = getResources().getDrawable(R.drawable.blue_point_shape);
                        mMemory.setSign("BLUE");
                        break;
                }
                mNoteSign.setBackground(d);
            }
        });

    }

    /**
     * 添加Bitmap
     */
    private Bitmap addBitmap() {
        Bitmap bitmap = null;
        if (mBitmaps == null) mBitmaps = new LinkedList<>();
        if (mAlbumPaths.size() != 0) {//如果该位置存在Uri
            bitmap = BitmapUtils.getScaledBitmap(mImages.getLast(), getActivity());
            mBitmaps.add(bitmap);
            mAlbumPaths.removeLast();
        } else {
            File mPhotFile = new File(mImages.getLast());
            bitmap = BitmapUtils.getScaledBitmap(
                    mPhotFile.getPath(), getActivity());//根据文件路径新建位图对象
        }
        mBitmaps.add(bitmap);
        return bitmap;
    }


    /**
     * 设置时间
     */
    private void setNoteTime() {
        String date = getNoteDate(mMemory.getDate());
        mDateTextView.setText(date);
        mTimeTextView.setText(mMemory.getTime());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMemory.setDate(date);
            mDateTextView.setText(getNoteDate(date));
            updateRemind();//更新日历提醒事件

        } else if (requestCode == REQUEST_TIME) {
            String time = (String) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mTimeTextView.setText(time);
            mMemory.setTime(time);
            updateRemind();//更新日历提醒事件

        } else if (requestCode == REQUEST_CAMERA) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.lifeifanzs.memorableintent.fileprovider",
                    mPhotoFileList.getLast());

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //相机已经保存文件，就再次调用权限关闭文件的访问

            String mAlbumPath = null;
            String imagestr;


            if (null != data) { //如果intent中保存有数据，即打开的是相册

                mAlbumPath = data.getData().toString();
                imagestr = data.getData().toString();
//                if (mAlbumPath.contains("miui")) {
                    imagestr = MiuiAlbumUtils.getPath(getActivity(), data.getData());
                    mAlbumPath = imagestr;
//                }

                File fromFile = new File(imagestr);
                File toFile = new File(getActivity().getFilesDir(), "albumimg_"+fromFile.getName());
                if(fromFile!=null) {
                    CopyFileUtils.copyFile(fromFile, toFile);
                    imagestr = toFile.toString();
                    mAlbumPath = imagestr;
                }


            } else {
                imagestr = mPhotoFileList.getLast().toString();
                mPictureCount++;
            }

            if (mImages == null) mImages = new LinkedList<>();
            if (mAlbumPaths == null) mAlbumPaths = new LinkedList<>();
            mImages.add(imagestr);//图片列表中保存imagestr
            mImageCount++;//图片数量+1
            if (mAlbumPath != null) mAlbumPaths.add(mAlbumPath);//mUri列表中添加mUri（打开相机时为null）
            addBitmap();//添加Bitmap

            inertImage(mDetailField, mImages.getLast());

        } else if (requestCode == REQUEST_IMAGE) {


            mButtonId = Integer.parseInt(data.getStringExtra(EXTRA_IMAGE_ID));
            //获取删除位置
//            deleteImage(mButtonId);
            //删除图片
        }

    }


    //更新日历提醒事件
    private void updateRemind() {
        setMemoryTime();
        if (mMemory.isRemind()) {
            deleteCalendarRemind(mStartTime);
            addCalendarRemind();
        }
    }


    public void startImageActivity() {
        startImageActivity(mImageCount);
    }


    /**
     * 启动相机或相册
     *
     * @param imageId
     */
    public void startImageActivity(int imageId) {

        if (null == mPhotoFileList) mPhotoFileList = new LinkedList<>();
        if (mPictureCount == mPhotoFileList.size()) {
            int id;
            if (mDelPicPos.size() > 0)
                id = mDelPicPos.get(0);
            else
                id = mPictureCount;
            mPhotoFileList.add(MemoryLab.get(getActivity()).getPhotoFile(mMemory, id));
        }
        final Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);//创建选择器Intent
        Intent albumIntent = new Intent(Intent.ACTION_PICK);//创建相册Intent
        albumIntent.setDataAndType(MediaStore.Images
                .Media.EXTERNAL_CONTENT_URI, "image/*");//设置数据和文件类型
        chooserIntent.putExtra(Intent.EXTRA_INTENT, albumIntent);

        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //创建相机Intent
        PackageManager packageManager = getActivity().getPackageManager();
        //创建得到PackageManager对象
        final boolean canTakePhoto = mPhotoFileList.getLast() != null &&
                captureIntent.resolveActivity(packageManager) != null;
        //判断相机Intent是否可用

        mImageId = imageId;

        if (canTakePhoto) {//判断相机Intent是否可用
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.lifeifanzs.memorableintent.fileprovider", mPhotoFileList.getLast());
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = getActivity()
                    .getPackageManager().queryIntentActivities(captureIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activity : cameraActivities) {
                getActivity().grantUriPermission(activity.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
        }
        startActivityForResult(chooserIntent, REQUEST_CAMERA);
    }


    private String getNoteDate(Date date) {
        Date td = new Date();
        Calendar calendar = Calendar.getInstance();
        Calendar tcalendar = Calendar.getInstance();
        calendar.setTime(date);
        tcalendar.setTime(td);
        if (calendar.get(Calendar.YEAR) != tcalendar.get(Calendar.YEAR)) {
            return (String) DateFormat.format("yyyy年MM月dd日", date);
        } else {
            return (String) DateFormat.format("MM月dd日", date);
        }
    }

    /**
     * 删除日历提醒事件
     *
     * @param startTime
     */
    private void deleteCalendarRemind(long startTime) {
        CalendarUtils.deleteCalendarEventRemind(getActivity(), mMemory.getTitle()
                , mMemory.getDetail(), startTime, null);
    }

    private void deleteCalendarRemind(String title) {
        CalendarUtils.deleteCalendarEventRemind(getActivity(), title
                , mMemory.getDetail(), mMemory.getDate().getTime(), null);
    }

    /**
     * 添加日历提醒事件
     */
    private void addCalendarRemind() {
        mStartTime = mMemory.getDate().getTime();
        CalendarUtils.addCalendarEventRemind(getActivity(), mMemory.getTitle()
                , mMemory.getDetail(), mStartTime, mStartTime
                        + 60000, 5, null);
    }


    /**
     * 插入图片
     *
     * @param editText
     * @param imgPath
     */
    public void inertImage(EditText editText, String imgPath) {

        final Bitmap bitmap = mBitmaps.getLast();
        String imgsrc = "<img src=\"" + imgPath + "\"/>";
        //配置 SpannableString
        SpannableString spannableString = new SpannableString(imgsrc);
        Drawable drawable = new BitmapDrawable(editText.getContext().getResources(), bitmap
        );
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        ImageSpan span = new ImageSpan(drawable);
        spannableString.setSpan(span, 0, imgsrc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Editable text = editText.getText();
        text.insert(editText.getSelectionStart(), "\n");
        int start = editText.getSelectionStart();//获取光标位置
        text.insert(start, spannableString);//添加图片

        editText.append("\n");
    }

    /**
     * 初始化内容中的图片
     */
    private void loadImage() {
        String text = mDetailField.getText().toString();


        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(text);
        SpannableString ss = new SpannableString(text);


        while (m.find()) {
            String path = m.group().replaceAll("\\<img src=\"|\"\\/>", "").trim();

            mImageCount++;
            if (mImages == null) mImages = new LinkedList<>();
            if (null == mAlbumPaths) mAlbumPaths = new LinkedList<>();

            mImages.addLast(path);
            if (path.contains("/data/user/0/com")) {
                if (mPhotoFileList == null) mPhotoFileList = new LinkedList<>();
                mPhotoFileList.add(MemoryLab.get(getActivity()).getPhotoFile(mMemory, mPictureCount));
                mPictureCount++;
            } else if (path.contains("content://")) {
                mAlbumPaths.addLast(path);
            }

            Bitmap bm = addBitmap();

            ImageSpan span = new ImageSpan(getActivity(), bm);
            ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//            Bitmap bm=BitmapUtils.stringToBitmap(mMemory.getBitmpaStr());


        mDetailField.setText(ss);

    }


}