package com.wnc.news.api.soccer;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.Club;
import com.wnc.string.PatternUtil;
import common.utils.JsoupHelper;

public class LeagueApi
{
    int league_id;

    public LeagueApi(int league_id)
    {
        this.league_id = league_id;
    }

    public List<Club> getAllClubs()
    {
        List<Club> clubs = new ArrayList<Club>();
        try
        {
            String jsonStr = JsoupHelper
                    .getJsonResult("http://www.squawka.com/team-directory?ajax=true&league_id="
                            + this.league_id
                            + "&mode=getClubs&cache="
                            + Math.random());
            if (BasicStringUtil.isNotNullString(jsonStr))
            {
                JSONArray arr = JSONArray.parseArray(jsonStr);
                for (int i = 0; i < arr.size(); i++)
                {
                    try
                    {
                        Club club = new Club();
                        JSONObject jsonObject = (JSONObject) arr.get(i);
                        club.setClub_id(BasicNumberUtil.getNumber(jsonObject
                                .get("club_id").toString()));
                        club.setAbbreviation(jsonObject.get("abbreviation")
                                .toString());
                        club.setShort_name(jsonObject.get("short_name")
                                .toString());
                        club.setPhoto(jsonObject.get("photo").toString());
                        club.setClub_stats_url(jsonObject.get("club_stats_url")
                                .toString());
                        club.setFull_name(PatternUtil.getFirstPatternGroup(
                                jsonObject.get("club_stats_url").toString(),
                                "teams/(.*?)/stats"));
                        clubs.add(club);
                    }
                    catch (Exception e)
                    {
                        System.out.println("俱乐部数据解析失败.");
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                System.out.println("获取联赛俱乐部数据失败!");
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return clubs;
    }
}