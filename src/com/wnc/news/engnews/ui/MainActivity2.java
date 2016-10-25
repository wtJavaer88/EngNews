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
import common.uihelper.MyAppParams;

public class MainActivity2 extends FragmentActivity implements
        UncaughtExceptionHandler
{
    /**
     * 选项卡总数
     */
    // private int TAB_COUNT = 0;
    List<String> titles = new ArrayList<String>();
    Logger log = Logger.getLogger(MainActivity2.class);

    List<Fragment> fragmentList = new ArrayList<Fragment>();
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

    }

    private void addFragment(String tagname)
    {
        this.fragmentList.add(new PageFragment(tagname));
        this.titles.add(tagname);
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

        Fragment currentFragment;

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                Object object)
        {
            currentFragment = (Fragment) object;
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position)
        {
            return MainActivity2.this.fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return MainActivity2.this.titles.get(position % getCount());
        }

        @Override
        public int getCount()
        {
            return MainActivity2.this.titles.size();
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