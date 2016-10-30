package com.wnc.news.engnews.helper;

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

public class SpecialAdapter extends SimpleAdapter
{
	private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	{
		super(context, data, resource, from, to);
	}

	String key;

	public SpecialAdapter setKey(String key)
	{
		this.key = key;
		return this;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		TextView lblName = (TextView) view.findViewById(R.id.ItemTitle);
		String text = lblName.getText().toString();
		if (BasicStringUtil.isNotNullString(key))
		{
			int i = text.indexOf(key);
			String text2 = text.substring(0, i) + "<font color=blue>" + key + "</font>" + text.substring(i + key.length());
			lblName.setText(Html.fromHtml(text2));
		}
		return view;
	}
}