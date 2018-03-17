package com.androidapp.g_s_org.mytimetable.common;

        import java.util.Calendar;

// judge weekday or holiday or sunday
public class TimeUtil {
    private TimeUtil(){}

    // check whether the given day is weekday, Saturday, or holiday
    public static String getTypeOfDay(Calendar cal, String operator, String line) {
        if (cal.get(Calendar.HOUR_OF_DAY) < 2){
            // 0:00-2:00 AM treated as the previous day
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        // check type of day
        String typeOfDay = Common.VAL_WEEKDAY;
        if (isHoliday(cal)){
            // holiday
            switch (operator) {
                case Common.KEIO:
                    typeOfDay = Common.VAL_SATHOLIDAY;
                    break;
                default:
                    typeOfDay = Common.VAL_HOLIDAY;
                    break;
            }
        } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            // sunday (treated as holiday)
            switch (operator) {
                case Common.KEIO:
                    typeOfDay = Common.VAL_SATHOLIDAY;
                    break;
                default:
                    typeOfDay = Common.VAL_HOLIDAY;
                    break;
            }
        } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            // Saturday
            switch (operator) {
                case Common.KEIO:
                    typeOfDay = Common.VAL_SATHOLIDAY;
                    break;
                default:
                    typeOfDay = Common.VAL_SATURDAY;
                    break;
            }
        } else {
            // other (treated as weekday)
            typeOfDay = Common.VAL_WEEKDAY;
        }
        return typeOfDay;
    }

    // check whether the holiday in the year matches the given date
    public static boolean isHoliday(Calendar cal) {
        if (cal != null) {
            // check each holiday of the year
            for (Common.JapaneseNationalHoliday hol : Common.JapaneseNationalHoliday.values()) {
                if (cal.get(Calendar.MONTH) == hol.dateOf(cal.get(Calendar.YEAR)).get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) == hol.dateOf(cal.get(Calendar.YEAR)).get(Calendar.DAY_OF_MONTH)){
                    return true;
                }
            }
        }
        return false;
    }
}


