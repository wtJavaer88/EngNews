package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.selectabletv.SelectableTextView;
import net.selectabletv.SelectableTextView.OnCursorStateChangedListener;

import org.apache.log4j.Logger;

import voa.VINFO;
import voa.VoaNewsInfo;
import word.DicWord;
import word.Topic;
import word.WordExpand;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicRunTimeUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.VoaDao;
import com.wnc.news.engnews.helper.ActivityMgr;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.PlayThread;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.helper.ViewNewsHolder;
import com.wnc.news.engnews.helper.WebUrlHelper;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow.NewsMenuListener;
import com.wnc.news.engnews.ui.popup.SectionPopWindow;
import com.wnc.news.engnews.ui.popup.SectionPopWindow.WordSectionListener;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.ClickableRichText;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ClipBoardUtil;
import common.app.Log4jUtil;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.utils.UrlPicDownloader;

@SuppressLint(
{ "DefaultLocale", "HandlerLeak" })
public class VoaActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler
{
    private SelectableTextView mTextView;
    final int REQUEST_SEARCH_CODE = 1;
    public static VoaNewsInfo news_info;
    View main;

    private final int MESSAGE_PROCESS_CODE = 1;
    private final int MESSAGE_DIRECTLINK_CODE = 2;
    private final int MESSAGE_DOWNLOAD_OK_CODE = 3;
    private final int MESSAGE_DOWNLOAD_ERR_CODE = 4;

    private int mTouchX;
    private int mTouchY;
    private Button topicListBt, wordMenuBtn;
    TextView wordTipTv;
    private ScrollView mScrollView;
    ImageButton newsMenuBtn;

    WordMenuPopWindow wordMenuPopWindow;
    SectionPopWindow sectionPopWindow;
    NewsMenuPopWindow newsMenuPopWindow;

    Logger log = Logger.getLogger(VoaActivity.class);

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        getLayoutInflater();
        main = LayoutInflater.from(this).inflate(R.layout.activity_voa, null);
        hideVirtualBts();
        setContentView(main);

        Thread.setDefaultUncaughtExceptionHandler(this);

        Log4jUtil.configLog(MyAppParams.LOG_FOLDER
                + BasicDateUtil.getCurrentDateString() + ".txt");
        log.info("App Start...");

        initView();
        initData();
    }

    private void initData()
    {
        String json = "[{\"ch\":\"接下来，是一篇反映美国政府观点的社论。\",\"en\":\"Next, an editorial reflecting the views of the United States government.\",\"time\":31},{\"ch\":\"2016年9月28日，美国和印度政府\",\"en\":\"The Governments of the United States and India\",\"time\":77},{\"ch\":\"在印度首都新德里举办了第5届美国-印度网络对话。\",\"en\":\"held the Fifth U.S.-India Cyber Dialogue in New Delhi on September 28, 2016.\",\"time\":101},{\"ch\":\"美印网络对话反映了两国在重要的双边和全球问题上的广泛接触\",\"en\":\"The U.S.-India Cyber Dialogue reflects our nations’ broad engagement\",\"time\":164},{\"ch\":\"和长期合作。\",\"en\":\"and long-standing cooperation on important bilateral and global issues.\",\"time\":207},{\"ch\":\"此次网络对话论坛旨在执行印度-美国网络关系框架，\",\"en\":\"The Cyber Dialogue is a forum for implementing the Framework for the India-U.S. Cyber relationship,\",\"time\":262},{\"ch\":\"特别是交流和探讨国际网络政策、\",\"en\":\"in particular exchanging and discussing international cyber policies,\",\"time\":318},{\"ch\":\"比较国家网络策略、\",\"en\":\"comparing national cyber strategies,\",\"time\":368},{\"ch\":\"增强我们打击网络犯罪的力度、\",\"en\":\"enhancing our efforts to combat cybercrime,\",\"time\":394},{\"ch\":\"并且培养网络建设和研究发展的能力，\",\"en\":\"and fostering capacity building and Research and Development,\",\"time\":425},{\"ch\":\"只有这样才能促进网络安全的实现和数字经济的发展。\",\"en\":\"thus promoting cybersecurity and the digital economy.\",\"time\":463},{\"ch\":\"美国-印度网络对话正在加深一系列网络问题方面的双边合作，\",\"en\":\"The U.S.-India Cyber Dialogue is deepening bilateral cooperation on a wide range of cyber issues\",\"time\":500},{\"ch\":\"以及加强美印战略伙伴关系，\",\"en\":\"and strengthening the U.S.-India strategic partnership\",\"time\":556},{\"ch\":\"通过对网络风险信息和双方都关心的问题进行交流、\",\"en\":\"by exchanging information on cyber threats and issues of mutual concern,\",\"time\":589},{\"ch\":\"并对可能的合作措施进行探讨的方式。\",\"en\":\"and discussing possible cooperative measures.\",\"time\":634},{\"ch\":\"此次对话通过创建一种合作机制来促进双方在执法和网络犯罪领域开展双边合作，\",\"en\":\"It also promotes bilateral cooperation on law enforcement and cybercrime by creating a mechanism for cooperation,\",\"time\":661},{\"ch\":\"其中包括设立适当的亚组并确认打击国际网络犯罪的共同目标。\",\"en\":\"including setting up appropriate sub-groups and affirming common objectives in fighting international cyber crime.\",\"time\":727},{\"ch\":\"这些合作包括在网络空间方面将国际法应用于国家行为、\",\"en\":\"These include the application of international law to state behavior in cyberspace,\",\"time\":797},{\"ch\":\"确认负责任的国家行为的标准、\",\"en\":\"the affirmation of norms of responsible state behavior,\",\"time\":846},{\"ch\":\"并发展实际建立信任的措施。\",\"en\":\"and the development of practical confidence-building measures.\",\"time\":881},{\"ch\":\"这已经是第5次举办同类型的对话，\",\"en\":\"This whole-of-government Cyber Dialogue, fifth in the series,\",\"time\":918},{\"ch\":\"此次全政府网络对话是在美国国家安全委员会网络政策高级官员萨米尔·简恩、\",\"en\":\"was led by the U.S. National Security Council Senior Director for Cyber Policy Samir Jain\",\"time\":953},{\"ch\":\"印度外交部政策规划和全球网络问题联合秘书桑托什·杰哈先生的领导下举办的。\",\"en\":\"and by Shri Santosh Jha, Joint Secretary for Policy Planning and Global Cyber Issues, Ministry of External Affairs.\",\"time\":1010},{\"ch\":\"美国国务院网络问题协调员克里斯多夫·佩因特\",\"en\":\"The Department of State Coordinator for Cyber Issues Christopher Painter\",\"time\":1090},{\"ch\":\"和国家安全委员会秘书处联合秘书阿比曼纽·高希先生联合主办此次对话论坛。\",\"en\":\"and the National Security Council Secretariat Joint Secretary Shri Abhimanyu Ghosh co-hosted the Dialogue.\",\"time\":1126},{\"ch\":\"美国政府机构间代表包括\",\"en\":\"The U.S. government interagency delegation included representatives\",\"time\":1196},{\"ch\":\"来自国务院、国土安全部、商务部和联邦调查局的代表。\",\"en\":\"from the Departments of State, Homeland Security, and Commerce, and the Federal Bureau of Investigation.\",\"time\":1238},{\"ch\":\"印度政府出席的代表来自外交部、\",\"en\":\"The Indian government was represented by Ministry of External Affairs,\",\"time\":1295},{\"ch\":\"电子与信息技术部、交通部、内政部、\",\"en\":\"Ministry of Electronics and Information Technology, Ministry of Communication, Ministry of Home Affairs,\",\"time\":1335},{\"ch\":\"计算机紧急响应小组、国家关键信息基础设施保护中心、\",\"en\":\"Computer Emergency Response Team, National Critical Information Infrastructure Protection Centre,\",\"time\":1394},{\"ch\":\"中央调查和国防研究与发展组织局。\",\"en\":\"Central Bureau of Investigation and Defense Research & Development Organization.\",\"time\":1452},{\"ch\":\"两国决定将于2017年在华盛顿举办下一轮网络对话。\",\"en\":\"The two countries decided to hold the next round of the Cyber Dialogue in Washington in 2017.\",\"time\":1507},{\"ch\":\"网络安全对于当今世界的安全和经济福祉而言是至关重要的。\",\"en\":\"Cyber security is essential to the safety and economic well-being of the modern world.\",\"time\":1568},{\"ch\":\"美国将与其合作伙伴印度以及其他国家一道\",\"en\":\"The U.S. will work with its partner India and other nations\",\"time\":1622},{\"ch\":\"为所有国家和人民都能够获得安全和有保障的网络环境而努力。\",\"en\":\"to achieve a safe and secure internet for all nations and peoples.\",\"time\":1660}]";
        if (news_info != null)
        {
            json = news_info.getHtml_content();
            setNewsTitle(news_info.getTitle());
        }
        else
        {
            news_info = VoaDao.findtest();
        }

        List<VINFO> infos = JSONArray.parseArray(json, VINFO.class);
        for (int i = 0; i < infos.size(); i++)
        {
            VINFO vinfo = infos.get(i);
            final int seektime = vinfo.getTime() * 100;
            int stoptime = (i == (infos.size() - 1) ? seektime : (infos.get(
                    i + 1).getTime() * 100));
            mTextView.append(new ClickableRichText("播放", this, seektime,
                    stoptime).getCharSequence());
            mTextView.append("  ");
            final ArrayList<Topic> allFind2 = new ArrayList<Topic>();
            mTextView.append(new HtmlRichText(new CETTopicCache().splitArticle(
                    vinfo.getEn(), allFind2)).getCharSequence());
            mTextView.append("\n" + vinfo.getCh() + "\n\n");

            if (allFind2.size() > 0)
            {
                for (Topic t : allFind2)
                {
                    if (!allFind.contains(t))
                    {
                        allFind.add(t);
                    }
                }
            }
            totalWords += PatternUtil.getAllPatternGroup(vinfo.getEn(),
                    "['\\w]+").size();
        }
        if (allFind.size() > 0)
        {
            this.topicListBt.setText("" + allFind.size());
        }

    }

    private void setNewsTitle(String title)
    {
        ((TextView) findViewById(R.id.tv_voa_title)).setText(title);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case MESSAGE_PROCESS_CODE:
                mTextView.setText(msg.obj.toString());
                break;
            case MESSAGE_DIRECTLINK_CODE:
                NewsContentActivity.news_info = (NewsInfo) msg.obj;
                startActivity(new Intent(getApplicationContext(),
                        NewsContentActivity.class));
                break;
            case MESSAGE_DOWNLOAD_OK_CODE:
                ToastUtil.showLongToast(getApplicationContext(), "MP3下载已经完成.");
                break;
            case MESSAGE_DOWNLOAD_ERR_CODE:
                ToastUtil.showLongToast(getApplicationContext(), "MP3下载失败.");
                break;
            }
        }
    };
    /**
     * 多余多层次的搜索,使用栈来管理数据
     */
    Stack<List<NewsInfo>> newsStack = new Stack<List<NewsInfo>>();

    private void initView()
    {
        newsMenuBtn = (ImageButton) findViewById(R.id.imgbt_voa_menu);
        wordTipTv = (TextView) findViewById(R.id.tv_voa_oneword_tip);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_voa_content);

        wordMenuBtn = (Button) findViewById(R.id.btn_voa_word_menu);
        topicListBt = (Button) findViewById(R.id.bt_voa_topics);
        wordMenuBtn.setOnClickListener(this);
        topicListBt.setOnClickListener(this);
        newsMenuBtn.setOnClickListener(this);

        mTextView = (SelectableTextView) findViewById(R.id.tvVoaContent);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setDefaultSelectionColor(0x40FF00FF);
        // 事件调用顺序OnTouch --> OnLongClick --> OnClick
        mTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }
        });
        mTextView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                enableTextViewLink(false);
                hideWordZone();
                hideCursor();
                showSelectionCursors(mTouchX, mTouchY);
                return true;
            }
        });

        mTextView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                enableTextViewLink(true);
                hideWordZone();
                hideCursor();
            }
        });
        mTextView
                .setOnCursorStateChangedListener(new OnCursorStateChangedListener()
                {

                    @Override
                    public void onShowCursors(View v)
                    {
                    }

                    @Override
                    public void onPositionChanged(View v, int x, int y,
                            int oldx, int oldy)
                    {
                    }

                    @Override
                    public void onHideCursors(View v)
                    {
                    }

                    @Override
                    public void onDragStarts(View v)
                    {

                    }

                    @Override
                    public void onDragStop(View v)
                    {
                        String selectedText = mTextView.getCursorSelection()
                                .getSelectedText().toString();
                        if (selectedText.contains(" "))
                        {
                            hideWordZone();
                            getCurrentSectionAndSetPos();

                            final int h = common.app.BasicPhoneUtil
                                    .getScreenHeight(VoaActivity.this);

                            if (mTextView.getCursorPoint()[1] < h * 0.8)
                            {
                                sectionPopWindow.showAtLocation(mTextView,
                                        Gravity.NO_GRAVITY,
                                        mTextView.getCursorPoint()[0] + 100,
                                        mTextView.getCursorPoint()[1]);
                            }
                            else
                            {
                                sectionPopWindow.showAtLocation(mTextView,
                                        Gravity.CENTER, 0, 0);
                            }
                        }
                        else
                        {
                            showWordZone(selectedText);
                        }
                    }
                });
        wordMenuPopWindow = new WordMenuPopWindow(this, new WordMenuListener()
        {
            @Override
            public void doSound()
            {
                String voicePath = MyAppParams.VOICE_FOLDER + getCurrentWord()
                        + ".mp3";
                if (BasicFileUtil.isExistFile(voicePath))
                {
                    SrtVoiceHelper.play(voicePath);
                }
                else
                {
                    common.app.ToastUtil.showShortToast(
                            getApplicationContext(), "找不到声音文件!");
                    log.info("找不到声音文件:" + voicePath);
                }
            }

            @Override
            public void doCopy()
            {
                common.app.ClipBoardUtil
                        .setNormalContent(getApplicationContext(), wordTipTv
                                .getText().toString());
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
            }

            @Override
            public void toNet()
            {
                ActivityMgr.gotoIE(VoaActivity.this,
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
                System.out.println("doexpand");
                final Dialog dialog = new Dialog(VoaActivity.this,
                        R.style.CustomDialogStyle);
                dialog.setContentView(R.layout.topic_tip_wdailog);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = (int) (0.85 * BasicPhoneUtil
                        .getScreenWidth(VoaActivity.this));
                lp.height = (int) (0.85 * BasicPhoneUtil
                        .getScreenHeight(VoaActivity.this));
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
                            newsStack.push(ViewNewsHolder.getCurList());
                            String selectedText = NewsContentUtil
                                    .getSuitWordAndSetPos(tvTopic, start);
                            log.info("selectedText:" + selectedText);
                            ClipBoardUtil.setNormalContent(
                                    getApplicationContext(), selectedText);
                            startActivityForResult(new Intent(
                                    getApplicationContext(),
                                    SearchActivity.class).putExtra("keyword",
                                    selectedText), REQUEST_SEARCH_CODE);
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

                if (OptedDictData.seekWordList.size() > 0)
                {
                    final Integer topic_id = OptedDictData.seekWordList.peek()
                            .getTopic_id();
                    if (OptedDictData.wordExpandContentMap
                            .containsKey(topic_id))
                    {
                        tvTopic.setText(OptedDictData.wordExpandContentMap
                                .get(topic_id));
                        dialog.show();
                    }
                    else
                    {
                        System.out.println("not contains");
                        WordExpand wordExpand = DictionaryDao
                                .findSameAntonym(OptedDictData.seekWordList
                                        .peek().getTopic_id());

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
                                OptedDictData.wordExpandContentMap.put(
                                        topic_id, charSequence);
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
        sectionPopWindow = new SectionPopWindow(this, new WordSectionListener()
        {

            @Override
            public void doTranslate()
            {
                ActivityMgr.gotoIE(VoaActivity.this, WebUrlHelper
                        .getTranslateUrl(getCurrentSectionAndSetPos()));
                log.info("翻译: " + getCurrentSectionAndSetPos());
            }

            @Override
            public void doFavorite()
            {
                System.out.println(getCurrentSectionAndSetPos());
                BasicFileUtil.writeFileString(MyAppParams.FAVORITE_TXT,
                        getCurrentSectionAndSetPos() + "\r\n", "UTF-8", true);
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("收藏成功: " + getCurrentSectionAndSetPos());
            }

            @Override
            public void doCopy()
            {
                common.app.ClipBoardUtil.setNormalContent(
                        getApplicationContext(), getCurrentSectionAndSetPos());
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
            }
        });
        newsMenuPopWindow = new NewsMenuPopWindow(this, new NewsMenuListener()
        {

            @Override
            public void toSrcPage()
            {
                if (news_info != null)
                {
                    ActivityMgr.gotoIE(VoaActivity.this, news_info.getUrl());
                }
            }

            @Override
            public void doFavorite()
            {
                String url = news_info.getUrl();
                BasicFileUtil.writeFileString(MyAppParams.FAVORITE_TXT, url
                        + "\r\n", "UTF-8", true);
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("收藏新闻成功: " + url);
            }
        });
    }

    private String getCurrentSectionAndSetPos()
    {
        return NewsContentUtil.getSectionAndSetPos(mTextView, mTextView
                .getCursorSelection().getStart(), mTextView
                .getCursorSelection().getEnd());
    }

    private String getCurrentWord()
    {
        return PatternUtil.getFirstPatternGroup(wordTipTv.getText().toString(),
                "\\w+");
    }

    private void hideCursor()
    {
        try
        {
            mTextView.hideCursor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void hideWordZone()
    {
        wordMenuBtn.setVisibility(View.INVISIBLE);
        wordTipTv.setVisibility(View.INVISIBLE);
    }

    private void enableTextViewLink(boolean flag)
    {
        if (!flag)
        {
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        else
        {
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void showSelectionCursors(int x, int y)
    {
        int start = mTextView.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(
                    mTextView, start);
            log.info("selectedText:" + selectedText);
            showWordZone(selectedText);
        }
    }

    /**
     * 隐藏虚拟按键
     */
    @SuppressLint("NewApi")
    private void hideVirtualBts()
    {
        // 普通
        final int currentAPIVersion = BasicPhoneUtil
                .getCurrentAPIVersion(getApplicationContext());
        // System.out.println("Level ........" + currentAPIVersion);
        if (currentAPIVersion < 19)
        {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        else
        {
            // 保留任务栏
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void showWordZone(String selectedText)
    {
        if (BasicStringUtil.isNullString(selectedText))
        {
            return;
        }
        DicWord findWord = null;
        BasicRunTimeUtil util = new BasicRunTimeUtil("");
        util.beginRun();
        for (DicWord d : OptedDictData.seekWordList)
        {
            if (selectedText.equalsIgnoreCase(d.getBase_word())
                    || selectedText.equalsIgnoreCase(d.getWord_done())
                    || selectedText.equalsIgnoreCase(d.getWord_er())
                    || selectedText.equalsIgnoreCase(d.getWord_est())
                    || selectedText.equalsIgnoreCase(d.getWord_ing())
                    || selectedText.equalsIgnoreCase(d.getWord_past())
                    || selectedText.equalsIgnoreCase(d.getWord_pl())
                    || selectedText.equalsIgnoreCase(d.getWord_third()))
            {
                findWord = d;
                util.finishRun();
                System.out.println("查缓存的时间:" + util.getRunMilliSecond());
                break;
            }
        }
        if (findWord == null)
        {
            findWord = DictionaryDao.findWord(selectedText);
            util.finishRun();
            System.out.println("查字典的时间:" + util.getRunMilliSecond());
        }

        if (findWord != null)
        {
            wordTipTv.setText(findWord.getBase_word() + " "
                    + findWord.getCn_mean());
            OptedDictData.seekWordList.push(findWord);
            if (DictionaryDao.findSameAntonym(findWord.getTopic_id()) != null)
            {
                wordMenuPopWindow.openExpand();
            }
            else
            {
                wordMenuPopWindow.closeExpand();
            }
        }
        else
        {
            wordTipTv.setText(selectedText);

        }
        wordMenuPopWindow.showPopupWindow(wordMenuBtn);

        wordTipTv.setVisibility(View.VISIBLE);
        wordMenuBtn.setVisibility(View.VISIBLE);

    }

    List<Topic> allFind = new ArrayList<Topic>();

    private boolean hasTopics()
    {
        return allFind != null && allFind.size() > 0;
    }

    int topicBtClickCount = 0;
    int mTouchX2, mTouchY2;
    int totalWords = 0;

    private void showTopicList()
    {
        topicBtClickCount++;
        if (hasTopics())
        {
            final Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
            dialog.setContentView(R.layout.topic_tip_wdailog);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (0.85 * BasicPhoneUtil.getScreenWidth(this));
            lp.height = (int) (0.85 * BasicPhoneUtil.getScreenHeight(this));

            final SelectableTextView tvTopic = (SelectableTextView) dialog
                    .findViewById(R.id.tvTopicInfo);

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
                    seekWordAndScroll(tvTopic, mTouchX2, mTouchY2);

                    dialog.dismiss();
                    return true;
                }
            });

            List<Topic> allFind2 = new ArrayList<Topic>(allFind);
            if (topicBtClickCount % 2 == 1)
            {
                allFind2 = allFind;
            }
            else
            {
                Collections.sort(allFind2, new Comparator<Topic>()
                {

                    @Override
                    public int compare(Topic arg0, Topic arg1)
                    {
                        return arg0.getMatched_word().compareToIgnoreCase(
                                arg1.getMatched_word());
                    }
                });
            }
            Iterator<Topic> iterator = allFind2.iterator();
            String tpContent = "总词数/生词率: "
                    + totalWords
                    + "/"
                    + BasicNumberUtil.convertScienceNum(100.0 * allFind2.size()
                            / totalWords, 1) + "%\n";
            while (iterator.hasNext())
            {
                Topic next = iterator.next();
                tpContent += next.getMatched_word() + "  "
                        + next.getMean_cn().replace("\n", "\n    ") + "\n\n";
            }
            if (tpContent.length() > 2)
            {
                tpContent = tpContent.substring(0, tpContent.length() - 2);
            }
            tvTopic.setText(tpContent);
            dialog.show();
        }
    }

    private void seekWordAndScroll(SelectableTextView tvTopic, int x, int y)
    {
        int start = tvTopic.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(tvTopic,
                    start);
            log.info("selectedText:" + selectedText);
            {
                Layout layout = mTextView.getLayout();
                final String string = mTextView.getText().toString();
                int i = string.indexOf(selectedText);
                if (i == -1)
                {
                    return;
                }
                for (int line = 0; line < layout.getLineCount(); line++)
                {
                    final int lineEnd = layout.getLineEnd(line);
                    if (lineEnd > i)
                    {
                        mScrollView.scrollTo(0, layout.getLineTop(line));
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("welcome back..." + requestCode);

        if (requestCode == REQUEST_SEARCH_CODE && newsStack.size() > 0)
        {
            ViewNewsHolder.refrehList(newsStack.pop());
            if (news_info != null)
            {
                ViewNewsHolder.refreh(news_info);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_theme:
            startActivity(new Intent(this, MainActivity2.class));
            break;
        case R.id.btn_voa_word_menu:
            wordMenuPopWindow.showPopupWindow(wordMenuBtn);
            break;
        case R.id.bt_voa_topics:
            showTopicList();
            break;
        case R.id.imgbt_voa_menu:
            newsMenuPopWindow.showPopupWindow(newsMenuBtn);
            break;
        default:
            break;
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException   ", ex);
    }

    private boolean isDownLoading = false;

    public void play(int seekTime, int stopTime)
    {
        if (isDownLoading)
        {
            ToastUtil.showShortToast(this, "已经在下载,请稍等...");
        }
        String mp3Id = PatternUtil.getLastPattern(news_info.getMp3()
                .toLowerCase().replace("mp3", ""), "\\d+");
        final String path = MyAppParams.VOA_MP3_PATH + mp3Id + ".mp3";
        if (BasicFileUtil.getFileSize(path) == 0)
        {
            BasicFileUtil.deleteFile(path);
        }
        if (!BasicFileUtil.isExistFile(path))
        {
            if (!isDownLoading)
            {
                isDownLoading = true;
                ToastUtil.showShortToast(this, "Mp3不存在,正在下载...");
                Thread downThread = new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            int download = UrlPicDownloader.download(
                                    news_info.getMp3(), path);
                            log.info(news_info.getMp3() + "  download..."
                                    + download);
                            isDownLoading = false;
                            handler.sendEmptyMessage(MESSAGE_DOWNLOAD_OK_CODE);
                        }
                        catch (Exception e)
                        {
                            isDownLoading = false;
                            handler.sendEmptyMessage(MESSAGE_DOWNLOAD_ERR_CODE);
                            BasicFileUtil.deleteFile(path);
                            e.printStackTrace();
                        }
                    }
                });
                downThread.start();
            }
        }

        if (BasicFileUtil.isExistFile(path)
                && BasicFileUtil.getFileSize(path) > 1000)
        {
            new PlayThread(path, seekTime, stopTime).start();
        }
    }
}
