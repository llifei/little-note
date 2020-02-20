package com.lifeifanzs.memorableintent.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.SQLite.ThemeDbSchema.ThemeTable;

public class ThemeLab {

    private static ThemeLab sThemeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;


    private ThemeLab(Context context) {
        this.mContext = context;
        mDatabase = new ThemeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static ThemeLab getThemeLab(Context context) {
        if (sThemeLab == null) sThemeLab = new ThemeLab(context);
        return sThemeLab;
    }

    /**
     * 获得主题
     *
     * @return
     */
    public Theme getTheme() {
        Theme theme = null;
        Cursor cursor = mDatabase.query(
                ThemeTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ThemeCursorWrapper cursorWrapper = new
                ThemeCursorWrapper(cursor);
        cursorWrapper.moveToFirst();
        try {
            while (!cursorWrapper.isAfterLast()) {
                theme = cursorWrapper.getTheme();
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }

        return theme;
    }

    /**
     * 添加主题
     *
     * @param t
     */
    public void addTheme(Theme t) {
        ContentValues values = getContentValues(t);

        mDatabase.insert(ThemeTable.NAME, null, values);

    }

    public void updateTheme(Theme theme) {
        ContentValues values = getContentValues(theme);
        mDatabase.update(ThemeTable.NAME, values
                , null, null);
    }


    /**
     * 将Theme记录转换为ContentValues
     *
     * @param theme
     * @return
     */
    public static ContentValues getContentValues(Theme theme) {
        ContentValues values = new ContentValues();
        values.put(ThemeTable.Cols.COLOR, theme.getColor());
        values.put(ThemeTable.Cols.URI,theme.getUri());
        values.put(ThemeTable.Cols.PAGER,theme.getPager());

        return values;
    }

}
