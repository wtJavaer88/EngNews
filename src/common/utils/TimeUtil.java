package common.utils;

public class TimeUtil
{
    public static String timeToText(int millistime)
    {
        if (millistime < 1000)
        {
            return "00:00";
        }

        millistime = millistime / 1000;
        int hours = millistime / 3600;
        int minutes = millistime % 3600 / 60;
        int seconds = millistime % 60;
        if (hours > 0)
        {
            return aligntime(hours) + ":" + aligntime(minutes) + ":"
                    + aligntime(seconds);
        }
        return aligntime(minutes) + ":" + aligntime(seconds);
    }

    public static String aligntime(int t)
    {
        return (t >= 10 ? t + "" : "0" + t);
    }
}
