package com.nzl.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/*/**
 * Created by longhy on 2017/5/3.
 */

public class NZLSeekbar extends View {


    private Drawable grayBgView;
    private Drawable blueBgView;
    private Drawable blueLeftBgView;
    private Drawable seekBar;

    private int seekWidth;
    private int seekHeight;

    //根据屏幕的密度获取文字的大小
    private DisplayMetrics dm = this.getResources().getDisplayMetrics();

    //顶部文字和seekbar的间距
    private int topPaddingSeekBar = (int)(10 * dm.density);
    //刻度和文字的间距
    private int keduPaddingText = (int)(2 * dm.density);


    private Rect grayBgRect;
    private Rect blueBgRect;
    private Rect blueLeftBgRect;
    private Rect seekbarRect;

    //平均每一节的宽度
    private int oneWidth;

    private String[] list = {"500", "600", "700", "800", "900", "1000"};
    private float[] textWidth;

    private Paint mPaint;

    private int textColor = Color.parseColor("#666666");

    //底部文字的大小
    private int textSize = (int)(12 * dm.density);
    //底部刻度的大小
    private int textKeduSize = (int)(8 * dm.density);

    //顶部的文字和大小
    private String topText;
    private int topTextSize;
    private int topTextNonalSize = (int)(20 * dm.density);
    private int topTextBigSize = (int)(30 * dm.density);
    private float topTextWidth;

    //最后移动到的x
    private int lastDownX;

    /**
     * 百分比
     * 当前拖动的x占当前长度 / count的百分比
     * 如果当前count为5,percentage的范围从0~5
     */
    private float position = list.length - 1;
    //seekbar调整后的距离左边
    private float distance;

    private Scroller mScroller;
    private boolean isOnMeasure = true;


    public NZLSeekbar(Context context) {
        this(context, null, 0);
    }

    public NZLSeekbar(Context context, AttributeSet attr) {
        this(context, attr, 0);

    }

    public NZLSeekbar(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        if (attr == null)
            return;

        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.NZLSeekbar);
        seekBar = a.getDrawable(R.styleable.NZLSeekbar_my_seekBar);
        grayBgView = a.getDrawable(R.styleable.NZLSeekbar_grayView);
        blueBgView = a.getDrawable(R.styleable.NZLSeekbar_blueView);
        blueLeftBgView = a.getDrawable(R.styleable.NZLSeekbar_leftView);

        a.recycle();

        init();
    }


    private void init() {
        setClickable(true);
        setBackgroundColor(Color.WHITE);

        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

        grayBgRect = new Rect();
        blueBgRect = new Rect();
        blueLeftBgRect = new Rect();
        seekbarRect = new Rect();
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setStyle(Paint.Style.FILL);

        textWidth = new float[list.length];
        for (int i = 0; i < list.length; i++) {
            textWidth[i] = mPaint.measureText(list[i].toString());
        }

        topTextSize = topTextNonalSize;
        mPaint.setTextSize(topTextSize);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (isOnMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int heightModel = MeasureSpec.getMode(heightMeasureSpec);

            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int widthModel = MeasureSpec.getMode(widthMeasureSpec);

            int mWidth;
            int mHeight;


            seekWidth = seekBar.getIntrinsicWidth();
            seekHeight = seekBar.getIntrinsicHeight();

            if (widthModel == MeasureSpec.EXACTLY) {
                mWidth = widthSize;
            } else {
                mWidth = widthSize - getPaddingLeft() - getPaddingRight() + seekWidth / 2;
            }


            if (heightModel == MeasureSpec.EXACTLY) {
                mHeight = heightSize;
            } else {
                mHeight = seekHeight + topTextBigSize + topPaddingSeekBar + textSize + keduPaddingText + textKeduSize;
            }


            float rightPadding = Math.max(topTextWidth / 2, textWidth[list.length - 1]);


            grayBgRect.left = getPaddingLeft();
            grayBgRect.right = (int) (mWidth - getPaddingRight() - rightPadding);
            grayBgRect.top = mHeight / 2 - grayBgView.getIntrinsicHeight() / 2;
            grayBgRect.bottom = mHeight / 2 + grayBgView.getIntrinsicHeight() / 2;


            oneWidth = (grayBgRect.right - grayBgRect.left - seekWidth / 2) / (list.length - 1);
            distance = oneWidth * position + getPaddingLeft();


            blueLeftBgRect.left = grayBgRect.left - blueLeftBgView.getIntrinsicWidth() / 2 + 3;
            blueLeftBgRect.right = getPaddingLeft() + blueLeftBgView.getIntrinsicWidth() + 3;
            blueLeftBgRect.top = mHeight / 2 - blueLeftBgView.getIntrinsicHeight() / 2;
            blueLeftBgRect.bottom = mHeight / 2 + blueLeftBgView.getIntrinsicHeight() / 2;


            seekbarRect.left = (int) distance;
            seekbarRect.right = seekbarRect.left + seekWidth;
            seekbarRect.top = mHeight / 2 - seekHeight / 2;
            seekbarRect.bottom = mHeight / 2 + seekHeight / 2;


            blueBgRect.left = grayBgRect.left;
            blueBgRect.right = seekbarRect.centerX();
            blueBgRect.top = grayBgRect.top;
            blueBgRect.bottom = grayBgRect.bottom;


            setMeasuredDimension(mWidth, mHeight);
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        grayBgView.setBounds(grayBgRect);
        grayBgView.draw(canvas);


        blueBgView.setBounds(blueBgRect);
        blueBgView.draw(canvas);

        blueLeftBgView.setBounds(blueLeftBgRect);
        blueLeftBgView.draw(canvas);

        seekBar.setBounds(seekbarRect);
        seekBar.draw(canvas);


        mPaint.setColor(textColor);
        for (int i = 0; i < list.length; i++) {
            String keduText = "|";
            mPaint.setTextSize(textKeduSize);
            float keduWidth = mPaint.measureText(keduText);

            float left = seekWidth / 2 + oneWidth * i + getPaddingLeft() - keduWidth / 2;
            canvas.drawText(keduText, left, seekbarRect.bottom + topPaddingSeekBar, mPaint);


            mPaint.setTextSize(textSize);
            float textLeft = left + keduWidth / 2 - textWidth[i] / 2;
            canvas.drawText(list[i], textLeft, seekbarRect.bottom + topPaddingSeekBar + textSize + keduPaddingText, mPaint);

        }

        System.out.println("*********position*********"+position);
        if (position <= 0.5) {
            topText = "500";
            System.out.println("0000");
        } else if (position > 0.5 && position <= 1.5) {
            topText = "600";
            System.out.println("11");
        } else if (position > 1.5 && position <= 2.5) {
            topText = "700";
            System.out.println("22");
        } else if (position > 2.5 && position <= 3.5) {
            topText = "800";
            System.out.println("33");
        } else if (position > 3.5 && position <= 4.5) {
            topText = "900";
            System.out.println("44");
        } else if (position > 4.5) {
            topText = "1000";
            System.out.println("55");
        }


        mPaint.setTextSize(topTextSize);
        mPaint.setColor(Color.parseColor("#FF8400"));
        topTextWidth = mPaint.measureText(topText);
        float seekBarCenter = seekbarRect.centerX();
        canvas.drawText(topText, seekBarCenter - topTextWidth / 2, seekbarRect.top - 15, mPaint);

        if (mScroller.isFinished()) {
            Log.d("SeekBar topText", topText + "");
            if (listener != null)
                listener.onSeekChange(topText);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                handleTouchDown(event);
                System.out.println("*******ACTION_DOWN*******");
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleTouchUp(event);
                System.out.println("*******ACTION_UP*******");
                break;
        }

        return super.onTouchEvent(event);
    }


    private void handleTouchDown(MotionEvent event) {
        int downX = (int) event.getX();
        lastDownX = downX;
        isOnMeasure = false;

        topTextSize = topTextBigSize;

        position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / (float) oneWidth;

        seekbarRect.left = lastDownX - seekWidth / 2;
        seekbarRect.right = seekbarRect.left + seekWidth;
        blueBgRect.right = seekbarRect.centerX();
        invalidate();
    }


    private void handleTouchMove(MotionEvent event) {

        int downX = (int) event.getX();

        lastDownX = downX;

        if (lastDownX < grayBgRect.left + seekWidth / 2) {
            lastDownX = grayBgRect.left + seekWidth / 2;
            setPosition(0);
            return;
        }
        if (lastDownX > grayBgRect.right) {
            lastDownX = grayBgRect.right;
            setPosition(list.length - 1);
            return;
        }


        seekbarRect.left = lastDownX - seekWidth / 2;
        seekbarRect.right = seekbarRect.left + seekWidth;
        blueBgRect.right = seekbarRect.centerX();


        position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / (float) oneWidth;

        invalidate();
    }

    private void handleTouchUp(MotionEvent event) {
        topTextSize = topTextNonalSize;

        int downX = (int) event.getX();

        lastDownX = downX;
        position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / (float) oneWidth;

        if (lastDownX < grayBgRect.left + seekWidth / 2) {
            lastDownX = grayBgRect.left + seekWidth / 2;
            Log.e("SeekBar", "左边超出了");
        }
        if (lastDownX > grayBgRect.right) {
            lastDownX = grayBgRect.right;
            Log.e("SeekBar", "右边超出了");
        }

        if (position <= 0.5) {
            position = 0;
            distance = getPaddingLeft();
        } else if (position > 0.5 && position <= 1.5) {
            position = 1;
            distance = getPaddingLeft() + oneWidth;
        } else if (position > 1.5 && position <= 2.5) {
            position = 2;
            distance = getPaddingLeft() + oneWidth * 2;
        } else if (position > 2.5 && position <= 3.5) {
            position = 3;
            distance = getPaddingLeft() + oneWidth * 3;
        } else if (position > 3.5 && position <= 4.5) {
            position = 4;
            distance = getPaddingLeft() + oneWidth * 4;
        } else if (position > 4.5) {
            position = 5;
            distance = getPaddingLeft() + oneWidth * 5;
        }


        if (!mScroller.computeScrollOffset()) {
            mScroller.startScroll(lastDownX, 0, (int) distance - lastDownX, 0, 500);
        }

        Log.d("SeekBar position", position + "");
        invalidate();

    }


    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {

            final int deltaX = mScroller.getCurrX();

            float destinace = deltaX / (float) oneWidth;
            System.out.println("*****destinace**"+destinace);

            if (destinace < 1) {
                position = 0;
            } else if (destinace >= 1 && destinace < 2) {
                position = 1;
            } else if (destinace >= 2 && destinace < 3) {
                position = 2;
            } else if (destinace >=3 && destinace < 4) {
                position = 3;
            } else if (destinace >=4 && destinace < 5) {
                position = 4;
            } else if (destinace >= 5) {
                position = 5;
            }

            distance = oneWidth * destinace;

            seekbarRect.left = (int) distance;
            seekbarRect.right = seekbarRect.left + seekWidth;
            blueBgRect.right = seekbarRect.centerX();

            invalidate();
        }
    }


    /**
     * 设置seekbar选中的位置
     *
     * @param position
     */

    public void setPosition(int position) {

        if (position < 0 || position > list.length - 1)
            return;

        this.position = position;

        distance = oneWidth * position + getPaddingLeft();

        seekbarRect.left = (int) distance;
        seekbarRect.right = seekbarRect.left + seekWidth;
        blueBgRect.right = seekbarRect.centerX();

        topText = 500 + 100 * position + "";
        if (listener != null)
            listener.onSeekChange(topText);

        invalidate();

    }


    public OnSeekChangeListener listener;

    public interface OnSeekChangeListener {
        void onSeekChange(String changeText);
    }

    public void setOnSeekChangeListener(OnSeekChangeListener listener) {
        this.listener = listener;
    }


    private boolean inRangeOfView(MotionEvent ev) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + getWidth()) || ev.getY() < y || ev.getY() > (y + getHeight())) {
            return true;
        }
        return false;
    }

}


