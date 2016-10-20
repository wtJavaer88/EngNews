package com.wnc.news.engnews.helper;

import net.selectabletv.SelectableTextView;

public class NewsContentUtil
{

    public static String getSection(String content, int i, int j)
    {
        j--;
        System.out.println(i + "  " + j);
        while (i > -1 && i < content.length()
                && (content.charAt(i) + "").matches("[a-zA-z]{1}"))
        {
            i--;
        }
        i++;
        while (j > -1 && j < content.length()
                && (content.charAt(j) + "").matches("[a-zA-z]{1}"))
        {
            j++;
        }
        return content.substring(i, j);
    }

    /**
     * 通过制定位置,获取合适的单词
     * 
     * @param mTextView
     * @param start
     * @return
     */
    public static String getSuitWord(SelectableTextView mTextView, int start)
    {

        String text = mTextView.getText().toString();
        while (!(text.charAt(start) + "").matches("[0-9a-zA-Z]{1}"))
        {
            start++;
        }
        if (start >= text.length())
        {
            return "";
        }
        int end = start;
        while ((text.charAt(end) + "").matches("[0-9a-zA-Z]{1}"))
        {
            end++;
            if (end >= text.length())
            {
                end = text.length() - 1;
                break;
            }

        }
        while ((text.charAt(start) + "").matches("[0-9a-zA-Z]{1}"))
        {
            start--;
            if (start < 0)
            {
                break;
            }
        }
        // 主要是调动CursorHand小手的位置
        mTextView.showSelectionControls(start + 1, end);
        return mTextView.getCursorSelection().getSelectedText().toString();
    }
}
