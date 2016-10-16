package com.wnc.news.engnews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.engnews.R;

public class MainActivity extends Activity implements OnClickListener
{
	private Button bt_nba;
	private Button bt_soccer;

	@Override
	public void onCreate(Bundle icicle)
	{
		System.out.println("111111111");
		super.onCreate(icicle);
		System.out.println("222222222");
		setContentView(R.layout.activity_main);
		System.out.println("333333333");
		initView();
	}

	private void initView()
	{
		bt_nba = (Button) findViewById(R.id.btn_nba);
		bt_soccer = (Button) findViewById(R.id.btn_soccer);

		bt_nba.setOnClickListener(this);
		bt_soccer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_nba:
			startActivity(new Intent(this, NewsListActivity.class).putExtra("type", "nba"));
			break;
		case R.id.btn_soccer:
			startActivity(new Intent(this, NewsListActivity.class).putExtra("type", "soccer"));
			break;
		default:
			break;
		}
	}
}
