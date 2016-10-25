package net.widget.cqq;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * The <code>AddAndSubView</code> class is used to initialize a custom widget to
 * show and edit number.
 * 
 * @author index_cqq
 * @version 1.0, 8 September 2015
 */
public class AddAndSubView extends LinearLayout
{

    private static final String SUB_TAG = "-";

    private static final String ADD_TAG = "+";

    private Context mContext;

    private LinearLayout mMainLinearLayout;

    private LinearLayout mLeftLinearLayout;

    private LinearLayout mCenterLinearLayout;

    private LinearLayout mRightLinearLayout;

    private OnNumChangeListener onNumChangeListener;

    private Button mAddButton;

    private Button mSubButton;

    private EditText mEditText;

    private int mNum;

    private int mEditTextLayoutWidth;

    private int mEditTextLayoutHeight;

    private int mEditTextMinimumWidth;

    private int mEditTextMinimumHeight;

    private int mEditTextMinHeight;

    private int mEditTextHeight;

    private int num_step = 1;

    public void setNumStep(int step)
    {
        this.num_step = step;
    }

    public int getNumStep()
    {
        return this.num_step;
    }

    /**
     * Creates an instance of <code>AddAndSubView</code>.
     * 
     * @param context
     *            context allows access to resources and types of features to
     *            the application.
     */
    public AddAndSubView(Context context)
    {

        super(context);
        this.mContext = context;
        this.mNum = 0;
        initWidget();
    }

    /**
     * Creates an instance of <code>AddAndSubView</code>.
     * 
     * @param context
     *            context allows access to resources and types of features to
     *            the application.
     * @param num
     *            initialized number.
     */
    public AddAndSubView(Context context, int num)
    {

        super(context);
        this.mContext = context;
        this.mNum = num;
        initWidget();
    }

    /**
     * Creates an instance of <code>AddAndSubView</code>.
     * 
     * @param context
     *            context allows access to resources and types of features to
     *            the application.
     * @param attrs
     *            attributes.
     */
    public AddAndSubView(Context context, AttributeSet attrs)
    {

        super(context, attrs);
        this.mContext = context;
        this.mNum = 0;
        initWidget();
    }

    /**
     * Initializes widget.
     */
    private void initWidget()
    {

        initTextWithHeight();
        initInternalView();
        setViewsLayoutParm();
        insertView();
        setViewListener();
    }

    /**
     * Initializes high parameter of EditText.
     */
    private void initTextWithHeight()
    {

        mEditTextLayoutWidth = -1;
        mEditTextLayoutHeight = -1;
        mEditTextMinimumWidth = -1;
        mEditTextMinimumHeight = -1;
        mEditTextMinHeight = -1;
        mEditTextHeight = -1;
    }

    /**
     * Initializes internal view.
     */
    private void initInternalView()
    {

        mMainLinearLayout = new LinearLayout(mContext);
        mLeftLinearLayout = new LinearLayout(mContext);
        mCenterLinearLayout = new LinearLayout(mContext);
        mRightLinearLayout = new LinearLayout(mContext);
        mAddButton = new Button(mContext);
        mSubButton = new Button(mContext);
        mEditText = new EditText(mContext);

        mAddButton.setText(" + ");
        mSubButton.setText(" - ");
        mAddButton.setTag(ADD_TAG);
        mSubButton.setTag(SUB_TAG);

        mEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mEditText.setText(String.valueOf(mNum));
    }

    /**
     * Sets layout parameters for internal layout.
     */
    private void setViewsLayoutParm()
    {

        LayoutParams viewLayoutParams = new LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mAddButton.setLayoutParams(viewLayoutParams);
        mSubButton.setLayoutParams(viewLayoutParams);
        mEditText.setLayoutParams(viewLayoutParams);
        mEditText.setGravity(Gravity.CENTER);
        setTextWidthHeight();

        viewLayoutParams.gravity = Gravity.CENTER;
        mCenterLinearLayout.setLayoutParams(viewLayoutParams);

        mCenterLinearLayout.setFocusable(true);
        mCenterLinearLayout.setFocusableInTouchMode(true);

        viewLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        viewLayoutParams.weight = 1.0f;
        mLeftLinearLayout.setLayoutParams(viewLayoutParams);
        mRightLinearLayout.setLayoutParams(viewLayoutParams);

        viewLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mMainLinearLayout.setLayoutParams(viewLayoutParams);
        mMainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    /**
     * Puts the view in the LinearLayout.
     */
    private void insertView()
    {

        mMainLinearLayout.addView(mLeftLinearLayout, 0);
        mMainLinearLayout.addView(mCenterLinearLayout, 1);
        mMainLinearLayout.addView(mRightLinearLayout, 2);

        mLeftLinearLayout.addView(mSubButton);
        mCenterLinearLayout.addView(mEditText);
        mRightLinearLayout.addView(mAddButton);

        addView(mMainLinearLayout);
    }

    /**
     * Sets listener for EditText changes.
     */
    private void setViewListener()
    {

        mAddButton.setOnClickListener(new OnButtonClickListener());
        mSubButton.setOnClickListener(new OnButtonClickListener());
        mEditText.addTextChangedListener(new OnTextChangeListener());
    }

    /**
     * Set the width of EditText view and text area.
     */
    private void setTextWidthHeight()
    {

        float fPx;

        if (mEditTextMinimumWidth < 0)
        {
            // 将数据从dip(即dp)转换到px，第一参数为数据原单位（此为DIP），第二参数为要转换的数据值
            fPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f,
                    mContext.getResources().getDisplayMetrics());
            mEditTextMinimumWidth = Math.round(fPx);
        }
        mEditText.setMinimumWidth(mEditTextMinimumWidth);

        if (mEditTextHeight > 0)
        {
            if (mEditTextMinHeight >= 0 && mEditTextMinHeight > mEditTextHeight)
            {
                mEditTextHeight = mEditTextMinHeight;
            }
            mEditText.setHeight(mEditTextHeight);
        }

        if (mEditTextLayoutHeight > 0)
        {
            if (mEditTextMinimumHeight > 0
                    && mEditTextMinimumHeight > mEditTextLayoutHeight)
            {
                mEditTextLayoutHeight = mEditTextMinimumHeight;
            }

            LayoutParams layoutParams = (LayoutParams) mEditText
                    .getLayoutParams();
            layoutParams.height = mEditTextLayoutHeight;
            mEditText.setLayoutParams(layoutParams);
        }

        if (mEditTextLayoutWidth > 0)
        {
            if (mEditTextMinimumWidth > 0
                    && mEditTextMinimumWidth > mEditTextLayoutWidth)
            {
                mEditTextLayoutWidth = mEditTextMinimumWidth;
            }

            LayoutParams layoutParams = (LayoutParams) mEditText
                    .getLayoutParams();
            layoutParams.width = mEditTextLayoutWidth;
            mEditText.setLayoutParams(layoutParams);
        }
    }

    /**
     * Sets the value in editText.
     * 
     * @param num
     *            number.
     */
    public void setNum(int num)
    {

        this.mNum = num;
        mEditText.setText(String.valueOf(num));
    }

    /**
     * Gets the value in editText.
     * 
     * @return numeber.
     */
    public int getNum()
    {

        try
        {
            return Integer.parseInt(mEditText.getText().toString());
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * Sets the minimum width of the EditText view.
     * 
     * @param editTextMinimumWidth
     *            the minimum width of the EditText view.
     */
    public void setEditTextMinimumWidth(int editTextMinimumWidth)
    {

        if (mEditTextMinimumWidth > 0)
        {
            this.mEditTextMinimumWidth = editTextMinimumWidth;
            mEditText.setMinimumWidth(editTextMinimumWidth);
        }

    }

    /**
     * Sets the minimum height of the EditText view.
     * 
     * @param editTextMinimumHeight
     *            the minimum height of the EditText view.
     */
    public void setEditTextMinimumHeight(int editTextMinimumHeight)
    {

        if (mEditTextMinimumHeight > 0)
        {
            this.mEditTextMinimumHeight = editTextMinimumHeight;
            mEditText.setMinimumHeight(editTextMinimumHeight);
        }
    }

    /**
     * Sets the minimum height of the EditText text area.
     * 
     * @param editTextMinHeight
     *            the minimum height of the EditText text area.
     */
    public void setEditTextMinHeight(int editTextMinHeight)
    {

        if (mEditTextMinHeight > 0)
        {
            this.mEditTextMinHeight = editTextMinHeight;
            mEditText.setMinHeight(editTextMinHeight);
        }
    }

    /**
     * Sets the height of the EditText text area.
     * 
     * @param editTextHeight
     *            the height of the EditText text area.
     */
    public void setEditTextHeight(int editTextHeight)
    {

        this.mEditTextHeight = editTextHeight;
        setTextWidthHeight();
    }

    /**
     * Sets the width of the EditText view.
     * 
     * @param editTextLayoutWidth
     *            the width of the EditText view.
     */
    public void setEditTextLayoutWidth(int editTextLayoutWidth)
    {

        this.mEditTextLayoutWidth = editTextLayoutWidth;
        setTextWidthHeight();
    }

    /**
     * Sets the height of the EditText view.
     * 
     * @param editTextLayoutHeight
     *            height of the EditText view.
     */
    public void setEditTextLayoutHeight(int editTextLayoutHeight)
    {

        this.mEditTextLayoutHeight = editTextLayoutHeight;
        setTextWidthHeight();
    }

    /**
     * Sets the button background diagram by drawable.
     * 
     * @param addBtnDrawable
     *            the background map for addBtn.
     * @param subBtnDrawable
     *            the background map for asubBtn.
     */
    public void drawable(Drawable addBtnDrawable, Drawable subBtnDrawable)
    {

        mAddButton.setBackgroundDrawable(addBtnDrawable);
        mSubButton.setBackgroundDrawable(subBtnDrawable);
        mAddButton.setText("");
        mSubButton.setText("");
    }

    /**
     * Sets the button background diagram by the resource.
     * 
     * @param addBtnResource
     *            the background map for addBtn.
     * @param subBtnResource
     *            the background map for asubBtn.
     */
    public void setButtonBgResource(int addBtnResource, int subBtnResource)
    {

        mAddButton.setBackgroundResource(addBtnResource);
        mSubButton.setBackgroundResource(subBtnResource);
        mAddButton.setText("");
        mSubButton.setText("");
    }

    /**
     * Sets button background color.
     * 
     * @param addBtnColor
     *            the background color for addBtn.
     * @param subBtnColor
     *            the background color for subBtn.
     */
    public void setButtonBgColor(int addBtnColor, int subBtnColor)
    {

        mAddButton.setBackgroundColor(addBtnColor);
        mSubButton.setBackgroundColor(subBtnColor);
    }

    /**
     * Sets default style.
     */
    public void setDeafultStyle()
    {

        int color = getResources().getColor(android.R.color.transparent);
        mAddButton.setBackgroundColor(color);
        mSubButton.setBackgroundColor(color);
    }

    /**
     * Sets listener for EditText.
     * 
     * @param onNumChangeListener
     *            listener for EditText.
     */
    public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener)
    {

        this.onNumChangeListener = onNumChangeListener;
    }

    /**
     * The <code>OnButtonClickListener</code> class is used to sets listen for
     * addBtn.
     * 
     * @author index_cqq
     * @version 1.0, 8 September 2015
     */
    private class OnButtonClickListener implements OnClickListener
    {

        @Override
        public void onClick(View v)
        {

            String numString = mEditText.getText().toString();
            if (numString == null || numString.equals(""))
            {
                mNum = 0;
                mEditText.setText("0");
            }
            else
            {
                if (v.getTag().equals(ADD_TAG))
                {
                    if (addNum() < 0)
                    {
                        mNum--;
                        Toast.makeText(mContext,
                                "Please enter a number greater than 0.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mEditText.setText(String.valueOf(mNum));

                        if (onNumChangeListener != null)
                        {
                            onNumChangeListener.onNumChange(AddAndSubView.this,
                                    mNum);
                        }
                    }
                }
                else if (v.getTag().equals(SUB_TAG))
                {
                    if (subNum() < 0)
                    {
                        mNum++;
                        Toast.makeText(mContext,
                                "Please enter a number greater than 0.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mEditText.setText(String.valueOf(mNum));
                        if (onNumChangeListener != null)
                        {
                            onNumChangeListener.onNumChange(AddAndSubView.this,
                                    mNum);
                        }
                    }
                }
            }
        }

        private int subNum()
        {
            return mNum -= num_step;
        }

        private int addNum()
        {
            return mNum += num_step;
        }
    }

    /**
     * The <code>OnTextChangeListener</code> class is used to sets listen for
     * subBtn.
     * 
     * @author index_cqq
     * @version 1.0, 8 September 2015
     */
    private class OnTextChangeListener implements TextWatcher
    {

        @Override
        public void afterTextChanged(Editable s)
        {

            String numString = s.toString();
            if (numString == null || numString.equals(""))
            {
                mNum = 0;
                if (onNumChangeListener != null)
                {
                    onNumChangeListener.onNumChange(AddAndSubView.this, mNum);
                }
            }
            else
            {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0)
                {
                    Toast.makeText(mContext,
                            "Please enter a number greater than 0.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mEditText.setSelection(mEditText.getText().toString()
                            .length());
                    mNum = numInt;
                    if (onNumChangeListener != null)
                    {
                        onNumChangeListener.onNumChange(AddAndSubView.this,
                                mNum);
                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {

        }

    }

    /**
     * The <code>OnNumChangeListener</code> class is used to listen numerical
     * changes in the input box.
     * 
     * @author index_cqq
     * @version 1.0, 8 September 2015
     */
    public interface OnNumChangeListener
    {

        /**
         * Numerical changes in the input box.
         * 
         * @param view
         *            the whole widget.
         * @param num
         *            number in the input box.
         */
        public void onNumChange(View view, int num);
    }

}
