package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import word.Topic;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.api.common.Comment;
import com.wnc.news.api.common.DirectLinkNewsFactory;
import com.wnc.news.api.common.ErrSiteNewsInfo;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.mine.hupu.HpNewsExtract;
import com.wnc.news.api.mine.zhibo8.HtmlContentHelper;
import com.wnc.news.api.mine.zhibo8.NewsExtract;
import com.wnc.news.api.mine.zhibo8.NewsFilter;
import com.wnc.news.api.mine.zhibo8.SportType;
import com.wnc.news.api.mine.zhibo8.Zb8News;
import com.wnc.news.api.mine.zhibo8.comments_analyse.Zb8CommentsAnalyseTool;
import com.wnc.news.dao.Zb8Dao;
import com.wnc.news.db.DatabaseManager_ZB8;
import com.wnc.news.engnews.helper.CBService;
import com.wnc.news.engnews.helper.NewsTest;
import com.wnc.news.engnews.kpi.AssortKPI;
import com.wnc.news.engnews.kpi.ClickableKPIRichText;
import com.wnc.news.engnews.kpi.KPIChangeDayListener;
import com.wnc.news.engnews.kpi.KPIData;
import com.wnc.news.engnews.kpi.KPIService;
import com.wnc.news.engnews.kpi.KPI_TYPE;
import com.wnc.news.richtext.ClickableMovementMethod;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ClipBoardUtil;
import common.app.ConfirmUtil;
import common.app.SysInit;
import common.app.ToastUtil;
import common.uihelper.PositiveEvent;
import common.utils.JsoupHelper;
import common.utils.NewsUrlUtil;
import common.utils.TimeUtil;

@SuppressLint("HandlerLeak")
public class MainActivity extends BaseVerActivity implements OnClickListener,
		UncaughtExceptionHandler
{
	private Button bt_pick;
	private Button btn_cache1, btn_cache2, btn_cache_clear;
	private TextView proTv;

	private final int MESSAGE_EXIT_CODE = 0;
	private final int MESSAGE_PROCESS_CODE = 1;
	private final int MESSAGE_DIRECTLINK_CODE = 2;
	public static final int MESSAGE_KPI_ALL_CODE = 3;
	public static final int MESSAGE_KPI_HIS_CODE = 4;
	public static final int MESSAGE_KPI_SEL_CODE = 5;
	public static final int MESSAGE_KPI_FAV_CODE = 6;
	public static final int MESSAGE_KPI_CHANGE_CODE = 7;
	public static final int MESSAGE_TOAST_CODE = 8;

	EditText linkEt;
	Logger log = Logger.getLogger(MainActivity.class);

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);
		Thread.setDefaultUncaughtExceptionHandler(this);

		initView();
		initDialog();

		SysInit.init(MainActivity.this);
		log.info("App Start... ");
		cbListen();

		System.out.println(Zb8Dao.getLastUpdateTime(1));
		System.out.println(Zb8Dao.getLastUpdateTime(2));

		// initEngNews();
		// inittwitter();
		// new MaFengWoCityWrap(MaFengWoCityWrap.JING_DIAN).grabData();
		// new MaFengWoCityWrap(MaFengWoCityWrap.MEI_SHI).grabData();
		// new MaFengWoCityWrap(MaFengWoCityWrap.GOU_WU).grabData();

	}

	private void inittwitter()
	{
		// TODO Auto-generated method stub
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Document documentResult = JsoupHelper
							.getDocumentResult("http://www.twitter.com");
					System.out.println(documentResult);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void initEngNews()
	{
		final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(4);

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{

					String lastZbday = Zb8Dao.getLastUpdateTime(1).substring(0,
							10);
					List<Zb8News> zuqiuNewsByDay = new NewsExtract()
							.getNewsAfterDay(lastZbday, SportType.Zuqiu);
					zuqiuNewsByDay.addAll(new NewsExtract().getNewsAfterDay(
							lastZbday, SportType.NBA));

					String lastHptime = Zb8Dao.getLastUpdateTime(2);
					zuqiuNewsByDay.addAll(new HpNewsExtract()
							.getZb8NewsAfterDateTime(lastHptime,
									SportType.Soccer));
					zuqiuNewsByDay.addAll(new HpNewsExtract()
							.getZb8NewsAfterDateTime(lastHptime, SportType.NBA));
					List<Zb8News> filterOutSide = NewsFilter
							.filterOutSide(zuqiuNewsByDay);
					Collections.sort(filterOutSide, new Comparator<Zb8News>()
					{

						@Override
						public int compare(Zb8News arg0, Zb8News arg1)
						{
							if (arg0.getNews_time() == null)
							{
								return -1;
							}
							if (arg1.getNews_time() == null)
							{
								return 1;
							}
							return arg0.getNews_time().compareTo(
									arg1.getNews_time());
						}

					});
					for (final Zb8News zb8News : filterOutSide)
					{
						executor.execute(new Runnable()
						{

							@Override
							public void run()
							{
								SQLiteDatabase db = DatabaseManager_ZB8
										.getInstance().openDatabase();
								newsContentInit(db, zb8News);
								DatabaseManager_ZB8.getInstance()
										.closeDatabase();
							}
						});
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			private void newsContentInit(SQLiteDatabase db, Zb8News zb8News)
			{
				log.info(zb8News.getTitle() + " " + zb8News.getNews_time()
						+ " " + zb8News.getFrom_name() + " " + zb8News.getUrl());
				String url = zb8News.getUrl();
				if (!Zb8Dao.isExistUrl(db, url))
				{
					HtmlContentHelper htmlContentHelper = new HtmlContentHelper();
					htmlContentHelper.initEngHtmlContent(zb8News);
					htmlContentHelper.initChsHtmlContent(zb8News);
					if (zb8News.getEng_content() != null)
					{
						String splitArticle = new CETTopicCache().splitArticle(
								zb8News.getEng_content(),
								new ArrayList<Topic>());
						zb8News.setEng_content(splitArticle);
					}
					int articleId = Zb8Dao.insertSingleZb8News(db, zb8News);
					if (articleId == 0)
					{
						return;
					}

					// 寻找评论
					try
					{
						if (NewsUrlUtil.isHupuUrl(zb8News.getUrl()))
						{
							return;
						}
						Zb8CommentsAnalyseTool tool = new Zb8CommentsAnalyseTool(
								zb8News.getUrl());
						zb8News.setComments(tool.getAllCommentCount());
						zb8News.setHotComments(tool.getHotCommentCount());
						zb8News.setUpdate_time(BasicDateUtil
								.getCurrentDateTimeString());
						if (tool.getHotCommentCount() > 0
								&& tool.getAllCommentCount() > 0)
						{
							List<Comment> top5Comments = tool
									.getTop5Comments(5);
							Zb8Dao.updateNews(db, zb8News);
							for (Comment comment : top5Comments)
							{
								comment.setArticleId(articleId);
								Zb8Dao.insertComment(db, comment);
							}
						}
					}
					catch (Exception e)
					{
						log.error(zb8News.getUrl() + " 生成评论内容失败!", e);
						e.printStackTrace();
					}
				}
				else
					log.info(url + " 已经存在...");
			}
		}).start();
	}

	private void cbListen()
	{
		Intent startIntent = new Intent(this, CBService.class);
		startService(startIntent);// 启动服务
		// final ClipboardManager clipboardManager = (ClipboardManager)
		// getSystemService(Context.CLIPBOARD_SERVICE);
		// clipboardManager
		// .addPrimaryClipChangedListener(new OnPrimaryClipChangedListener()
		// {
		//
		// @Override
		// public void onPrimaryClipChanged()
		// {
		// String content = clipboardManager.getPrimaryClip()
		// .getItemAt(0).getText().toString();
		// log.info(content);
		// }
		// });
		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// while (true)
		// {
		// System.out.println("run...");
		// handler.sendEmptyMessage(990);
		// try
		// {
		// TimeUnit.SECONDS.sleep(5);
		// }
		// catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
		// }
		// }).start();
	}

	private void bgUpdate()
	{
		kPIChangeDayListener.updateKPIHeader();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (BasicDateUtil.getCurrentHour() > 7
						&& BasicDateUtil.getCurrentHour() < 10)
				{
					new NewsTest().topicUpdate();
				}
				// NewsDao.test();
				// new CETTopicUpdate().updateCounts();
				// new NewsTest().deleteEmptyDate();
				// NewsInfo newsFromUrl = new IyubaApi()
				// .getNewsFromUrl("http://voa.iyuba.com/audioitem_standard_803.html");
			}
		}).start();
	}

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 990:
				String clipBoardContent = ClipBoardUtil
						.getClipBoardContent(getApplicationContext());
				log.info(clipBoardContent);
				break;
			case MESSAGE_PROCESS_CODE:
				proTv.setText(msg.obj.toString());
				break;
			case MESSAGE_EXIT_CODE:
				isExit = false;
				break;
			case MESSAGE_DIRECTLINK_CODE:
				NewsContentActivity.news_info = (NewsInfo) msg.obj;
				startActivity(new Intent(getApplicationContext(),
						NewsContentActivity.class));
				break;
			case MESSAGE_KPI_ALL_CODE:
				KPIData data = (KPIData) msg.obj;
				((TextView) findViewById(R.id.tv_head_fav)).setText(data
						.getLoved_news() + "");
				((TextView) findViewById(R.id.tv_head_sel)).setText(data
						.getSelectedWords() + "");
				((TextView) findViewById(R.id.tv_head_his)).setText(data
						.getViewed_news() + "");
				((TextView) findViewById(R.id.tv_head_tim)).setText(TimeUtil
						.timeToText(data.getTimes()));
				((TextView) findViewById(R.id.tv_head_wor)).setText(data
						.getTopicWords() + "");
				break;
			case MESSAGE_KPI_FAV_CODE:
			case MESSAGE_KPI_HIS_CODE:
			case MESSAGE_KPI_SEL_CODE:
				AssortKPI assortKPI = (AssortKPI) msg.obj;
				tvKPI_item_desc.setText("");
				tvKPI_item_desc.append(new ClickableKPIRichText(true,
						kPIChangeDayListener).getCharSequence());
				tvKPI_item_desc.append("        " + assortKPI.getDate() + " ("
						+ assortKPI.getCount() + ")        ");
				tvKPI_item_desc.append(new ClickableKPIRichText(false,
						kPIChangeDayListener).getCharSequence());
				tvKPI_item_desc.append("\n");
				for (CharSequence content : assortKPI.getContents())
				{
					tvKPI_item_desc.append(content);
				}
				tvKPI_item_desc.setMovementMethod(ClickableMovementMethod
						.getInstance());
				// tvKPI_item_desc.scrollTo(0, 0);
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						// scrollTo达不到效果
						scrollView_news_content.smoothScrollTo(0, 0);
					}
				});
				break;
			case MESSAGE_KPI_CHANGE_CODE:
				tvKPI_item_desc.setText("\n\n\n正在寻找数据...\n\n\n");
				break;
			case MESSAGE_TOAST_CODE:
				ToastUtil.showLongToast(getApplicationContext(),
						msg.obj.toString());
				break;
			}
		}
	};
	KPIChangeDayListener kPIChangeDayListener = new KPIChangeDayListener(
			handler, KPI_TYPE.ALL);
	TextView tvKPI_item_desc;

	Button bt_web_parse;
	Dialog dialog;
	private ScrollView scrollView_news_content;

	private void initView()
	{
		linkEt = (EditText) findViewById(R.id.et_pick_url);
		proTv = (TextView) findViewById(R.id.tv_cache_process);
		proTv.setMovementMethod(ScrollingMovementMethod.getInstance());

		bt_pick = (Button) findViewById(R.id.btn_pick);
		bt_web_parse = (Button) findViewById(R.id.btn_web_parse);

		btn_cache1 = (Button) findViewById(R.id.btn_cache_team);
		btn_cache2 = (Button) findViewById(R.id.btn_cache_forums);

		btn_cache_clear = (Button) findViewById(R.id.btn_cache_clear);

		bt_pick.setOnClickListener(this);
		bt_web_parse.setOnClickListener(this);

		btn_cache1.setOnClickListener(this);
		btn_cache2.setOnClickListener(this);
		btn_cache_clear.setOnClickListener(this);
		findViewById(R.id.btn_theme).setOnClickListener(this);
		findViewById(R.id.btn_search).setOnClickListener(this);
		findViewById(R.id.imgbutton_head_sel).setOnClickListener(this);
		findViewById(R.id.imgbutton_head_his).setOnClickListener(this);
		findViewById(R.id.imgbutton_head_fav).setOnClickListener(this);
	}

	private void initDialog()
	{
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		dialog.setContentView(R.layout.topic_tip_ndailog);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = (int) (0.85 * BasicPhoneUtil.getScreenWidth(this));
		lp.height = (int) (0.85 * BasicPhoneUtil.getScreenHeight(this));
		tvKPI_item_desc = (TextView) dialog.findViewById(R.id.tvTopicInfo);
		scrollView_news_content = (ScrollView) dialog
				.findViewById(R.id.scrollView_news_content);
	}

	private void showKPIDialog(KPI_TYPE kpi_type)
	{
		kPIChangeDayListener = new KPIChangeDayListener(handler, kpi_type);
		tvKPI_item_desc.append("\n\n\n正在寻找数据...\n\n\n");
		kPIChangeDayListener.performChange();
		dialog.show();
	}

	Thread cacheWatchThread1;
	Thread cacheWatchThread2;
	public volatile boolean cachedExit = false;

	@Override
	public void onClick(View v)
	{
		final CacheSchedule cacheSchedule = new CacheSchedule();
		switch (v.getId())
		{
		case R.id.imgbutton_head_sel:
			showKPIDialog(KPI_TYPE.SEL);
			break;
		case R.id.imgbutton_head_fav:
			System.out.println("fav...........");
			showKPIDialog(KPI_TYPE.FAV);
			break;
		case R.id.imgbutton_head_his:
			showKPIDialog(KPI_TYPE.HIS);
			break;
		case R.id.btn_theme:
			startActivity(new Intent(this, TabsActivity.class));
			break;
		case R.id.btn_search:
			startActivity(new Intent(this, SearchActivity.class));
			break;
		case R.id.btn_pick:
			new CacheSchedule().voaCache();
			startActivity(new Intent(this, VoaActivity.class));
			break;
		case R.id.btn_web_parse:
			parseWebHtml();
			break;
		case R.id.btn_cache_team:
			System.out.println("开始缓存!");
			if (cacheWatchThread1 != null)
			{
				cacheWatchThread1.interrupt();
			}
			new NewsTest().cacheTeams(cacheSchedule);
			cacheWatchThread1 = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					while (!cachedExit)
					{
						List<String> list = cacheSchedule.getMap();
						String process = "";

						final boolean allCached = cacheSchedule.isAllCached();
						int sum = 0;
						for (String s : list)
						{
							process += s + "  \n";
							sum += BasicNumberUtil.getNumber(PatternUtil
									.getFirstPattern(s, "\\d+"));
						}
						String head = "";
						Message msg = new Message();
						msg.what = MESSAGE_PROCESS_CODE;

						if (allCached)
						{
							head = " 缓存全部结束(" + sum + ")\n";
							msg.obj = head + process;
							handler.sendMessage(msg);
							break;
						}
						else
						{
							head = "正在缓存... (" + sum + ")\n";
							msg.obj = head + process;
							handler.sendMessage(msg);
						}
						try
						{
							Thread.sleep(5000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			cacheWatchThread1.setDaemon(true);
			cacheWatchThread1.start();
			break;
		case R.id.btn_cache_forums:
			System.out.println("开始缓存!");
			if (cacheWatchThread2 != null)
			{
				cacheWatchThread2.interrupt();
			}
			new NewsTest().cacheFormus(cacheSchedule);
			cacheWatchThread2 = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					while (!cachedExit)
					{
						List<String> list = cacheSchedule.getMap();
						String process = "";

						final boolean allCached = cacheSchedule.isAllCached();
						int sum = 0;
						for (String s : list)
						{
							process += s + "  \n";
							sum += BasicNumberUtil.getNumber(PatternUtil
									.getFirstPattern(s, "\\d+"));
						}
						String head = "";
						Message msg = new Message();
						msg.what = MESSAGE_PROCESS_CODE;

						if (allCached)
						{
							head = " 缓存全部结束(" + sum + ")\n";
							msg.obj = head + process;
							handler.sendMessage(msg);
							break;
						}
						else
						{
							head = "正在缓存... (" + sum + ")\n";
							msg.obj = head + process;
							handler.sendMessage(msg);
						}
						try
						{
							Thread.sleep(5000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			cacheWatchThread2.setDaemon(true);
			cacheWatchThread2.start();
			break;
		case R.id.btn_cache_clear:
			ConfirmUtil.confirmDelete(this, "确定要清除缓存吗?", new PositiveEvent()
			{
				@Override
				public void onConfirmPositive()
				{
					final CacheSchedule cacheSchedule2 = new CacheSchedule();
					cacheSchedule2.clearCache();
					System.out.println("缓存清空!");
				}
			});
			break;
		default:
			break;
		}
	}

	private void parseWebHtml()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String url = "http://forums.realgm.com/boards/viewtopic.php?f=6&t=1486328";
				url = "https://www.reddit.com/r/NBASpurs/comments/59fpi7/careerhighkawhi/";
				url = linkEt.getText().toString();
				if (BasicStringUtil.isNullString(url))
				{
					return;
				}
				Message msg = new Message();
				msg.what = MESSAGE_TOAST_CODE;
				msg.obj = "正在解析网页!";
				handler.sendMessage(msg);
				final NewsInfo newsFromUrl = DirectLinkNewsFactory
						.getNewsFromUrl(url);
				if (newsFromUrl instanceof ErrSiteNewsInfo)
				{
					msg = new Message();
					msg.what = MESSAGE_TOAST_CODE;
					log.error("不支持该网站!");
					msg.obj = "不支持该网站!";
					handler.sendMessage(msg);
				}
				else if (newsFromUrl != null
						&& newsFromUrl.getHtml_content() != null
						&& newsFromUrl.getHtml_content().length() > 200)
				{
					msg = new Message();
					msg.what = MESSAGE_DIRECTLINK_CODE;
					msg.obj = newsFromUrl;
					handler.sendMessage(msg);
				}
				else
				{
					msg = new Message();
					log.error(url + "解析有误!");
					msg.what = MESSAGE_TOAST_CODE;
					msg.obj = "解析有误!";
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable ex)
	{
		log.error("uncaughtException   ", ex);
	}

	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit()
	{
		if (!isExit)
		{
			isExit = true;
			ToastUtil.showShortToast(this, "再按一次退出程序");
			// 利用handler延迟发送更改状态信息
			handler.sendEmptyMessageDelayed(MESSAGE_EXIT_CODE, 2000);
		}
		else
		{
			finish();
			System.exit(0);
		}
	}

	@Override
	protected void onDestroy()
	{
		KPIService.getInstance().closeDb();
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		bgUpdate();
	}
}
