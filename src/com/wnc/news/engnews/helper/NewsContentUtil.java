package com.wnc.news.engnews.helper;

import net.selectabletv.SelectableTextView;

import com.wnc.basic.BasicStringUtil;

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
                    && (content.charAt(i) + "").matches("[a-zA-z'‘’0-9\\-]{1}"))
            {
                i--;
            }
            i++;
            while (j > -1 && j < content.length()
                    && (content.charAt(j) + "").matches("[a-zA-z'‘’0-9\\-]{1}"))
            {
                j++;
            }
            mTextView.removeSelection();
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
            while (!isWordChar(start, text))
            {
                start++;
            }
            if (start >= text.length())
            {
                return "";
            }
            int end = start;
            while (isWordChar(end, text))
            {
                end++;
                if (end >= text.length())
                {
                    end = text.length() - 1;
                    break;
                }

            }
            while (isWordChar(start, text))
            {
                start--;
                if (start < 0)
                {
                    break;
                }
            }
            // 主要是调动CursorHand小手的位置
            mTextView.removeSelection();
            mTextView.showSelectionControls(start + 1, end);
            return mTextView.getCursorSelection().getSelectedText().toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private static boolean isWordChar(int start, String text)
    {
        return (text.charAt(start) + "").matches("[0-9a-zA-Z]{1}");
    }

    public static CharSequence getSection(String content, String selected)
    {
        content = content.toLowerCase().replaceAll("<.*?>", "");
        selected = selected.toLowerCase();
        if (BasicStringUtil.isNull2String(content, selected)
                || content.length() < selected.length())
        {
            return "";
        }

        int i = content.indexOf(selected);
        int j = i + selected.length();
        if (i == -1)
        {
            return "";
        }
        try
        {
            while (i > -1 && i < content.length() && isSectionChar(content, i))
            {
                i--;
            }
            i++;
            while (j > -1 && j < content.length() && isSectionChar(content, j))
            {
                j++;
            }
            if (i >= j)
            {
                return "";
            }
            return content.substring(i, j);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private static boolean isSectionChar(String content, int j)
    {
        return (content.charAt(j) + "").matches("[a-zA-Z0-9\\-‘’' ]{1}");
    }
}
