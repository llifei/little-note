package com.lifeifanzs.memorableintent.Fragment;

import android.app.Activity;;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.lifeifanzs.memorableintent.Utils.BitmapUtils;
import com.lifeifanzs.memorableintent.R;

import java.io.File;
import java.io.FileNotFoundException;

public class ShowImageFragment extends Fragment {

    private static final String ARG_IMAGE_BITMAP =
            "com.lifeifanzs.memorableintent.image_bitmap";
    private static final String ARG_IMAGE_ID =
            "com.lifeifanzs.memorableintent.image_id";
    private static final String ARG_IMAGE_COUNT =
            "com.lifeifanzs.memorableintent.image_count";
    private static final String EXTRA_IMAGE_ID=
            "imageid";


    private ImageView mImageView;
    private Toolbar mToolbar;

    private String imageStr;
    private int imageId;
    private int imageCount;

    public static ShowImageFragment newInstance(int imageId
            , int imageCount, String imageStr) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE_BITMAP, imageStr);
        args.putSerializable(ARG_IMAGE_ID, imageId);
        args.putSerializable(ARG_IMAGE_COUNT, imageCount);

        ShowImageFragment fragment = new ShowImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bitmap bitmap = null;
        imageStr = (String) getArguments().getSerializable(ARG_IMAGE_BITMAP);
        imageId = (int) getArguments().getSerializable(ARG_IMAGE_ID);
        imageCount = (int) getArguments().getSerializable(ARG_IMAGE_COUNT);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_image, container, false);

        mImageView = v.findViewById(R.id.memory_imageView);//获取imageView对象
        mToolbar = v.findViewById(R.id.memory_image_toolbar);//获取ToolBar对象


        Bitmap bitmap = null;
        File photoFile = new File(imageStr);
        //获取intent中的file数据

        if (photoFile != null && photoFile.exists()) {//如果file不为null且存在

            bitmap = BitmapUtils.getScaledBitmap(
                    photoFile.getPath(), getActivity());
            //通过file获得bitmap

        } else {
            Uri imageUri = Uri.parse(imageStr);
            try {
                bitmap = BitmapFactory.decodeStream(getActivity()
                        .getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }//通过Uri获得bitmap
        }


        mImageView.setImageBitmap(bitmap);//给imageView设置bitmap



        System.out.println("imageId: "+imageId);
        String toolBarTitle = (imageId + 1) + "/" + imageCount;

        mToolbar.setTitle(toolBarTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.inflateMenu(R.menu.fragment_showimage);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.showImage_toolbar_delete:
                            Intent intent = new Intent();
                            int position=imageId;
                            intent.putExtra(EXTRA_IMAGE_ID,imageId+"");
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }

        return v;
    }

}
