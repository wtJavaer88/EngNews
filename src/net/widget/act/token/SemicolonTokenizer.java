package net.widget.act.token;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

public class SemicolonTokenizer implements Tokenizer
{
    private char singleChar = 0;
    private String multiChars;
    private String mSTring;

    public SemicolonTokenizer(char singleChar)
    {
        this.singleChar = singleChar;
        mSTring = String.valueOf(singleChar);
    }

    /**
     * 默认单词结尾为给定的第一个字符
     * 
     * @param chars
     */
    public SemicolonTokenizer(String chars)
    {
        if (chars == null || chars.length() == 0)
        {
            throw new IllegalArgumentException("分隔符参数不合法......");
        }
        this.multiChars = chars;
        this.mSTring = String.valueOf(chars.charAt(0));
    }

    /**
     * 自定义一个单词结尾
     * 
     * @param chars
     * @param end
     */
    public SemicolonTokenizer(String chars, String end)
    {
        if (chars == null || chars.length() == 0)
        {
            throw new IllegalArgumentException("分隔符参数不合法......");
        }
        this.multiChars = chars;
        this.mSTring = end;
    }

    @Override
    public int findTokenStart(CharSequence text, int cursor)
    {
        int i = cursor;
        while (i > 0 && !isMatchChar(text.charAt(i - 1)))
        {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ')
        {
            i++;
        }

        return i;
    }

    private boolean isMatchChar(char c)
    {
        if (singleChar != 0)
        {
            if (c == singleChar)
            {
                return true;
            }
        }
        else
        {
            for (int i = 0; i < multiChars.length(); i++)
            {
                if (multiChars.charAt(i) == c)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor)
    {
        int i = cursor;
        int len = text.length();

        while (i < len)
        {
            if (isMatchChar(text.charAt(i)))
            {
                return i;
            }
            else
            {
                i++;
            }
        }

        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence text)
    {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ')
        {
            i--;
        }

        if (i > 0 && isMatchChar(text.charAt(i - 1)))
        {
            return text;
        }
        else
        {
            if (text instanceof Spanned)
            {
                SpannableString sp = new SpannableString(text + mSTring);
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            }
            else
            {
                return text + mSTring;
            }
        }
    }

}
