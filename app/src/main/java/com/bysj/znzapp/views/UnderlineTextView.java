package com.bysj.znzapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:38
 * @Describe:
 * @Version: 1.0.0
 */
public class UnderlineTextView extends AppCompatTextView {

    private final Paint paint = new Paint();
    // 下划线高度
    private int underlineHeight = 5;
    // 下划线颜色
    private int underLineColor = Color.parseColor("#078ded");

    private boolean isVisible = true;

    public UnderlineTextView(Context context) {
        this(context, null, 0);
    }

    public UnderlineTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnderlineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 设置下划线颜色
        paint.setColor(underLineColor);
    }

    // 绘制下划线
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isVisible) {
            canvas.drawRect(0, getHeight() - underlineHeight, getWidth(), getHeight(), paint);
        }
    }

    public void setUnderlineVisible(boolean isVisible) {
        this.isVisible = isVisible;
        invalidate();
    }

    public void setUnderLineColor(int underLineColor) {
        this.underLineColor = underLineColor;
        paint.setColor(this.underLineColor);
    }
}

