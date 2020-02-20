package com.lifeifanzs.memorableintent.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.SQLite.MemoryDbSchema.MemoryTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemoryLab {
    private static MemoryLab sMemoryLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private MemoryLab(Context context) {

        mContext = context.getApplicationContext();
        mDatabase = new MemoryBaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * 获取MemoryLab单例对象
     *
     * @param context
     * @return
     */
    public static MemoryLab get(Context context) {
        if (sMemoryLab == null) sMemoryLab = new MemoryLab(context);
        return sMemoryLab;
    }

    /**
     * 获取所有的Memory
     *
     * @return
     */
    public List<Memory> getMemories() {
        List<Memory> memories = new ArrayList<>();

        MemoryCursorWrapper cursor = queryMemories(null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                if (!cursor.getMemory().isNote())
                    memories.add(cursor.getMemory());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return memories;
    }


    public List<Memory> getNotes() {
        List<Memory> memories = new ArrayList<>();

        MemoryCursorWrapper cursor = queryMemories(null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                if (cursor.getMemory().isNote())
                    memories.add(cursor.getMemory());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return memories;
    }


    /**
     * 获取指定的Memory
     *
     * @param id
     * @return
     */
    public Memory getMemory(UUID id) {
        MemoryCursorWrapper cursor = queryMemories(
                MemoryTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getMemory();
        } finally {
            cursor.close();
        }
    }

    /**
     * 添加新的Memory
     *
     * @param m
     */
    public void addMemory(Memory m) {
        ContentValues values = getContentValues(m);

        mDatabase.insert(MemoryTable.NAME, null, values);
    }

    /**
     * 将Memory记录转换为ContentValues
     *
     * @param memory
     * @return
     */
    public static ContentValues getContentValues(Memory memory) {
        ContentValues values = new ContentValues();
        values.put(MemoryTable.Cols.UUID, memory.getId().toString());
        values.put(MemoryTable.Cols.TITLE, memory.getTitle());
        values.put(MemoryTable.Cols.DATE, memory.getDate().getTime());
        values.put(MemoryTable.Cols.TIME, memory.getTime());
        values.put(MemoryTable.Cols.DETAIL, memory.getDetail());
        values.put(MemoryTable.Cols.SOLVED, memory.isSolved());
        values.put(MemoryTable.Cols.ISNOTE, memory.isNote());
        values.put(MemoryTable.Cols.OVER, memory.isOver());
        values.put(MemoryTable.Cols.SIGN, memory.getSign());
        values.put(MemoryTable.Cols.ZHIDING, memory.isZhiding());
        values.put(MemoryTable.Cols.REMIND, memory.isRemind());
        if (memory.getZhidingdate() != null) {
            values.put(MemoryTable.Cols.ZHIDINGDATE, memory.getZhidingdate().getTime());
        }

        return values;
    }

    /**
     * 更新数据库记录
     *
     * @param memory
     */
    public void updateMemory(Memory memory) {
        String uuidString = memory.getId().toString();
        ContentValues values = getContentValues(memory);
        mDatabase.update(MemoryTable.NAME, values,
                MemoryTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * 查询Memory记录
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private MemoryCursorWrapper queryMemories(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MemoryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                null//orderBy
        );
        return new MemoryCursorWrapper(cursor);
    }

    /**
     * 删除指定Memory记录
     *
     * @param memory
     */
    public void deleteMemory(Memory memory) {
        String uuidString = memory.getId().toString();
        mDatabase.delete(MemoryTable.NAME,
                MemoryTable.Cols.UUID + " = ?"
                , new String[]{uuidString});
    }

    public File getPhotoFile(Memory memory, int id) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, memory.getPhotoFilename(id));
    }

}
