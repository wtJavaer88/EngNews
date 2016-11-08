package com.wnc.news.engnews.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.engnews.kpi.KPIService;

public class NewsListAdapter extends SimpleAdapter
{
    private int[] colors = new int[]
    { 0x30FF0000, 0x300000FF };

    public NewsListAdapter(Context context,
            List<? extends Map<String, ?>> data, int resource, String[] from,
            int[] to)
    {
        super(context, data, resource, from, to);
    }

    String key;

    public NewsListAdapter setKey(String key)
    {
        this.key = key.trim();
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        TextView titleTv = (TextView) view.findViewById(R.id.ItemTitle);
        String text = titleTv.getText().toString();
        if (BasicStringUtil.isNotNullString(key))
        {
            int i = text.indexOf(key);
            if (i != -1)
            {
                String text2 = text.substring(0, i) + "<font color=blue>" + key
                        + "</font>" + text.substring(i + key.length());
                titleTv.setText(Html.fromHtml(text2));
            }
        }
        final NewsInfo item = (NewsInfo) ((HashMap) this.getItem(position))
                .get("news_info");
        if (KPIService.getInstance().hasViewed(item.getDb_id()))
        {
            // System.out.println("已经阅读过!");
            titleTv.setTextColor(0xff666666);
            view.setBackgroundColor(0xffffffff);

        }
        return view;
    }
}