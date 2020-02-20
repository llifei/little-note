package com.lifeifanzs.memorableintent.SQLite;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.lifeifanzs.memorableintent.Bean.Theme;
import com.lifeifanzs.memorableintent.SQLite.ThemeDbSchema.ThemeTable;

public class ThemeCursorWrapper extends CursorWrapper {
    public ThemeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Theme getTheme() {
        String color = getString(getColumnIndex(ThemeTable.Cols.COLOR));
        String uri = getString(getColumnIndex(ThemeTable.Cols.URI));
        int pager = getInt(getColumnIndex(ThemeTable.Cols.PAGER));

        Theme theme = new Theme();
        theme.setUri(uri);
        theme.setColor(color);
        theme.setPager(pager);

        return theme;
    }
}
