package com.wnc.news.engnews.ui;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.common.Comment;
import com.wnc.news.api.mine.zhibo8.Zb8News;
import com.wnc.news.api.mine.zhibo8.comments_analyse.Zb8CommentsAnalyseTool;
import com.wnc.news.dao.Zb8Dao;
import com.wnc.news.db.DatabaseManager_ZB8;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.uihelper.gesture.CtrlableDoubleClickGestureDetectorListener;
import common.uihelper.gesture.MyCtrlableGestureDetector;

@SuppressLint({ "DefaultLocale", "HandlerLeak" })
public class Zb8Activity extends BaseNewsActivity implements CtrlableDoubleClickGestureDetectorListener
{
	Zb8News zb8News;
	List<Comment> comments;
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
		zb8News = (Zb8News) news_info;
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
				msg.obj = new WebImgText("<img src=\"" + zb8News.getHead_pic() + "\"/>").getCharSequence();
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
					SQLiteDatabase db = DatabaseManager_ZB8.getInstance().openDatabase();
					timeCountBegin();
					String html_content = zb8News.getEng_content();
					comments = Zb8Dao.findComments(zb8News.getDb_id());
					if (comments.size() < 5)
					{
						try
						{
							Zb8CommentsAnalyseTool tool = new Zb8CommentsAnalyseTool(zb8News.getUrl());
							zb8News.setComments(tool.getAllCommentCount());
							zb8News.setHotComments(tool.getHotCommentCount());
							zb8News.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());
							if (tool.getHotCommentCount() > 0 && tool.getAllCommentCount() > 0)
							{
								List<Comment> top5Comments = tool.getTop5Comments(5);

								Zb8Dao.updateNews(db, zb8News);
								for (Comment comment : top5Comments)
								{
									comment.setArticleId(zb8News.getId());
									Zb8Dao.insertComment(db, comment);
								}
								comments = top5Comments;

							}
						}
						catch (Exception e)
						{
							log.error(zb8News.getUrl() + " 生成评论内容失败!", e);
							e.printStackTrace();
						}
						finally
						{
							DatabaseManager_ZB8.getInstance().closeDatabase();
						}
					}
					sendContentMsg(new HtmlRichText(html_content).getCharSequence());
					totalWords = PatternUtil.getAllPatternGroup(zb8News.getHtml_content(), "['\\w]+").size();

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
		if (!is_eng)
		{
			sendContentMsg(new HtmlRichText(zb8News.getEng_content()).getCharSequence());
			is_eng = true;
		}
		else
		{
			log.info("评论:" + comments);
			sendContentMsg(new HtmlRichText(zb8News.getChs_content() + "<p>热评(" + comments.size() + "):</p><div>" + StringUtils.join(comments, "</div><div>") + "</div>").getCharSequence());
			is_eng = false;
		}
	}

}