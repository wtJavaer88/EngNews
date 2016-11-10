package com.wnc.news.engnews.kpi;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.os.Handler;
import android.os.Message;

import com.wnc.news.engnews.kpi.ClickableKPIRichText.KPIChangeDayEvent;
import com.wnc.news.engnews.ui.MainActivity;
import com.wnc.news.richtext.HtmlRichText;
import common.utils.TimeUtil;

public class KPIChangeDayListener implements KPIChangeDayEvent
{
    private Handler handler;
    KPI_TYPE kpi_type;

    public KPIChangeDayListener(Handler handler, KPI_TYPE kpi_type)
    {
        this.handler = handler;
        this.kpi_type = kpi_type;
    }

    public String getCurDay()
    {
        return ClickableKPIRichText.getCurDay();
    }

    public void updateKPIHeader()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                KPIData findTodayData = KPIService.getInstance().findDataByDay(
                        getCurDay());
                if (findTodayData != null)
                {
                    Message msg = new Message();
                    msg.what = MainActivity.MESSAGE_KPI_ALL_CODE;
                    msg.obj = findTodayData;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void performChange()
    {
        performChange(getCurDay());
    }

    @Override
    public void performChange(String kpi_date)
    {
        updateKPIHeader();
        handler.sendEmptyMessage(MainActivity.MESSAGE_KPI_CHANGE_CODE);
        switch (kpi_type)
        {
        case HIS:
            postHistory(kpi_date);
            break;
        case SEL:
            postSelected(kpi_date);
            break;
        case FAV:
            postLoved(kpi_date);
            break;
        default:
            break;
        }
    }

    private void postLoved(final String kpi_date)
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                final List<ViewedNews> findLoveHistory = KPIService
                        .getInstance().findLoveHistory(kpi_date);
                Iterator<ViewedNews> iterator = findLoveHistory.iterator();
                String tpContent = "";
                AssortKPI assortKPI = new AssortKPI(findLoveHistory.size(),
                        getCurDay(), KPI_TYPE.FAV);
                while (iterator.hasNext())
                {
                    ViewedNews next = iterator.next();
                    tpContent += "<p>收藏:" + next.getView_time() + "<br>";
                    tpContent += "<font color=blue><a href=\"" + next.getUrl()
                            + "\">" + next.getTitle() + "</a></font></p>";
                }
                if (tpContent.length() > 2)
                {
                    tpContent = tpContent.substring(0, tpContent.length()
                            - "</br>".length() * 2);
                }
                assortKPI.addContent(new HtmlRichText(tpContent)
                        .getCharSequence());
                Message msg = new Message();
                msg.what = MainActivity.MESSAGE_KPI_FAV_CODE;
                msg.obj = assortKPI;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void postSelected(final String kpi_date)
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                final Set<SelectedWord> findSelectedWordsToday = KPIService
                        .getInstance().findSelectedWords(kpi_date);
                Iterator<SelectedWord> iterator = findSelectedWordsToday
                        .iterator();
                String tpContent = "";
                AssortKPI assortKPI = new AssortKPI(findSelectedWordsToday
                        .size(), getCurDay(), KPI_TYPE.SEL);
                while (iterator.hasNext())
                {
                    SelectedWord next = iterator.next();
                    String mean = next.getCn_mean();
                    if (mean == null)
                    {
                        mean = "";
                    }
                    tpContent += next.getWord() + "  "
                            + mean.replace("\n", "\n    ") + "\n\n";
                }
                if (tpContent.length() > 2)
                {
                    tpContent = tpContent.substring(0, tpContent.length() - 2);
                }
                assortKPI.addContent(tpContent);
                Message msg = new Message();
                msg.what = MainActivity.MESSAGE_KPI_SEL_CODE;
                msg.obj = assortKPI;
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
                final List<ViewedNews> findViewHistory = KPIService
                        .getInstance().findViewHistory(kpi_date);
                Iterator<ViewedNews> iterator = findViewHistory.iterator();
                String tpContent = "";
                AssortKPI assortKPI = new AssortKPI(findViewHistory.size(),
                        getCurDay(), KPI_TYPE.HIS);
                while (iterator.hasNext())
                {
                    ViewedNews next = iterator.next();
                    final String timeToText = TimeUtil.timeToText(next
                            .getView_duration());
                    tpContent += "<p>浏览:" + next.getView_time() + "  用时:"
                            + timeToText + "<br>";
                    tpContent += "<font color=blue><a href=\"" + next.getUrl()
                            + "\">" + next.getTitle() + "</a></font></p>";
                }
                if (tpContent.length() > 2)
                {
                    tpContent = tpContent.substring(0, tpContent.length()
                            - "</br>".length() * 2);
                }
                assortKPI.addContent(new HtmlRichText(tpContent)
                        .getCharSequence());
                Message msg = new Message();
                msg.what = MainActivity.MESSAGE_KPI_HIS_CODE;
                msg.obj = assortKPI;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
