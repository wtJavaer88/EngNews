package com.wnc.news.engnews.helper;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CBService extends Service
{

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        listen();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    private void listen()
    {
        System.out.println("启动服务...");
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager
                .addPrimaryClipChangedListener(new OnPrimaryClipChangedListener()
                {

                    @Override
                    public void onPrimaryClipChanged()
                    {
                        String content = clipboardManager.getPrimaryClip()
                                .getItemAt(0).getText().toString();
                        System.out.println(content);
                    }
                });
    }

}