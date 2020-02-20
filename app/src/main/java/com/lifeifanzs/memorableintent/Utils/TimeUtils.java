package com.lifeifanzs.memorableintent.Utils;

import android.text.format.DateFormat;

import com.lifeifanzs.memorableintent.Bean.Memory;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static Date mToday;

    public static void setToday(Date date) {
        mToday = date;
    }

    /**
     * 设置条目显示的时间
     *
     * @param mMemory
     */
    public static String setMemoryTime(Memory mMemory) {
        Date date = mMemory.getDate();
        String time = mMemory.getTime();
        String memoryTime = "" + time;
        Calendar memoryCalendar = Calendar.getInstance();
        Calendar todayCalendar = Calendar.getInstance();
        memoryCalendar.setTime(date);
        todayCalendar.setTime(mToday);
        int memoryYear = memoryCalendar.get(Calendar.YEAR);
        int todayYear = todayCalendar.get(Calendar.YEAR);
        int memoryDay = memoryCalendar.get(Calendar.DAY_OF_MONTH);
        int todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int memoryMonth = memoryCalendar.get(Calendar.MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        if (memoryYear < todayYear
                || ((memoryYear == todayYear && memoryMonth == todayMonth) && (memoryDay < todayDay))
                || (memoryYear == todayYear && memoryMonth < todayMonth)) {
            if (!mMemory.isSolved())
                memoryTime = getMonth(memoryCalendar, date);
            else
                memoryTime = getMonth(memoryCalendar, date);
            mMemory.setOver(true);
        } else if (memoryYear == todayYear && memoryMonth == todayMonth) {
            switch (memoryDay - todayDay) {
                case 0:
                    break;
                case 1:
                    memoryTime = "明天 " + memoryTime;
                    break;
                case 2:
                    memoryTime = "后天" + memoryTime;
                    break;
                default:
                    memoryTime = getMonth(memoryCalendar, date);
                    break;
            }
        } else if (memoryYear == todayYear && memoryMonth > todayMonth) {
            memoryTime = getMonth(memoryCalendar, date);
        } else if (memoryYear > todayYear) {
            memoryTime = memoryYear + "年 " + memoryMonth + "月";
        }
        return memoryTime;
    }


    public static String setNoteTime(Memory memory){
        String noteTime = memory.getTime();
        Date date =memory.getDate();
        Calendar noteCalendar = Calendar.getInstance();
        Calendar todayCalendar = Calendar.getInstance();
        noteCalendar.setTime(date);
        todayCalendar.setTime(mToday);
        int noteYear = noteCalendar.get(Calendar.YEAR);
        int todayYear = todayCalendar.get(Calendar.YEAR);
        int noteMonth = noteCalendar.get(Calendar.MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int noteDay = noteCalendar.get(Calendar.DAY_OF_MONTH);
        int todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
        if (noteYear == todayYear) {
            if (noteMonth == todayMonth) {
                if (noteDay == todayDay)
                    noteTime = noteTime;
                else if (noteDay == todayDay - 1)
                    noteTime = "昨天 " + noteTime;
                else if (noteDay == todayDay - 2)
                    noteTime = "前天 " + noteTime;
                else
                    noteTime = getMonth(noteCalendar, date);
            } else
                noteTime = getMonth(noteCalendar, date);
        } else {
            noteTime = noteYear + "年 " + noteMonth+"月";
        }
        return noteTime;
    }


    /**
     * 获取月份
     *
     * @param memoryCalendar
     * @param date
     * @return
     */
    public static String getMonth(Calendar memoryCalendar, Date date) {
        String month;
        if (memoryCalendar.get(Calendar.MONTH) >= 10)
            month = (String) DateFormat.format("MM月dd日", date);
        else
            month = (String) DateFormat.format("M月dd日", date);
        return month;
    }

}
