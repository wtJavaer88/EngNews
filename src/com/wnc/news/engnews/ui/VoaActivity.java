package com.wnc.news.engnews.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import voa.VINFO;
import voa.VoaNewsInfo;
import word.Topic;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSONArray;
import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.engnews.helper.PlaySevice;
import com.wnc.news.richtext.ClickableVoiceRichText;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableDoubleClickGestureDetectorListener;
import common.uihelper.gesture.MyCtrlableGestureDetector;
import common.utils.UrlPicDownloader;

@SuppressLint(
{ "DefaultLocale", "HandlerLeak" })
public class VoaActivity extends BaseNewsActivity implements
        CtrlableDoubleClickGestureDetectorListener
{

    private static final int MESSAGE_TOPIC_COUNT_CODE = 999;
    boolean isAutoStop = true;
    public final int MESSAGE_DOWNLOAD_OK_CODE = 13;
    public final int MESSAGE_DOWNLOAD_ERR_CODE = 14;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(main);

        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.5, 0, null, null)
                        .setDclistener(this));

        log = Logger.getLogger(VoaActivity.class);
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
                msg.obj = new WebImgText("<img src=\""
                        + news_info.getHead_pic() + "\"/>").getCharSequence();
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
                    String json = news_info.getHtml_content();
                    List<VINFO> infos = JSONArray.parseArray(json, VINFO.class);
                    for (int i = 0; i < infos.size(); i++)
                    {
                        VINFO vinfo = infos.get(i);
                        final int seektime = vinfo.getTime() * 100;
                        int stoptime = (i == (infos.size() - 1) ? seektime
                                : (infos.get(i + 1).getTime() * 100));
                        final ArrayList<Topic> allFind2 = new ArrayList<Topic>();
                        sendContentMsg(new ClickableVoiceRichText(
                                VoaActivity.this, seektime, stoptime)
                                .getCharSequence());
                        sendContentMsg("   ");
                        sendContentMsg(new HtmlRichText(new CETTopicCache()
                                .splitArticle(vinfo.getEn(), allFind2))
                                .getCharSequence());
                        sendContentMsg("\n" + vinfo.getCh() + "\n\n");
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
                        totalWords += PatternUtil.getAllPatternGroup(
                                vinfo.getEn(), "['\\w]+").size();
                    }
                    if (allFind.size() > 0)
                    {
                        handler.sendEmptyMessage(MESSAGE_TOPIC_COUNT_CODE);
                    }
                }
                catch (Exception e)
                {
                    log.error("initData", e);
                }
            }

            public void sendContentMsg(CharSequence obj)
            {
                Message msg = new Message();
                msg.what = MESSAGE_ON_CONTEXT_TEXT;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        }).start();

    }

    @Override
    public void dispatchMsg(Message msg)
    {
        switch (msg.what)
        {
        case MESSAGE_ON_CONTEXT_TEXT:
            mTextView.append((CharSequence) msg.obj);
            break;
        case MESSAGE_DOWNLOAD_OK_CODE:
            messagePopWindow.setMsgAndShow("MP3下载已经完成.", mTextView);
            break;
        case MESSAGE_DOWNLOAD_ERR_CODE:
            messagePopWindow.setMsgAndShow("MP3下载失败.", mTextView);
            break;
        case MESSAGE_TOPIC_COUNT_CODE:
            showTopicCounts();
            break;
        }
    }

    @Override
    public void newsMenuSetting()
    {
        final AlertDialog voaSettingDialog = new AlertDialog.Builder(
                VoaActivity.this).create();
        voaSettingDialog.show();
        voaSettingDialog.getWindow().setGravity(Gravity.CENTER);
        voaSettingDialog.getWindow().setLayout(
                (int) (Math.min(MyAppParams.getScreenWidth(),
                        MyAppParams.getScreenHeight()) * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        voaSettingDialog.getWindow().setContentView(R.layout.voa_setting);
        if (isAutoStop)
        {
            ((RadioButton) voaSettingDialog.findViewById(R.id.rb_voa_single))
                    .setChecked(true);
        }
        else
        {
            ((RadioButton) voaSettingDialog.findViewById(R.id.rb_voa_full))
                    .setChecked(true);
        }
        // 根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) voaSettingDialog
                .findViewById(R.id.rb_voa_setting);
        // 绑定一个匿名监听器
        group.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1)
            {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) voaSettingDialog
                        .findViewById(radioButtonId);

                if (rb.getId() == R.id.rb_voa_full)
                {
                    isAutoStop = false;
                }
                else if (rb.getId() == R.id.rb_voa_single)
                {
                    isAutoStop = true;
                }
                if (playService != null)
                {
                    System.out.println("isAutoStop:" + isAutoStop);
                    playService.setAutoStop(isAutoStop);
                }
                voaSettingDialog.dismiss();
                newsMenuPopWindow.dismiss();
            }
        });

    }

    private boolean isDownLoading = false;

    public void play(int seekTime, int stopTime)
    {
        if (isDownLoading)
        {
            messagePopWindow.setMsgAndShow("Mp3正在下载,请稍候", mTextView);
        }

        final String path = getMp3Path();
        if (BasicFileUtil.getFileSize(path) == 0)
        {
            BasicFileUtil.deleteFile(path);
        }
        if (!BasicFileUtil.isExistFile(path))
        {
            if (!isDownLoading)
            {
                isDownLoading = true;
                messagePopWindow.setMsgAndShow("Mp3不存在,正在下载...", mTextView);
                Thread downThread = new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            int download = UrlPicDownloader.download(
                                    ((VoaNewsInfo) news_info).getMp3(), path);
                            log.info(((VoaNewsInfo) news_info).getMp3()
                                    + "  download..." + download);
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
            if (playService == null)
            {
                playService = new PlaySevice(getMp3Path(), isAutoStop);
                playService.initPlayer();
            }
            playService.play(seekTime, stopTime);
        }
    }

    private String getMp3Path()
    {
        String mp3Id = PatternUtil.getLastPattern(((VoaNewsInfo) news_info)
                .getMp3().toLowerCase().replace("mp3", ""), "\\d+");
        return MyAppParams.VOA_MP3_PATH + mp3Id + ".mp3";
    }

    PlaySevice playService;

    @Override
    public void doDoubleClick(MotionEvent e)
    {
        if (playService != null)
        {
            playService.pauseOrPlay();
        }
    }

}