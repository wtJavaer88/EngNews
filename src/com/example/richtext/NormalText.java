package com.example.richtext;

public class NormalText implements RichText
{

    private String text;

    public NormalText(String text)
    {
        this.text = text;
    }

    @Override
    public CharSequence getCharSequence()
    {
        return this.text;
    }
}
