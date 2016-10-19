package com.wnc.news.engnews.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.engnews.R;

public class MorePopWindow extends PopupWindow implements OnClickListener
{
    private View conentView;
    WordMenuListener menuListener;

    public MorePopWindow(final Activity context, WordMenuListener menuListener)
    {
        this.menuListener = menuListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.more_popup_dialog, null);
        int w = common.app.BasicPhoneUtil.getScreenWidth(context);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 3);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        LinearLayout layout1 = (LinearLayout) conentView
                .findViewById(R.id.layout_word_sound);
        LinearLayout layout2 = (LinearLayout) conentView
                .findViewById(R.id.layout_word_copy);
        LinearLayout layout3 = (LinearLayout) conentView
                .findViewById(R.id.layout_word_net);
        LinearLayout layout4 = (LinearLayout) conentView
                .findViewById(R.id.layout_word_pass);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
    }

    public interface WordMenuListener
    {
        public void doCopy();

        public void doSound();

        public void toNet();

        public void doPassTopic();
    }

    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        }
        else
        {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.layout_word_sound:
            System.out.println("发音");
            if (menuListener != null)
            {
                menuListener.doSound();
            }
            break;
        case R.id.layout_word_copy:
            System.out.println("复制");
            if (menuListener != null)
            {
                menuListener.doCopy();
            }
            break;
        case R.id.layout_word_net:
            System.out.println("网络");
            if (menuListener != null)
            {
                menuListener.toNet();
            }
            break;
        case R.id.layout_word_pass:
            if (menuListener != null)
            {
                menuListener.doPassTopic();
            }
            break;
        default:
            break;
        }
    }
}
