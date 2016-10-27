package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.selectabletv.SelectableTextView;

import org.apache.log4j.Logger;

import voa.VINFO;
import word.Topic;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.ClickableRichText;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.Log4jUtil;
import common.uihelper.MyAppParams;

public class VoaActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler
{
    private SelectableTextView contentTv;

    private final int MESSAGE_PROCESS_CODE = 1;
    private final int MESSAGE_DIRECTLINK_CODE = 2;
    private int mTouchX;
    private int mTouchY;
    private Button topicListBt, wordMenuBtn;

    WordMenuPopWindow wordMenuPopWindow;
    Logger log = Logger.getLogger(VoaActivity.class);

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_voa);
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
        List<VINFO> infos = JSONArray.parseArray(json, VINFO.class);
        for (int i = 0; i < infos.size(); i++)
        {
            VINFO vinfo = infos.get(i);
            final int seektime = vinfo.getTime() * 100;
            int stoptime = (i == (infos.size() - 1) ? seektime : (infos.get(
                    i + 1).getTime() * 100));
            contentTv.append(new ClickableRichText("播放", this, seektime,
                    stoptime).getCharSequence());
            contentTv.append("  ");
            final ArrayList<Topic> allFind2 = new ArrayList<Topic>();
            contentTv.append(new HtmlRichText(new CETTopicCache().splitArticle(
                    vinfo.getEn(), allFind2)).getCharSequence());
            contentTv.append("\n" + vinfo.getCh() + "\n\n");

            if (allFind2.size() > 0)
            {
                allFind.addAll(allFind2);
            }
            totalWords += PatternUtil.getAllPatternGroup(vinfo.getEn(),
                    "['\\w]+").size();
        }
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
                contentTv.setText(msg.obj.toString());
                break;
            case MESSAGE_DIRECTLINK_CODE:
                NewsContentActivity.news_info = (NewsInfo) msg.obj;
                startActivity(new Intent(getApplicationContext(),
                        NewsContentActivity.class));
                break;
            }
        }
    };

    private void initView()
    {
        wordMenuBtn = (Button) findViewById(R.id.btn_voa_word_menu);
        topicListBt = (Button) findViewById(R.id.bt_voa_topics);
        wordMenuBtn.setOnClickListener(this);
        topicListBt.setOnClickListener(this);

        contentTv = (SelectableTextView) findViewById(R.id.tvVoaContent);
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
        contentTv.setDefaultSelectionColor(0x40FF00FF);
        // 事件调用顺序OnTouch --> OnLongClick --> OnClick
        contentTv.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }
        });
        contentTv.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showSelectionCursors(mTouchX, mTouchY);
                return true;
            }
        });

        contentTv.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
            }
        });
        wordMenuPopWindow = new WordMenuPopWindow(this, new WordMenuListener()
        {
            @Override
            public void doSound()
            {

            }

            @Override
            public void doCopy()
            {
            }

            @Override
            public void toNet()
            {
            }

            @Override
            public void doPassTopic()
            {
            }

            @Override
            public void doExpand()
            {

            }
        });
    }

    private void showSelectionCursors(int x, int y)
    {
        int start = contentTv.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(
                    contentTv, start);
            log.info("selectedText:" + selectedText);
        }
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
        default:
            break;
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException   ", ex);
    }

    public void play(int seekTime, int stopTime)
    {
        new PlayThread(MyAppParams.getInstance().getWorkPath() + "6857.mp3",
                seekTime, stopTime).start();
    }
}
