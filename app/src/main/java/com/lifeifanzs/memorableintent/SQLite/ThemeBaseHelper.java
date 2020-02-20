package com.lifeifanzs.memorableintent.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lifeifanzs.memorableintent.SQLite.ThemeDbSchema.ThemeTable;

public class ThemeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "themeBase.db";

    public ThemeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ThemeTable.NAME + "(" +
                "id_ integer PRIMARY KEY AUTOINCREMENT, " +
                ThemeTable.Cols.COLOR + ", " +
                ThemeTable.Cols.PAGER+", "+
                ThemeTable.Cols.URI + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
