package com.wnc.news.engnews.kpi;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.richtext.ClickableImageSpan;
import com.wnc.news.richtext.RichText;
import common.uihelper.MyAppParams;

public class ClickableKPIRichText implements RichText
{
	boolean isPre = false;
	private static String curday = BasicDateUtil.getCurrentDateString();
	KPIChangeDayEvent kPIChangeDayEvent;

	public ClickableKPIRichText(boolean isPre, KPIChangeDayEvent kPIChangeDayEvent)
	{
		this.isPre = isPre;
		this.kPIChangeDayEvent = kPIChangeDayEvent;
	}

	public static String getCurDay()
	{
		return curday;
	}

	public interface KPIChangeDayEvent
	{
		void performChange(String kpi_date);
	}

	@Override
	public CharSequence getCharSequence()
	{
		final Resources resources = MyAppParams.getInstance().getResources();
		int pic_id = resources.getIdentifier("icon_next", "drawable", MyAppParams.getInstance().getPackageName());
		if (isPre)
		{
			pic_id = resources.getIdentifier("icon_previous", "drawable", MyAppParams.getInstance().getPackageName());
		}
		Drawable drawable = resources.getDrawable(pic_id);
		// System.out.println(drawable.getIntrinsicWidth());
		drawable.setBounds(0, 0, 80, 80);
		ImageSpan imgSpan = new ClickableImageSpan(drawable)
		{

			@Override
			public void onClick(View view)
			{
				if (isPre)
				{
					curday = BasicDateUtil.getDateBeforeDayDateString(curday, 1);
					System.out.println("前一天" + curday);
				}
				else
				{
					curday = BasicDateUtil.getDateAfterDayDateString(curday, 1);
					System.out.println("后一天" + curday);
				}
				kPIChangeDayEvent.performChange(curday);
			}
		};
		SpannableString spanString = new SpannableString("icon");
		spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

}
