package com.wnc.news.engnews.ui;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.mine.zhibo8.Zb8News;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.uihelper.gesture.CtrlableDoubleClickGestureDetectorListener;
import common.uihelper.gesture.MyCtrlableGestureDetector;

@SuppressLint({ "DefaultLocale", "HandlerLeak" })
public class Zb8Activity extends BaseNewsActivity implements CtrlableDoubleClickGestureDetectorListener
{

	private static final int MESSAGE_TOPIC_COUNT_CODE = 999;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		setContentView(main);

		this.gestureDetector = new GestureDetector(this, new MyCtrlableGestureDetector(this, 0.5, 0, null, null).setDclistener(this));

		log = Logger.getLogger(Zb8Activity.class);
		if (news_info != null)
		{
			setNewsTitle(news_info.getTitle());
			initData();
		}
	}

	Thread dataThread1;

	@Override
	protected void initData()
	{
		if (dataThread1 != null)
		{
			dataThread1.interrupt();
		}
		dataThread1 = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				msg.what = MESSAGE_ON_IMG_TEXT;
				msg.obj = new WebImgText("<img src=\"" + news_info.getHead_pic() + "\"/>").getCharSequence();
				if (Thread.currentThread().isInterrupted())
				{
					System.out.println("should stop thread1 no send msg");
					return;
				}
				handler.sendMessage(msg);
			}
		});
		dataThread1.setDaemon(true);
		dataThread1.start();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					timeCountBegin();
					String html_content = news_info.getHtml_content();
					log.info(html_content);
					sendContentMsg(new HtmlRichText(html_content).getCharSequence());
					totalWords = PatternUtil.getAllPatternGroup(news_info.getHtml_content(), "['\\w]+").size();

					new CETTopicCache().splitArticle(html_content.replaceAll("<[^>].*+>", ""), allFind);
					log.info(totalWords);
					if (hasTopics())
					{
						handler.sendEmptyMessage(MESSAGE_TOPIC_COUNT_CODE);
					}
				}
				catch (Exception e)
				{
					log.error("initData", e);
				}
			}

		}).start();

	}

	private void sendContentMsg(CharSequence obj)
	{
		Message msg = new Message();
		msg.what = MESSAGE_ON_CONTEXT_TEXT;
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	@Override
	public void dispatchMsg(Message msg)
	{
		switch (msg.what)
		{
		case MESSAGE_ON_CONTEXT_TEXT:
			mTextView.setText((CharSequence) msg.obj);
			break;
		case MESSAGE_TOPIC_COUNT_CODE:
			showTopicCounts();
			break;
		}
	}

	private boolean is_eng = true;

	@Override
	public void newsMenuSetting()
	{

	}

	@Override
	public void doDoubleClick(MotionEvent e)
	{
		Zb8News news = (Zb8News) news_info;
		if (!is_eng)
		{
			sendContentMsg(new HtmlRichText(news.getEng_content()).getCharSequence());
			is_eng = true;
		}
		else
		{
			sendContentMsg(new HtmlRichText(news.getChs_content()).getCharSequence());
			is_eng = false;
		}
	}

}