package com.lifeifanzs.memorableintent.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lifeifanzs.memorableintent.SQLite.MemoryDbSchema.MemoryTable;

public class MemoryBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "memoryBase.db";

    public MemoryBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MemoryTable.NAME + "(" +
                "_id integer PRIMARY KEY AUTOINCREMENT, " +
                MemoryTable.Cols.UUID + ", " +
                MemoryTable.Cols.TITLE + ", " +
                MemoryTable.Cols.DATE + ", " +
                MemoryTable.Cols.TIME + ", " +
                MemoryTable.Cols.DETAIL + ", " +
                MemoryTable.Cols.ISNOTE + ", " +
                MemoryTable.Cols.OVER + ", " +
                MemoryTable.Cols.SIGN + ", " +
                MemoryTable.Cols.ZHIDING + ", " +
                MemoryTable.Cols.ZHIDINGDATE + ", " +
                MemoryTable.Cols.REMIND + ", " +
                MemoryTable.Cols.SOLVED + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
