package com.lifeifanzs.memorableintent.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;

public class ColorUtils {

    private static final int REQUEST_MEDIA_PROJECTION = 0;
    private static MediaProjectionManager mMediaProjectionManager;
    private static MediaProjection mMediaProjection;

    private static ImageReader reader;
    private static Bitmap bitmap;

    /**
     * 开启屏幕投影扫面
     */
    public static void startScreenActivity(Activity ac) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) ac.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            assert mMediaProjectionManager != null;
            ac.startActivityForResult(
                    mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    /**
     * 处理回调屏幕投影
     *
     * @param ac
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static boolean onActivityResult(Activity ac, int requestCode, int resultCode, Intent data,
                                           int x, int y) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK)
                return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            }
            setUpVirtualDisplay(ac);
            return colorIsLight(x, y);
        }

        return false;
    }

    private static void setUpVirtualDisplay(Activity ac) {
        DisplayMetrics dm = new DisplayMetrics();
        ac.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        ImageReader imageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection.createVirtualDisplay("ScreenCapture",
                    dm.widthPixels, dm.heightPixels, dm.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.getSurface(), null, null);
            reader = imageReader;
        }
    }


    /**
     * 获取指定坐标颜色
     *
     * @param x
     * @param y
     * @return
     */
    public static int getColor(int x, int y) {
        if (reader == null) {
            return -1;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            if (bitmap == null) {
                return -1;
            }
            return bitmap.getPixel(x, y);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        return bitmap.getPixel(x, y);
    }

    /**
     * 获取指定坐标颜色是否为浅色
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean colorIsLight(int x, int y) {
        int color = getColor(x, y);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int gray = (int) (r * 0.299 + g * 0.587 + b * 0.114);

        gray=100;
        return gray < 192;
    }
}
