package common.utils;

public class FileTypeUtil
{
    public static boolean isPicFile(String filePath)
    {
        return filePath.endsWith(".jpeg") || filePath.endsWith(".jpg")
                || filePath.endsWith(".bmp") || filePath.endsWith(".gif")
                || filePath.endsWith(".png") || filePath.endsWith(".pic");
    }

    public static boolean isTextFile(String filePath)
    {
        return filePath.endsWith(".txt") || filePath.endsWith(".java")
                || filePath.endsWith(".xml") || filePath.endsWith(".css")
                || filePath.endsWith(".cs");
    }

    public static boolean isVoiceFile(String filePath)
    {
        return filePath.endsWith(".amr") || filePath.endsWith(".mp3")
                || filePath.endsWith(".wma") || filePath.endsWith(".ogg");
    }

    public static boolean isVideoFile(String filePath)
    {
        return filePath.endsWith(".3gp") || filePath.endsWith(".mp4")
                || filePath.endsWith(".wmv") || filePath.endsWith(".avi");
    }
}
