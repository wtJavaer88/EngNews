package com.wnc.news.act;

import net.widget.act.abs.AutoCompletable;

public class ActTeam implements AutoCompletable
{
    private String team;
    private String desc;

    public ActTeam(String team, String desc)
    {
        this.team = team;
        this.desc = desc;
    }

    @Override
    public String toString()
    {
        return this.team;
    }

    @Override
    public boolean match(String searchStr)
    {
        if (searchStr.equals("*"))
        {
            return true;
        }
        if (this.getTeam() != null
                && this.getTeam().trim().startsWith(searchStr))
        {
            return true;
        }
        if (this.getDesc() != null && this.getDesc().trim().contains(searchStr))
        {
            return true;
        }
        return false;
    }

    public String getTeam()
    {
        return team;
    }

    public void setWord(String word)
    {
        this.team = word;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }
}
