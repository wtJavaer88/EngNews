package common.utils;

import java.util.Date;

public class DateTimeSelectArrUtil
{
    /**
     * 获取前一年后一年和今年的年份
     * 
     * @return
     */
    public static String[] getYears()
    {
        int year = new Date().getYear() + 1900;
        String[] arr = new String[3];
        arr[0] = (year - 1) + "年";
        arr[1] = (year) + "年";
        arr[2] = (year + 1) + "年";
        return arr;
    }

    public static String[] getMonths()
    {
        return getOrderedArrByLen(12, 1, "月");
    }

    public static String[] getDays()
    {
        return getOrderedArrByLen(31, 1, "日");
    }

    public static String[] getHours()
    {
        return getOrderedArrByLen(24, 0, "时");
    }

    public static String[] getMinutes()
    {
        return getOrderedArrByLen(60, 0, "分");
    }

    public static String[] getSeconds()
    {
        return getOrderedArrByLen(60, 0, "秒");
    }

    private static String[] getOrderedArrByLen(int len, int start, String ext)
    {
        String[] arr = new String[len];
        for (int i = 0; i < len; i++)
        {
            arr[i] = start + (i) + ext;
        }
        return arr;
    }

}
