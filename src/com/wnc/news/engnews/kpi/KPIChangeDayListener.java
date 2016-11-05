package com.wnc.news.engnews.kpi;

import java.util.Iterator;
import java.util.Set;

import android.os.Handler;
import android.os.Message;

import com.wnc.news.engnews.kpi.ClickableKPIRichText.KPIChangeDayEvent;
import com.wnc.news.engnews.ui.MainActivity;
import common.utils.TimeUtil;

public class KPIChangeDayListener implements KPIChangeDayEvent
{
	private Handler handler;
	KPI_TYPE kpi_type;
	private String curday;

	public KPIChangeDayListener(Handler handler, KPI_TYPE kpi_type)
	{
		this.handler = handler;
		this.kpi_type = kpi_type;
	}

	public String getCurDay()
	{
		return ClickableKPIRichText.getCurDay();
	}

	public void performChange()
	{
		performChange(getCurDay());
	}

	@Override
	public void performChange(String kpi_date)
	{
		handler.sendEmptyMessage(MainActivity.MESSAGE_KPI_CHANGE_CODE);
		curday = kpi_date;
		switch (kpi_type)
		{
		case HIS:
			postHistory(kpi_date);
			break;
		case SEL:
			postSelected(kpi_date);
		}
	}

	private void postSelected(final String kpi_date)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				final Set<SelectedWord> findSelectedWordsToday = KPIService.getInstance().findSelectedWords(kpi_date);
				Iterator<SelectedWord> iterator = findSelectedWordsToday.iterator();
				String tpContent = "";
				while (iterator.hasNext())
				{
					SelectedWord next = iterator.next();
					String mean = next.getCn_mean();
					if (mean == null)
					{
						mean = "";
					}
					tpContent += next.getWord() + "  " + mean.replace("\n", "\n    ") + "\n\n";
				}
				if (tpContent.length() > 2)
				{
					tpContent = tpContent.substring(0, tpContent.length() - 2);
				}
				Message msg = new Message();
				msg.what = MainActivity.MESSAGE_KPI_SEL_CODE;
				msg.obj = tpContent;
				handler.sendMessage(msg);
			}
		}).start();

	}

	private void postHistory(final String kpi_date)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Iterator<ViewedNews> iterator = KPIService.getInstance().findHistory(kpi_date).iterator();
				String tpContent = "";
				while (iterator.hasNext())
				{
					ViewedNews next = iterator.next();
					final String timeToText = TimeUtil.timeToText(next.getView_duration());
					tpContent += "<p>浏览:" + next.getView_time() + "  用时:" + timeToText + "<br>";
					tpContent += "<font color=blue><a href=\"" + next.getUrl() + "\">" + next.getTitle() + "</a></font></p>";
				}
				if (tpContent.length() > 2)
				{
					tpContent = tpContent.substring(0, tpContent.length() - "</br>".length() * 2);
				}
				Message msg = new Message();
				msg.what = MainActivity.MESSAGE_KPI_HIS_CODE;
				msg.obj = tpContent;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
