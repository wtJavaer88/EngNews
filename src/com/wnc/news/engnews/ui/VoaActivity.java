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
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.engnews.helper.KPIHelper;
import com.wnc.news.engnews.helper.PlaySevice;
import com.wnc.news.richtext.ClickableRichText;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableDoubleClickGestureDetectorListener;
import common.uihelper.gesture.MyCtrlableGestureDetector;
import common.utils.UrlPicDownloader;

@SuppressLint(
{ "DefaultLocale", "HandlerLeak" })
public class VoaActivity extends BaseNewsActivity implements
        CtrlableDoubleClickGestureDetectorListener
{

    boolean isAutoStop = true;
    public final int MESSAGE_PROCESS_CODE = 11;
    public final int MESSAGE_DOWNLOAD_OK_CODE = 13;
    public final int MESSAGE_DOWNLOAD_ERR_CODE = 14;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(main);
        activityTimeUtil.begin();

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

    @Override
    protected void initData()
    {
        try
        {
            String json = news_info.getHtml_content();
            List<VINFO> infos = JSONArray.parseArray(json, VINFO.class);
            for (int i = 0; i < infos.size(); i++)
            {
                VINFO vinfo = infos.get(i);
                final int seektime = vinfo.getTime() * 100;
                int stoptime = (i == (infos.size() - 1) ? seektime : (infos
                        .get(i + 1).getTime() * 100));
                mTextView.append(new ClickableRichText("播放", this, seektime,
                        stoptime).getCharSequence());
                mTextView.append("  ");
                final ArrayList<Topic> allFind2 = new ArrayList<Topic>();
                mTextView.append(new HtmlRichText(new CETTopicCache()
                        .splitArticle(vinfo.getEn(), allFind2))
                        .getCharSequence());
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
                KPIHelper.addHighLights(allFind);
                this.topicListBt.setText("" + allFind.size());
            }
        }
        catch (Exception e)
        {
            log.error("initData", e);
        }

    }

    @Override
    public void dispatchMsg(Message msg)
    {
        switch (msg.what)
        {
        case MESSAGE_PROCESS_CODE:
            mTextView.setText(msg.obj.toString());
            break;
        case MESSAGE_DOWNLOAD_OK_CODE:
            ToastUtil.showLongToast(getApplicationContext(), "MP3下载已经完成.");
            break;
        case MESSAGE_DOWNLOAD_ERR_CODE:
            ToastUtil.showLongToast(getApplicationContext(), "MP3下载失败.");
            break;
        case MESSAGE_ON_RUNTIME_TEXT:
            ((TextView) findViewById(R.id.bt_activity_runtime))
                    .setText(activityTimeUtil.getTranedRunTime());
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
            ToastUtil.showShortToast(this, "已经在下载,请稍等...");
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
                ToastUtil.showShortToast(this, "Mp3不存在,正在下载...");
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