package com.lifeifanzs.memorableintent.SQLite;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.lifeifanzs.memorableintent.Bean.Memory;
import com.lifeifanzs.memorableintent.SQLite.MemoryDbSchema.MemoryTable;

import java.util.Date;
import java.util.UUID;

public class MemoryCursorWrapper extends CursorWrapper {
    public MemoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Memory getMemory() {
        String uuidString = getString(getColumnIndex(MemoryTable.Cols.UUID));
        String title = getString(getColumnIndex(MemoryTable.Cols.TITLE));
        String detail = getString(getColumnIndex(MemoryTable.Cols.DETAIL));
        long date = getLong(getColumnIndex(MemoryTable.Cols.DATE));
        String time = getString(getColumnIndex(MemoryTable.Cols.TIME));
        int isSolved = getInt(getColumnIndex(MemoryTable.Cols.SOLVED));
        int isNote = getInt(getColumnIndex(MemoryTable.Cols.ISNOTE));
        int isOver = getInt(getColumnIndex(MemoryTable.Cols.OVER));
        String sign = getString(getColumnIndex(MemoryTable.Cols.SIGN));
        int isZhiding = getInt(getColumnIndex(MemoryTable.Cols.ZHIDING));
        long zhidingdate = getLong(getColumnIndex(MemoryTable.Cols.ZHIDINGDATE));
        int isRemind = getInt(getColumnIndex(MemoryTable.Cols.REMIND));

        Memory memory = new Memory(UUID.fromString(uuidString));
        memory.setTitle(title);
        memory.setDate(new Date(date));
        memory.setTime(time);
        memory.setDetail(detail);
        memory.setSolved(isSolved != 0);
        memory.setNote(isNote != 0);
        memory.setOver(isOver != 0);
        memory.setSign(sign);
        memory.setZhiding(isZhiding != 0);
        memory.setZhidingdate(new Date(zhidingdate));
        memory.setRemind(isRemind != 0);

        return memory;
    }
}
