package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.engnews.R;

@SuppressLint("ValidFragment")
public class PageFragment extends Fragment implements UncaughtExceptionHandler, OnClickListener
{
	View view;
	public boolean hasExecute = false;
	Logger log = Logger.getLogger(PageFragment.class);

	public PageFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.view = inflater.inflate(R.layout.activity_viewsubject, null);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		if (!this.hasExecute)
		{
			this.hasExecute = true;
		}
		Thread.setDefaultUncaughtExceptionHandler(this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void uncaughtException(Thread arg0, Throwable ex)
	{
		log.error("uncaughtException   ", ex);
	}

}
