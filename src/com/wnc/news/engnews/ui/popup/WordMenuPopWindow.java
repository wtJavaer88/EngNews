package com.wnc.news.engnews.ui.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.example.engnews.R;

public class WordMenuPopWindow extends PopupWindow implements OnClickListener
{
    private View conentView;
    WordMenuListener menuListener;
    View expandLayout;

    public WordMenuPopWindow(final Activity context,
            WordMenuListener menuListener)
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

        conentView.findViewById(R.id.layout_word_sound)
                .setOnClickListener(this);
        conentView.findViewById(R.id.layout_word_copy).setOnClickListener(this);
        conentView.findViewById(R.id.layout_word_net).setOnClickListener(this);
        conentView.findViewById(R.id.layout_word_pass).setOnClickListener(this);
        expandLayout = conentView.findViewById(R.id.layout_word_expand);
        expandLayout.setOnClickListener(this);
    }

    public void closeExpand()
    {
        expandLayout.setVisibility(View.GONE);
    }

    public void openExpand()
    {
        expandLayout.setVisibility(View.VISIBLE);
    }

    public interface WordMenuListener
    {
        public void doCopy();

        public void doSound();

        public void toNet();

        public void doPassTopic();

        public void doExpand();
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
            if (menuListener != null)
            {
                menuListener.doSound();
            }
            break;
        case R.id.layout_word_copy:
            if (menuListener != null)
            {
                menuListener.doCopy();
            }
            break;
        case R.id.layout_word_net:
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
        case R.id.layout_word_expand:
            if (menuListener != null)
            {
                menuListener.doExpand();
            }
            break;
        default:
            break;
        }
    }
}
