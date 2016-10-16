package common.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipBoardUtil
{
    @SuppressWarnings("deprecation")
    public static String getClipBoardContent(Context context)
    {
        ClipboardManager clipboardManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip())
        {

            return clipboardManager.getPrimaryClip().getItemAt(0).getText()
                    .toString();
        }
        System.out.println("nullssss");
        return "";
    }

    public static void setNormalContent(Context context, String text)
    {
        @SuppressWarnings("deprecation")
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("key", text);
        clipboard.setPrimaryClip(textCd);
        System.out.println("text: " + text);
    }
}
