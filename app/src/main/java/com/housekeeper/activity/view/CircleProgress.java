package com.housekeeper.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgress extends View {

    private CircleAttribute mCircleAttribute;
    private int mMaxProgress = 100;
    private int mSubCurProgress;
    private int mSecondCurProgress;

    public static final int ARC = 0;
    public static final int SECTOR = 1;
    public static final int ROUND = 2;

    private int type = ARC;

    public CircleProgress(Context paramContext) {
        this(paramContext, null);
    }

    public CircleProgress(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        defaultParam();
    }

    private void defaultParam() {
        this.mCircleAttribute = new CircleAttribute();
        this.mMaxProgress = 100;
        this.mSubCurProgress = 0;
        this.mSecondCurProgress = 0;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float f1 = 360.0F * this.mSubCurProgress / this.mMaxProgress;

        float f2 = 360.0F * this.mSecondCurProgress / this.mMaxProgress;

        Rect textBounds = new Rect();
        String str = String.valueOf(this.mSubCurProgress + "%");
        this.mCircleAttribute.mTextPaint.getTextBounds(str, 0, str.length(), textBounds);//get text bounds, that can get the text width and height
        int textHeight = textBounds.bottom - textBounds.top;

        switch (type) {
            case ARC:
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mBottomPaint);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, this.mCircleAttribute.mDrawPos, f1, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mSubPaint);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, this.mCircleAttribute.mDrawPos, f2, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mSecondPaint);
                canvas.drawArc(this.mCircleAttribute.inRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mMainPaints);
                //canvas.drawText(this.mSubCurProgress + "%", this.mCircleAttribute.mRoundOval.centerX(), this.mCircleAttribute.mRoundOval.centerY() + textHeight / 2, this.mCircleAttribute.mTextPaint);
                break;

            case SECTOR:
                this.mCircleAttribute.mTextPaint.setColor(Color.WHITE);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mBottomPaint);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, this.mCircleAttribute.mDrawPos, f1, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mSubPaint);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, this.mCircleAttribute.mDrawPos, f2, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mSecondPaint);
                canvas.drawText(this.mSubCurProgress + "%", this.mCircleAttribute.mRoundOval.centerX(), this.mCircleAttribute.mRoundOval.centerY() + textHeight / 2, this.mCircleAttribute.mTextPaint);
                break;

            case ROUND:
                float top = this.mCircleAttribute.mRoundOval.height() - this.mCircleAttribute.mRoundOval.height() * this.mSubCurProgress / 100;
                this.mCircleAttribute.mTextPaint.setColor(Color.WHITE);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mBottomPaint);

                canvas.save();
                RectF rectF = new RectF();
                rectF.set(this.mCircleAttribute.mRoundOval.left, top, this.mCircleAttribute.mRoundOval.right, this.mCircleAttribute.mRoundOval.bottom);
                canvas.clipRect(rectF);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, true, this.mCircleAttribute.mSubPaint);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, true, this.mCircleAttribute.mSecondPaint);
                canvas.restore();
                canvas.drawText(this.mSubCurProgress + "%", this.mCircleAttribute.mRoundOval.centerX(), this.mCircleAttribute.mRoundOval.centerY() + textHeight / 2, this.mCircleAttribute.mTextPaint);
                break;
        }

    }

    /**
     * 设置进度
     *
     * @param progress 取值范围0-100
     */
    public void setmSubCurProgress(int progress) {
        this.mSubCurProgress = progress;
        invalidate();
    }

    public void setmSecondCurProgress(int progress) {
        this.mSecondCurProgress = progress;
        invalidate();
    }

    public void setmMaxProgress(int progress) {
        this.mMaxProgress = progress;
        invalidate();
    }

    public void setSubPaintColor(int color) {
        this.mCircleAttribute.mSubPaintColor = color;
        this.mCircleAttribute.mSubPaint.setColor(color);

        invalidate();
    }

    public void setSecondPaintColor(int color) {
        this.mCircleAttribute.mSecondPaintColor = color;
        this.mCircleAttribute.mSecondPaint.setColor(color);

        invalidate();
    }

    public void setBottomPaintColor(int color) {
        this.mCircleAttribute.mBottomPaintColor = color;
        this.mCircleAttribute.mBottomPaint.setColor(color);

        invalidate();
    }

    /**
     * 设置圆形进度条的样式
     *
     * @param type ARC,SECTOR,ROUND
     */
    public void setType(int type) {
        this.type = type;
    }

    public void setPaintWidth(int width) {
        this.mCircleAttribute.mSidePaintInterval = width;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = MeasureSpec.getSize(widthMeasureSpec);
        MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSize(i, widthMeasureSpec), resolveSize(i, heightMeasureSpec));
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        this.mCircleAttribute.autoFix(paramInt1, paramInt2);
    }


    class CircleAttribute {
        public boolean mBRoundPaintsFill = true;
        public Paint mBottomPaint;
        public int mDrawPos = -90;
        public Paint mMainPaints;
        public int mSubPaintColor = Color.parseColor("#1caff6");
        public int mSecondPaintColor = Color.parseColor("#26B7FF");
        public int mBottomPaintColor = Color.parseColor("#CCCCCC");
        public int mMainPaintColor = Color.parseColor("#FFFFFF");
        public int mPaintWidth = 0;
        public RectF mRoundOval = new RectF();
        public RectF inRoundOval = new RectF();
        public int mSidePaintInterval = 5;
        public Paint mSubPaint;
        public Paint mSecondPaint;
        public Paint mTextPaint;
        public int mTextPaintColor = Color.parseColor("#FF001A");
        public int mTextSize = 22;

        public CircleAttribute() {
            this.mMainPaints = new Paint();
            this.mMainPaints.setAntiAlias(true);
            this.mMainPaints.setStyle(Paint.Style.FILL);
            this.mMainPaints.setStrokeWidth(this.mPaintWidth);
            this.mMainPaints.setColor(this.mMainPaintColor);

            this.mSubPaint = new Paint();
            this.mSubPaint.setAntiAlias(true);
            this.mSubPaint.setStyle(Paint.Style.FILL);
            this.mSubPaint.setStrokeWidth(this.mPaintWidth);
            this.mSubPaint.setColor(this.mSubPaintColor);

            this.mSecondPaint = new Paint();
            this.mSecondPaint.setAntiAlias(true);
            this.mSecondPaint.setStyle(Paint.Style.FILL);
            this.mSecondPaint.setStrokeWidth(this.mPaintWidth);
            this.mSecondPaint.setColor(this.mSecondPaintColor);

            this.mBottomPaint = new Paint();
            this.mBottomPaint.setAntiAlias(true);
            this.mBottomPaint.setStyle(Paint.Style.FILL);
            this.mBottomPaint.setStrokeWidth(this.mPaintWidth);
            this.mBottomPaint.setColor(this.mBottomPaintColor);

            this.mTextPaint = new Paint();
            this.mTextPaint.setAntiAlias(true);
            this.mTextPaint.setColor(this.mTextPaintColor);
            this.mTextPaint.setTextSize(this.mTextSize);
            this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        public void autoFix(int width, int height) {
            this.inRoundOval.set(this.mPaintWidth / 2 + this.mSidePaintInterval, this.mPaintWidth / 2 + this.mSidePaintInterval, width - this.mPaintWidth / 2 - this.mSidePaintInterval, height - this.mPaintWidth / 2 - this.mSidePaintInterval);
            int left = CircleProgress.this.getPaddingLeft();
            int right = CircleProgress.this.getPaddingRight();
            int top = CircleProgress.this.getPaddingTop();
            int bottom = CircleProgress.this.getPaddingBottom();
            this.mRoundOval.set(left + this.mPaintWidth / 2, top + this.mPaintWidth / 2, width - right - this.mPaintWidth / 2, height - bottom - this.mPaintWidth / 2);
        }

//        public void setPaintColor(int paintColor)
//        {
//            this.mMainPaints.setColor(paintColor);
//            int i = 0x66000000 | 0xFFFFFF & paintColor;
//            this.mSubPaint.setColor(i);
//        }
//
//        public void setPaintWidth(int paintWidth)
//        {
//            this.mMainPaints.setStrokeWidth(paintWidth);
//            this.mSubPaint.setStrokeWidth(paintWidth);
//            this.mBottomPaint.setStrokeWidth(paintWidth);
//        }
    }
}