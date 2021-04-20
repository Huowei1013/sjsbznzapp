package com.thirdsdks.timeline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.thirdsdks.R;

/**
 * Created by zhaotao on 2021/01/15 11:31.
 */

public class TimeLines extends View {

    private int mMarkerSize = 24;
    private int mLineSize = 9;
    private Drawable mBeginLines;
    private Drawable mEndLines;
    private Drawable mMarkerDrawable;
    private String word;
    private int color;
    private Canvas canvas = new Canvas();
    int pLeft = getPaddingLeft();
    int pRight = getPaddingRight();
    int pTop = getPaddingTop();
    int pBottom = getPaddingBottom();

    int width = getWidth();
    int height = getHeight();

    int cWidth = width - pLeft - pRight;
    int cHeight = height - pTop - pBottom;

    public TimeLines(Context context) {
        this(context, null);
    }

    public TimeLines(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLines(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeLineMarker, 0, 0);
        mMarkerSize = a.getDimensionPixelSize(R.styleable.TimeLineMarker_markerSize, mMarkerSize);
        mLineSize = a.getDimensionPixelSize(R.styleable.TimeLineMarker_LineSize, mLineSize);
        mBeginLines = a.getDrawable(R.styleable.TimeLineMarker_beginLine);
        mEndLines = a.getDrawable(R.styleable.TimeLineMarker_endLine);
        mMarkerDrawable = a.getDrawable(R.styleable.TimeLineMarker_marker);
        word = a.getString(R.styleable.TimeLineMarker_Text);
        a.recycle();
        if (mBeginLines != null) {
            mBeginLines.setCallback(this);
        }
        if (mEndLines != null) {
            mEndLines.setCallback(this);
        }
        if (mMarkerDrawable != null) {
            mMarkerDrawable.setCallback(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBeginLines != null) {
            mBeginLines.draw(canvas);
        }
        if (mEndLines != null) {
            mEndLines.draw(canvas);
        }
        if (mMarkerDrawable != null) {
            mMarkerDrawable.draw(canvas);
        }
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //先绘制圆
        Rect bounds = new Rect();
        canvas.drawCircle(getWidth() / 2, getHeight() / 3 , getWidth() / 3,
                paint);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.getTextBounds(word, 0, word.length(), bounds);
        float textWidth = bounds.width();
        float textHeight = bounds.height();
        //再绘制文字
        canvas.drawText(word, getWidth() / 2 - textWidth / 2, getHeight() / 3
                + textHeight / 2, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawableSize();
    }

    private void initDrawableSize() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();
        int height = getHeight();

        int cWidth = width - pLeft - pRight;
        int cHeight = height - pTop - pBottom;

        Rect bounds;
        if(word != null){
            int markerSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));
            bounds = new Rect(pLeft, pTop, pLeft + markerSize, pTop + markerSize);
        }else {
            bounds = new Rect(pLeft, pTop, pLeft + cWidth, pTop + cHeight);
        }

//        if (mMarkerDrawable != null) {
//            // Size
//            int markerSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));
//            mMarkerDrawable.setBounds(pLeft, pTop, pLeft + markerSize, pTop + markerSize);
//            bounds = mMarkerDrawable.getBounds();
//        } else {
//
//        }

        int halfLineSize = mLineSize >> 1;
//        int lineLeft = bounds.centerX() - halfLineSize;
        int lineLeft = getWidth()/2 - halfLineSize;

        if (mBeginLines != null) {
            mBeginLines.setBounds(lineLeft, 0, lineLeft + mLineSize, bounds.top);
        }

        if (mEndLines != null) {
            mEndLines.setBounds(lineLeft, bounds.bottom, lineLeft + mLineSize, height);
        }
    }

    public void setLineSize(int lineSize) {
        if (mLineSize != lineSize) {
            this.mLineSize = lineSize;
            initDrawableSize();
            invalidate();
        }
    }
    public void setWord(String word) {
        this.word = word;
        initDrawableSize();
        invalidate();
    }
    public void setBackGround(int color){
        this.color = color;
        initDrawableSize();
        invalidate();
    }

    public void setMarkerSize(int markerSize) {
        if (this.mMarkerSize != markerSize) {
            mMarkerSize = markerSize;
            initDrawableSize();
            invalidate();
        }
    }

    public void setBeginLine(Drawable beginLine) {
        if (this.mBeginLines != beginLine) {
            this.mBeginLines = beginLine;
            if (mBeginLines != null) {
                mBeginLines.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setEndLine(Drawable endLine) {
        if (this.mEndLines != endLine) {
            this.mEndLines = endLine;
            if (mEndLines != null) {
                mEndLines.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarkerDrawable(Drawable markerDrawable) {
        if (this.mMarkerDrawable != markerDrawable) {
            this.mMarkerDrawable = markerDrawable;
            if (mMarkerDrawable != null) {
                mMarkerDrawable.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }
}
