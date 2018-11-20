package com.example.rookie.extendtextview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.rookie.extendtextview.R;


/**
 * Created by rookie on 2018/11/20.
 * 展开，收起TextView
 * 支持：maxLineCount:最大的行数，超过后显示收起
 * 支持：collapseTxtColor:"收起"文字颜色
 * 支持: collapseTxtSize:"收起"文字大小
 * 支持: showMoreTxtColor:"展示更多"文字颜色
 * 支持: showMoreTxtSize:"展示更多"文字大小
 * 支持: shouldShowEllipse:是否显示...
 */

public class ExtendTextView extends AppCompatTextView {
    private static final String TAG = ExtendTextView.class.getSimpleName();

    private boolean shouldShowEllipse = false;
    private int maxLineCount = 5;
    private int collapseTxtColor;
    private int showMoreTxtColor;
    private float collapseTxtSize = 14;
    private float showMoreTxtSize = 14;


    public ExtendTextView(Context context) {
        this(context, null);
    }

    public ExtendTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView();

    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExtendTextView, defStyleAttr, 0);
        collapseTxtColor = typedArray.getColor(R.styleable.ExtendTextView_collapseTxtColor, Color.parseColor("#0094ff"));
        showMoreTxtColor = typedArray.getColor(R.styleable.ExtendTextView_showMoreTxtColor, Color.parseColor("#0094ff"));
        collapseTxtSize = typedArray.getDimension(R.styleable.ExtendTextView_collapseTxtSize, 20f);
        showMoreTxtSize = typedArray.getDimension(R.styleable.ExtendTextView_showMoreTxtSize, 20f);
        shouldShowEllipse = typedArray.getBoolean(R.styleable.ExtendTextView_shouldShowEllipse, false);
    }

    private final Runnable computeLineCountRunnable = new Runnable() {
        @Override
        public void run() {
            float textSize = getTextSize();
            Log.e("test","textsize---"+textSize);
            int originalLineCount = getLineCount();
            if (originalLineCount > maxLineCount) {
                setTxtForCollapse();
            }
        }
    };

    private void setTxtForCollapse() {
        TextPaint paint = getPaint();
        int width = getWidth();
        CharSequence text = getText();
        StaticLayout staticLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        int lineEnd = staticLayout.getLineEnd(maxLineCount - 1);//return的是参数+1行的第一个字符的索引
        lineEnd = lineEnd - 1;
        String showMoreTxt = shouldShowEllipse ? "...展示更多" : "展示更多";
        String sourceMoreTxt = text.subSequence(0, lineEnd - showMoreTxt.length()).toString();
        String showMoreTxtAll = sourceMoreTxt + showMoreTxt;
        final SpannableString showMoreSpannableString = new SpannableString(showMoreTxtAll);
        String collapseTxt = "收起";
        String collapseTxtAll = text.toString() + collapseTxt;

        final SpannableString collapseSpannableString = new SpannableString(collapseTxtAll);
        collapseSpannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setAntiAlias(true);
                ds.setColor(collapseTxtColor);
                ds.setTextSize(collapseTxtSize);
            }

            @Override
            public void onClick(View widget) {
                setOnClickListener(null);
                setText(showMoreSpannableString);
            }
        }, collapseTxtAll.length() - 2, collapseTxtAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        showMoreSpannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setAntiAlias(true);
                ds.setColor(showMoreTxtColor);
                ds.setTextSize(showMoreTxtSize);
            }

            @Override
            public void onClick(View widget) {
                setOnClickListener(null);
                setText(collapseSpannableString);
            }
        }, sourceMoreTxt.length(), showMoreTxtAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(showMoreSpannableString);
    }

    private void initView() {
        //TextView行数的计算，必须放到runnable或者监听中进行，否则获取不到，因为此时正在layout
        post(computeLineCountRunnable);
    }


}
