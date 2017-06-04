package common.utils;

public class NewsUrlUtil
{
	public static boolean isZhibo8Url(String url)
	{
		return url != null && url.contains(".zhibo8.");
	}

	public static boolean isHupuUrl(String url)
	{
		return url != null && url.contains(".hupu.");
	}
}
