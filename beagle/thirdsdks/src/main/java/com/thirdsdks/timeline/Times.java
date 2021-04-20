package com.thirdsdks.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by zhaotao on 2021/01/15 11:31.
 */

public class Times extends View {

    private Random random;
    private String word;
    private Rect mBounds = new Rect();
    private int rgb;
    public Times(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public Times(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Times(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        random = new Random();
        rgb = 0xff00a9e6;
    }

    public void setWord(String word) {
        this.word = word;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(rgb);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //先绘制圆
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2,
                paint);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.getTextBounds(word, 0, word.length(), mBounds);
        float textWidth = mBounds.width();
        float textHeight = mBounds.height();
        //再绘制文字
        canvas.drawText(word, getWidth() / 2 - textWidth / 2, getHeight() / 2
                + textHeight / 2, paint);
    }

}
