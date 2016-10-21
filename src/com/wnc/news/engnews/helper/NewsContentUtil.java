package com.wnc.news.engnews.helper;

import net.selectabletv.SelectableTextView;

public class NewsContentUtil
{
    /**
     * 自动匹配段落起始位置,获取合适的单词
     * 
     * @param mTextView
     * @param i
     * @param j
     * @return
     */
    public static String getSectionAndSetPos(SelectableTextView mTextView,
            int i, int j)
    {
        try
        {
            String content = mTextView.getText().toString();
            j--;
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
            mTextView.showSelectionControls(i, j);
            return content.substring(i, j);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 自动匹配单词起始位置,获取合适的单词
     * 
     * @param mTextView
     * @param start
     * @return
     */
    public static String getSuitWordAndSetPos(SelectableTextView mTextView,
            int start)
    {

        try
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
