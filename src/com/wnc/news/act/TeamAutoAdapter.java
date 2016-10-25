package com.wnc.news.act;


import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.ViewHolder;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.engnews.R;

public class TeamAutoAdapter extends net.widget.act.abs.MyActAdapter
{
    public TeamAutoAdapter(Context context, List<AutoCompletable> items,
            int maxMatch)
    {
        super(context, items, maxMatch);
    }

    class BookViewHolder extends ViewHolder
    {
        TextView nameTv;
        TextView descTv;
    }

    @Override
    protected View getView2(int position, View convertView, ViewGroup parent)
    {
        BookViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new BookViewHolder();
            convertView = View.inflate(context, R.layout.act_item, null);
            viewHolder.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_team_name);
            viewHolder.descTv = (TextView) convertView
                    .findViewById(R.id.tv_team_desc);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (BookViewHolder) convertView.getTag();
        }
        BookViewHolder bookviewHolder = viewHolder;
        ActTeam actTeam = (ActTeam) autoItems.get(position);
        bookviewHolder.nameTv.setText(actTeam.getTeam());
        bookviewHolder.descTv.setText(actTeam.getDesc());
        return convertView;
    }
}
