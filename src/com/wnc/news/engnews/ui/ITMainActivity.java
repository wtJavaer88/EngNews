package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Stack;

import net.selectabletv.SelectableTextView;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import word.DicWord;
import word.Topic;
import word.WordExpand;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.engnews.helper.ActivityMgr;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.OptedDictData;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.network.CibaCacheHelper;
import com.wnc.news.engnews.network.WebUrlHelper;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import com.wnc.tools.TextFormatUtil;
import common.app.BasicPhoneUtil;
import common.app.ClipBoardUtil;
import common.app.MessagePopWindow;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.utils.UrlPicDownloader;

@SuppressLint({ "HandlerLeak", "ClickableViewAccessibility" })
public class ITMainActivity extends BaseVerActivity implements OnClickListener,
		UncaughtExceptionHandler
{
	private static final int MESSAGE_ON_DOWNSOUND_ERROR_TEXT = 201;
	private static final int MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT = 202;

	private static final int MESSAGE_ON_DISPLAY_MEANING = 301;
	private static final int MESSAGE_ON_DISPLAY_MEANING_NULL = 302;

	private EditText wordEt;
	private Button wordClearBt;
	private Button explainBt;
	private Button itKpiBt;
	private Button btn_it_readbook;
	private Button toNewsBt;
	private TextView wordTipTv;
	ListView listView;
	Logger log = Logger.getLogger(ITMainActivity.class);
	private static Stack<String> wordNoticeStack = new Stack<String>();

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_itmain);
		Thread.setDefaultUncaughtExceptionHandler(this);

		initView();

		initClipListener();
		initMenu();
	}

	WordMenuPopWindow wordMenuPopWindow;
	int mTouchX2;
	int mTouchY2;

	private void initMenu()
	{
		wordMenuPopWindow = new WordMenuPopWindow(this, new WordMenuListener()
		{
			@Override
			public void doSound()
			{

				final String voicePath = MyAppParams.VOICE_FOLDER
						+ getCurrentWord() + ".mp3";
				if (BasicFileUtil.isExistFile(voicePath))
				{
					SrtVoiceHelper.play(voicePath);
				}
				else
				{
					common.app.ToastUtil.showShortToast(
							getApplicationContext(), "找不到声音文件, 正在为你下载!");
					// messagePopWindow
					// .setMsgAndShow("找不到声音文件,正在为你下载!", mTextView);

					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								Thread.sleep(1000);
								final String soundUrl = CibaCacheHelper
										.getCibaTranslate(getCurrentWord())
										.getSoundStr();
								UrlPicDownloader.download(soundUrl, voicePath);
							}
							catch (Exception e)
							{
								handler.sendEmptyMessage(MESSAGE_ON_DOWNSOUND_ERROR_TEXT);
								e.printStackTrace();
							}
							if (BasicFileUtil.getFileSize(voicePath) == 0)
							{
								BasicFileUtil.deleteFile(voicePath);
							}
							else
							{
								handler.sendEmptyMessage(MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT);
							}
						}
					}).start();

				}
			}

			@Override
			public void doCopy()
			{
				common.app.ClipBoardUtil.setNormalContent(
						getApplicationContext(), getCurrentWord());
				common.app.ToastUtil.showShortToast(getApplicationContext(),
						"操作成功!");
			}

			@Override
			public void toNet()
			{
				ActivityMgr.gotoIE(ITMainActivity.this,
						WebUrlHelper.getWordUrl(getCurrentWord()));
			}

			@Override
			public void doPassTopic()
			{
				BasicFileUtil.writeFileString(MyAppParams.PASS_TXT,
						getCurrentWord() + "\r\n", "UTF-8", true);
				PassedTopicCache.getPassedTopics().add(getCurrentWord());
				common.app.ToastUtil.showShortToast(getApplicationContext(),
						"操作成功!");
				log.info("Pass: " + getCurrentWord());
			}

			@Override
			public void doExpand()
			{
				final Dialog dialog = new Dialog(ITMainActivity.this,
						R.style.CustomDialogStyle);
				dialog.setContentView(R.layout.topic_tip_wdailog);
				dialog.setCanceledOnTouchOutside(true);
				Window window = dialog.getWindow();

				WindowManager.LayoutParams lp = window.getAttributes();
				lp.width = (int) (0.85 * BasicPhoneUtil
						.getScreenWidth(ITMainActivity.this));
				lp.height = (int) (0.85 * BasicPhoneUtil
						.getScreenHeight(ITMainActivity.this));
				lp.verticalMargin = 20;

				final SelectableTextView tvTopic = (SelectableTextView) dialog
						.findViewById(R.id.tvTopicInfo);

				tvTopic.setMovementMethod(LinkMovementMethod.getInstance());
				tvTopic.setOnTouchListener(new View.OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						mTouchX2 = (int) event.getX();
						mTouchY2 = (int) event.getY();
						return false;
					}
				});
				tvTopic.setOnLongClickListener(new View.OnLongClickListener()
				{
					@Override
					public boolean onLongClick(View v)
					{
						tvTopic.setMovementMethod(ScrollingMovementMethod
								.getInstance());

						int start = tvTopic
								.getPreciseOffset(mTouchX2, mTouchY2);

						if (start > -1)
						{
							String selectedText = NewsContentUtil
									.getSuitWordAndSetPos(tvTopic, start);
							log.info("selectedText:" + selectedText);
							ClipBoardUtil.setNormalContent(
									getApplicationContext(), selectedText);
						}
						return true;
					}
				});
				tvTopic.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View arg0)
					{
						tvTopic.setMovementMethod(LinkMovementMethod
								.getInstance());
					}
				});

				if (OptedDictData.getSeekWordList().size() > 0)
				{
					final Integer topic_id = OptedDictData.getSeekWordList()
							.peek().getTopic_id();
					if (OptedDictData.getWordExpandContentMap().containsKey(
							topic_id))
					{
						tvTopic.setText(OptedDictData.getWordExpandContentMap()
								.get(topic_id));
						dialog.show();
					}
					else
					{
						WordExpand wordExpand = DictionaryDao
								.findSameAntonym(OptedDictData
										.getSeekWordList().peek().getTopic_id());

						if (wordExpand != null)
						{
							try
							{
								String article = "<p>"
										+ wordExpand.toString().replace("\n",
												"<br>") + "</p>";
								String splitArticle = new CETTopicCache()
										.splitArticle(article,
												new ArrayList<Topic>());
								final CharSequence charSequence = new HtmlRichText(
										splitArticle).getCharSequence();
								OptedDictData.addWordExpand(topic_id,
										charSequence);
								tvTopic.setText(charSequence);
								dialog.show();
							}
							catch (Exception e)
							{
								log.error("wordExpand", e);
								e.printStackTrace();
							}
						}
						else
						{
							ToastUtil.showShortToast(getApplicationContext(),
									"找不到扩展的内容!");
						}
					}
				}

			}
		});
	}

	private String getCurrentWord()
	{

		String string = wordEt.getText().toString();
		if (StringUtils.isEmpty(string))
		{
			string = ClipBoardUtil.getClipBoardContent(getApplicationContext());
		}
		return string;
	}

	private void displayMeaning(final String word)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				DicWord findWord = findDicWord(word);
				if (findWord != null)
				{
					Message msg = new Message();
					msg.what = MESSAGE_ON_DISPLAY_MEANING;
					msg.obj = findWord;

					handler.sendMessage(msg);
				}
				else
				{
					handler.sendEmptyMessage(MESSAGE_ON_DISPLAY_MEANING_NULL);
				}
			}
		});

	}

	private void showWordMenu()
	{
		displayMeaning(this.wordEt.getText().toString().trim());
		wordMenuPopWindow.showPopupWindow(explainBt);
	}

	private void initClipListener()
	{
		new Thread(new Runnable()
		{
			String last = "";

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}

					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							String clipBoardContent = ClipBoardUtil
									.getClipBoardContent(
											getApplicationContext()).trim();
							if (isSequence(clipBoardContent))
							{
								writeToBookLog(clipBoardContent);
							}
							else if (isSingleWord(clipBoardContent))
							{
								try
								{
									DicWord findWord = findDicWord(clipBoardContent);
									if (findWord != null)
									{
										findSuccess(clipBoardContent, findWord);
									}
									else
									{
										findFailed(clipBoardContent);
									}

								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
							last = clipBoardContent;
						}

						private boolean isSequence(String clipBoardContent)
						{
							return StringUtils.isNotBlank(last)
									&& !clipBoardContent.equals(last)
									&& !clipBoardContent.startsWith("http")
									&& !TextFormatUtil
											.containsChinese(clipBoardContent)
									&& PatternUtil.getAllPatternGroup(
											clipBoardContent, "([a-zA-Z]+)")
											.size() > 1;
						}

						private boolean isSingleWord(String clipBoardContent)
						{
							return StringUtils.isNotBlank(last)
									&& !clipBoardContent.equals(last)
									&& clipBoardContent
											.matches("[a-zA-Z_\\-]+");
						}

						private void findFailed(String clipBoardContent)
						{
							ToastUtil.showLongToast(getApplicationContext(),
									"无法找到该单词");
							writeToBookLog(clipBoardContent, null, 1);
						}

						private void findSuccess(String clipBoardContent,
								DicWord findWord)
						{
							OptedDictData.addSeekWord(findWord);
							String basicInfo = (findWord.getBase_word() + " " + findWord
									.getCn_mean());

							ToastUtil.showLongToast(getApplicationContext(),
									"翻译:" + basicInfo);

							writeToBookLog(clipBoardContent, findWord, 1);
						}

						private void writeToBookLog(String clipBoardContent)
						{
							writeToBookLog(clipBoardContent, null, 2);
						}

						private void writeToBookLog(String clipBoardContent,
								DicWord object, int type)
						{
							wordNoticeStack.push(clipBoardContent);
							JSONObject jobj = new JSONObject();
							jobj.put("content", clipBoardContent);
							jobj.put("type", type);
							if (object != null)
							{
								jobj.put("topic", object.getTopic_id());
							}
							jobj.put("time",
									BasicDateUtil.getCurrentDateTimeString());

							BasicFileUtil.writeFileString(
									MyAppParams.getInstance().getWorkPath()
											+ "itword.txt", jobj.toJSONString()
											+ "\r\n", null, true);
						}
					});

				}
			}
		}).start();
	}

	private DicWord findFromCache(String clipBoardContent)
	{
		for (DicWord d : OptedDictData.getSeekWordList())
		{
			if (d != null && hasFindWord(clipBoardContent, d))
			{
				return d;
			}
		}
		return null;
	}

	private boolean hasFindWord(String clipBoardContent, DicWord d)
	{
		return clipBoardContent.equalsIgnoreCase(d.getBase_word())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_done())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_er())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_est())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_ing())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_past())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_pl())
				|| clipBoardContent.equalsIgnoreCase(d.getWord_third());
	}

	private DicWord findDicWord(String clipBoardContent)
	{
		DicWord fw = findFromCache(clipBoardContent);
		if (fw == null)
		{
			fw = DictionaryDao.findWord(clipBoardContent);
		}
		return fw;
	}

	private void initView()
	{
		messagePopWindow = new MessagePopWindow(this);
		wordEt = (EditText) findViewById(R.id.et_word);
		wordClearBt = (Button) findViewById(R.id.btn_itword_clear);
		wordClearBt.setOnClickListener(this);
		explainBt = (Button) findViewById(R.id.btn_explain);
		explainBt.setOnClickListener(this);
		itKpiBt = (Button) findViewById(R.id.btn_it_kpi);
		itKpiBt.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.lv_words);
		wordTipTv = (TextView) findViewById(R.id.tv_itword_tip);

		btn_it_readbook = (Button) findViewById(R.id.btn_it_readbook);
		btn_it_readbook.setOnClickListener(this);

		toNewsBt = (Button) findViewById(R.id.btn_news_main);
		toNewsBt.setOnClickListener(this);
	}

	private void createWordListView()
	{
		if (listView != null)
		{
			final String[] data = getDataFromStack();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, data);

			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3)
				{
					wordEt.setText(data[arg2]);
					showWordMenu();
				}
			});
		}
	}

	private String[] getDataFromStack()
	{
		int length = Math.min(wordNoticeStack.size(), 50);
		String[] list = new String[length];
		for (int i = 0; i < length; i++)
		{
			list[i] = wordNoticeStack.get(wordNoticeStack.size() - i - 1);
		}
		return list;
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable ex)
	{
		ex.printStackTrace();
		log.error("uncaughtException   ", ex);
	}

	@Override
	public void onClick(View arg0)
	{
		switch (arg0.getId())
		{
		case R.id.btn_itword_clear:
			clearWordInfo();
			break;
		case R.id.btn_explain:
			showWordMenu();
			break;
		case R.id.btn_it_kpi:
			break;
		case R.id.btn_news_main:
			startActivity(new Intent(getApplicationContext(),
					MainActivity.class));
			break;
		case R.id.btn_it_readbook:

			break;
		}
	}

	private void clearWordInfo()
	{
		this.wordTipTv.setText("");
		this.wordEt.setText("");
	}

	MessagePopWindow messagePopWindow;
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case MESSAGE_ON_DOWNSOUND_ERROR_TEXT:
				messagePopWindow.setMsgAndShow("下载音频异常!", wordEt);
				break;
			case MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT:
				messagePopWindow.setMsgAndShow("下载音频成功!", wordEt);
				break;
			case MESSAGE_ON_DISPLAY_MEANING:
				DicWord dicWord = (DicWord) msg.obj;
				wordTipTv.setText(dicWord.getBase_word() + " "
						+ dicWord.getCn_mean());
				break;
			case MESSAGE_ON_DISPLAY_MEANING_NULL:
				wordTipTv.setText("没有释义可以提供!");
				break;
			}
		}
	};

	protected void onResume()
	{
		super.onResume();
		createWordListView();
	};
}