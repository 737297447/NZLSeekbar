package com.beihui.aixin.view;

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

import com.beihui.aixin.R;

/**
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
    private int topPaddingSeekBar = (int) (10 * dm.density);
    //刻度和文字的间距
    private int keduPaddingText = (int) (2 * dm.density);


    private Rect grayBgRect;
    private Rect blueBgRect;
    private Rect blueLeftBgRect;
    private Rect seekbarRect;

    //平均每一节的宽度
    private float oneWidth;
    //右边的不可选择的距离
    private float oneNoWidth = 0;


    private String[] list;

    private float[] textWidth;

    private Paint mPaint;

    private int textColor = Color.parseColor("#666666");

    //底部文字的大小
    private int textSize = (int) (10 * dm.density);
    //底部刻度的大小
    private int textKeduSize = (int) (8 * dm.density);

    private float rightPadding = 10 * dm.density;


    //顶部的文字和大小
    private String topText;
    private int topTextSize;
    private int topTextNonalSize = (int) (20 * dm.density);
    private int topTextBigSize = (int) (30 * dm.density);
    private float topTextWidth;

    //最后移动到的x
    private int lastDownX;

    /**
     * 百分比
     * 当前拖动的x占当前长度 / count的百分比
     * 如果当前count为5,percentage的范围从0~5
     */
    private float position;
    private float baifenbi = 0;

    //seekbar调整后的距离左边
    private float distance;

    private Scroller mScroller;
    private boolean isOnMeasure = true;


    private int mWidth = 0;
    private int mHeight = 0;


    //当前用户可借的最小钱数
    private int minMoney = 500;
    //当前用户可借的最大钱数
    private int maxMoney = 1000;
    //当前用户不可借的最大钱数,maxNoMoney < maxMoney右边不显示
    private int maxNoMoney = 500;


    private float seekBarTrueWidth;


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

        setMaxMoney(maxMoney);

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


    /**
     * 初始化可用最大值
     *
     * @param maxMoney
     */
    public void setMaxMoney(int maxMoney) {
        if (maxMoney <= minMoney)
            return;

        this.maxMoney = maxMoney;
        int size = (maxMoney - minMoney) / 100 + 1;


        list = new String[size];
        position = list.length - 1;


        for (int i = 0; i < size; i++) {
            list[i] = minMoney + i * 100 + "";
        }

        init();

    }

    /**
     * 初始化不可用最大值
     *
     * @param maxNoMoney
     */
    public void setMaxNoMoney(int maxNoMoney) {

        if (maxNoMoney <= maxMoney)
            return;
        this.maxNoMoney = maxNoMoney;

        init();

    }


    /**
     * 动态改变可用的最大值
     *
     * @param maxMoney
     */
    public void setChangeMaxMoney(int maxMoney) {
        if (maxMoney <= minMoney)
            return;

        this.maxMoney = maxMoney;
        int size = (maxMoney - minMoney) / 100 + 1;


        list = new String[size];
        position = list.length - 1;
        for (int i = 0; i < size; i++) {
            list[i] = minMoney + i * 100 + "";
        }


        oneWidth = (seekBarTrueWidth - oneNoWidth) / (list.length - 1);



        textWidth = new float[list.length];
        for (int i = 0; i < list.length; i++) {
            textWidth[i] = mPaint.measureText(list[i].toString());
        }

        invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);

        if (isOnMeasure) {

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


            grayBgRect.left = getPaddingLeft() + 5;
            grayBgRect.right = (int) (mWidth - getPaddingRight() - rightPadding);
            grayBgRect.top = mHeight / 2 - grayBgView.getIntrinsicHeight() / 2;
            grayBgRect.bottom = mHeight / 2 + grayBgView.getIntrinsicHeight() / 2;

            seekBarTrueWidth = grayBgRect.right - grayBgRect.left - seekWidth / 2;

            if (maxNoMoney > maxMoney) {
                oneNoWidth = seekBarTrueWidth / 5;
            }


            oneWidth = (seekBarTrueWidth - oneNoWidth) / (list.length - 1);
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
        } else {

            if (mHeight == 0) {
                mHeight = heightSize;
            }
            setMeasuredDimension(widthMeasureSpec, mHeight);
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
        float keduWidth = 0;

        for (int i = 0; i < list.length; i++) {
            //String keduText = "|";
            String keduText = "";
            if (i == 0 || i == list.length - 1) {
                keduText = "|";
            } else {
                keduText = "";
            }


            mPaint.setTextSize(textKeduSize);
            keduWidth = mPaint.measureText(keduText);
            float left = seekWidth / 2 + oneWidth * i + getPaddingLeft() - keduWidth / 2;


//            float trueRangeWidth = seekBarTrueWidth - oneNoWidth + getPaddingLeft();


//            if (i == list.length - 1) {
//                canvas.drawText(keduText, trueRangeWidth - keduWidth / 2, seekbarRect.bottom + topPaddingSeekBar, mPaint);
//            } else {
            canvas.drawText(keduText, left, seekbarRect.bottom + topPaddingSeekBar, mPaint);
//            }


            mPaint.setTextSize(textSize);
            float textLeft = left + keduWidth / 2 - textWidth[i] / 2;

            if (i == 0) {
                canvas.drawText(minMoney + "", seekWidth / 2  + getPaddingLeft() - mPaint.measureText(minMoney + "") / 2, seekbarRect.bottom + topPaddingSeekBar + textSize + keduPaddingText, mPaint);
            } else if (i == list.length - 1) {
                canvas.drawText(maxMoney + "", left + keduWidth / 2 - mPaint.measureText(maxMoney + "") / 2, seekbarRect.bottom + topPaddingSeekBar + textSize + keduPaddingText, mPaint);
            } else {
                canvas.drawText("", textLeft, seekbarRect.bottom + topPaddingSeekBar + textSize + keduPaddingText, mPaint);
            }


        }


        if (oneNoWidth != 0) {
            canvas.drawText("|", grayBgRect.right - keduWidth / 2, seekbarRect.bottom + topPaddingSeekBar, mPaint);
            float maxNoMoneyWidth = mPaint.measureText(maxNoMoney + "");
            canvas.drawText(maxNoMoney + "", grayBgRect.right - maxNoMoneyWidth / 2, seekbarRect.bottom + topPaddingSeekBar + textSize + keduPaddingText, mPaint);
        }


        if (baifenbi > 0) {
            int maxNo = Integer.valueOf(maxNoMoney) - Integer.valueOf(maxMoney);

            float i = baifenbi * maxNo;


            if (i % 100 != 0) {
                i = (int) (i / 100) * 100;

            }


            topText = 2000 + (int) (i) + "";
        } else {
            if (position < list.length - 1 && position >= 0)
                topText = list[Math.round(position)];
            else if (position < 0)
                topText = minMoney + "";
            else
                topText = maxMoney + "";
        }


        mPaint.setTextSize(topTextSize);
        mPaint.setColor(Color.parseColor("#EB4735"));
        topTextWidth = mPaint.measureText(topText);
        float seekBarCenter = seekbarRect.centerX();
        canvas.drawText(topText, seekBarCenter - topTextWidth / 2, seekbarRect.top - 15, mPaint);

        if (mScroller.isFinished()) {
            // Log.d("SeekBar topText", topText + "");
            if (listener != null)
                listener.onSeekChange(topText);
        }

    }

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
            case MotionEvent.ACTION_POINTER_DOWN:
                handleTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                handleTouchMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleTouchUp(event);
                break;
        }

        return super.onTouchEvent(event);
    }


    private void handleTouchDown(MotionEvent event) {
        int downX = (int) event.getX();
        lastDownX = downX;
        isOnMeasure = false;

        topTextSize = topTextBigSize;

        position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / oneWidth;


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

        if (oneNoWidth == 0) {
            if (lastDownX > grayBgRect.right) {
                lastDownX = grayBgRect.right;
                setPosition(list.length - 1);
                return;
            }

        } else {
            if (lastDownX >= grayBgRect.right) {
                System.out.println("*******yuejie****************");
                baifenbi = 1;
                invalidate();
                return;
            }
        }


        seekbarRect.left = lastDownX - seekWidth / 2;
        seekbarRect.right = seekbarRect.left + seekWidth;
        blueBgRect.right = seekbarRect.centerX();


        if (lastDownX < grayBgRect.right - oneNoWidth) {
            position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / oneWidth;
            baifenbi = 0;
        } else {
            position = list.length - 1;
            baifenbi = (lastDownX - grayBgRect.right + oneNoWidth) /  oneNoWidth;
        }

        if (listener != null) {
            if (baifenbi == 0) {
                listener.onRangeChange(1);
            } else {
                listener.onRangeChange(2);
            }
        }


        invalidate();
    }

    private void handleTouchUp(MotionEvent event) {
        topTextSize = topTextNonalSize;

        int downX = (int) event.getX();

        lastDownX = downX;


        if (lastDownX < grayBgRect.right - oneNoWidth) {
            position = (lastDownX - seekWidth / 2 - getPaddingLeft()) / oneWidth;

        } else {
            position = list.length - 1;

        }


        baifenbi = 0;
        if (listener != null)
            listener.onRangeChange(1);


        if (lastDownX < grayBgRect.left + seekWidth / 2) {
            lastDownX = grayBgRect.left + seekWidth / 2;
            Log.e("SeekBar", "左边超出了");
            position = 0;
        }
        if (lastDownX > grayBgRect.right) {
            lastDownX = grayBgRect.right;
            Log.e("SeekBar", "右边超出了");
        }


        position = Math.round(position);
        distance = getPaddingLeft() + oneWidth * position;


        if (!mScroller.computeScrollOffset()) {
            mScroller.startScroll(lastDownX, 0, (int) distance - lastDownX, 0, minMoney);
        }


        Log.d("SeekBar position", position + "");
        invalidate();

    }


    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            final int deltaX = mScroller.getCurrX();

            float destinace = deltaX /  oneWidth;

//            if ((grayBgRect.left + seekWidth / 2) <= oneWidth) {
//
//                if (destinace - 0.5 > 0)
//                    position = Math.round(destinace - 0.5);
//                else
//                    position = 0;
//
//            } else {
//
//                if (destinace - 1.5 > 0)
//                    position = Math.round(destinace - 1.5);
//                else
//                    position = 0;
//
//            }
//
//            if (position >= list.length)
//                position = list.length - 1;


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

        // topText = minMoney + 100 * position + "";
        topText = minMoney + 100 * position + "";
        if (listener != null)
            listener.onSeekChange(topText);

        invalidate();

    }


    public OnSeekChangeListener listener;

    public interface OnSeekChangeListener {
        void onSeekChange(String changeText);

        //type = 1 ,没有超过maxMoney,type = 2超过maxMoney
        void onRangeChange(int type);

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
