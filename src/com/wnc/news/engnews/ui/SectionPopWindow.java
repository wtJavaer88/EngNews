package com.wnc.news.engnews.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.example.engnews.R;

public class SectionPopWindow extends PopupWindow implements OnClickListener
{
    private View conentView;
    WordSectionListener sectionListener;

    public SectionPopWindow(final Activity context,
            WordSectionListener sectionListener)
    {
        this.sectionListener = sectionListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.section_popup_dialog, null);
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

        conentView.findViewById(R.id.layout_section_fav).setOnClickListener(
                this);
        conentView.findViewById(R.id.layout_section_copy).setOnClickListener(
                this);
        conentView.findViewById(R.id.layout_section_translate)
                .setOnClickListener(this);
    }

    public interface WordSectionListener
    {
        public void doCopy();

        public void doFavorite();

        public void doTranslate();
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
        case R.id.layout_section_fav:
            if (sectionListener != null)
            {
                sectionListener.doFavorite();
            }
            break;
        case R.id.layout_section_copy:
            if (sectionListener != null)
            {
                sectionListener.doCopy();
            }
            break;
        case R.id.layout_section_translate:
            if (sectionListener != null)
            {
                sectionListener.doTranslate();
            }
            break;
        default:
            break;
        }
    }
}
