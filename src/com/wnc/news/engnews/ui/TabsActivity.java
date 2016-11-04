package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.example.engnews.R;
import com.viewpagerindicator.TabPageIndicator;
import com.wnc.news.engnews.helper.ViewNewsHolder;
import common.uihelper.MyAppParams;

public class TabsActivity extends FragmentActivity implements
        UncaughtExceptionHandler
{
    Logger log = Logger.getLogger(TabsActivity.class);

    List<PageFragment> fragmentList = new ArrayList<PageFragment>();
    FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main2);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initFragments();
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int arg0)
            {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {

            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {

            }
        });
    }

    private void initFragments()
    {
        addFragment(MyAppParams.getInstance().getBaskModelName());
        addFragment(MyAppParams.getInstance().getSoccModelName());
        addFragment(MyAppParams.getInstance().getForuModelName());
        addFragment(MyAppParams.getInstance().getVoaModelName());
    }

    private void addFragment(String tagname)
    {
        this.fragmentList.add(new PageFragment(tagname));
    }

    /**
     * ViewPager适配器
     * 
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter
    {
        public TabPageIndicatorAdapter(FragmentManager fm)
        {
            super(fm);
        }

        PageFragment currentFragment;

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                Object object)
        {
            currentFragment = (PageFragment) object;
            if (currentFragment != null && currentFragment.getNews().size() > 0)
            {
                // 切换主题
                ViewNewsHolder.refrehList(currentFragment.getNews());
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position)
        {
            return TabsActivity.this.fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return fragmentList.get(position).getFragmentTitle();
        }

        @Override
        public int getCount()
        {
            return fragmentList.size();
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException   ", ex);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // land
            System.out.println("ORIENTATION_LANDSCAPE");

        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // port
            System.out.println("ORIENTATION_PORTRAIT");
        }
    }

}