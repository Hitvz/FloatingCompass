package xxhui.space.floatingcompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;

import xxhui.space.floatingcompass.interfaces.CompassDoubleListener;
import xxhui.space.floatingcompass.interfaces.CompassSizeChangeListener;

import static android.content.ContentValues.TAG;

/**
 * Created by hui on 2017/1/16.
 */

public class CompassView extends View {

    //设置wrap_content的时候的默认宽和高
    private int defaultWidth = 400;
    private int defaultHeight = defaultWidth;
    //记录view的大小
    private int viewWidth;
    private int viewHeight;

    private Paint outterPaint;

    private Paint textPaint;//文字画笔
    private Paint centerPaint;//中心圆画笔
    private Paint midPaint;//类时钟刻度画笔
    private Paint northPaint;//指示北方的画笔

    private Paint sidePaint;//调节大小的边界边的画笔

    private String[] aText = {"N", "E", "S", "W"};
    private int rotation = 0;
    private long defaultDelay = 500;
    private int preRotation;

    private int radius;

    /**
     * 位置信息
     */
    private int l = 0;
    private int t = 0;
    private int r = 0;
    private int b = 0;

    /**
     * 监听ontouchevent的事件
     */
    private CompassSizeChangeListener compassSizeChangeListener;

    private boolean isSizeChange = false;

    private CompassDoubleListener compassDoubleListener;



    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    //记录手指按下时在小悬浮窗的View上的横坐标的值
    private float xInView;

    //在view中移动的横坐标值
    private float xMoveInView;

    //记录手指按下时在小悬浮窗的View上的纵坐标的值
    private float yInView;

    //在view中移动的纵坐标值
    private float yMoveInView;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xMoveInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yMoveInScreen;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    public CompassView(Context context) {
        super(context);
        initRes(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRes(context);
    }

    private void initRes(Context context) {
        // 给View设置背景颜色，便于观察
        setBackgroundColor(Color.argb(0, 0, 0, 0));//透明（0）~~不透明（255），三个255是白色

        outterPaint = new Paint();
        outterPaint.setColor(Color.argb(25, 0, 0, 0));
        //文字的画笔
        textPaint = new Paint();
        textPaint.setColor(Color.GRAY);
        //圆心的画笔
        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        centerPaint.setColor(Color.WHITE);
        centerPaint.setTextSize(20);
        //圆刻度的画笔
        midPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //指示北方的三角形的画笔
        northPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        northPaint.setColor(Color.RED);

        //调节大小的边界边的画笔
        sidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sidePaint.setColor(Color.BLUE);
        sidePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
    }

    private void drawCenterCircle(Canvas canvas, int radius) {//画一个内部的圆
        radius = radius * 82 / 120;

        outterPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius * 120 / 82, outterPaint);

        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius, centerPaint);
    }

    private void drawLineCircle(Canvas canvas, int radius) {//画类似时钟的刻度
        //动态计算刻度的宽度，范围在2.0f~6.0f之间
        if (radius <= 100) {
            midPaint.setStrokeWidth(2.0f);
        } else if (radius > 100 && radius <= 300) {
            float width = 4.0f * radius * 2 / defaultWidth;
            midPaint.setStrokeWidth(width);
        } else {
            midPaint.setStrokeWidth(6.0f);
        }

        int midX = viewWidth / 2;
        int startY = viewHeight / 2 - radius + radius * 23 / 120;
        int stopY = startY + radius * 10 / 120;

        //每画一条线旋转6度，360度循环刚好回到原来的位置
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                midPaint.setColor(Color.WHITE);
            } else {
                midPaint.setColor(Color.GRAY);
            }
            canvas.drawLine(midX, startY, midX, stopY, midPaint);
            canvas.rotate(-6, viewWidth / 2, viewHeight / 2);//6度（60/360）
        }
    }

    private void drawNorthTriangle(Canvas canvas, int radius) {
        int topX = viewWidth / 2;
        int topY = viewHeight / 2 - radius;
        int leftX = topX - radius * 10 / 120;
        int leftY = topY + radius * 18 / 120;
        int rightX = topX + radius * 10 / 120;
        int rightY = leftY;
        Path path = new Path();
        path.moveTo(topX, topY);
        path.lineTo(leftX, leftY);
        path.lineTo(rightX, rightY);
        path.close();
        canvas.drawPath(path, northPaint);
    }

    private void drawText(Canvas canvas, int radius) {
        int baseLineX = viewWidth / 2;
        int baseLineY = viewHeight / 2 - radius * 57 / 120;

        float baseSize = radius * 18 / 120;
        float bestSize = 0;
        float maxTextSize = 200.0f;
        int count = 0;
        while (true) {
            textPaint.setTextSize(100.0f * count);
            if (baseSize < textPaint.measureText("N")) {
                maxTextSize = 100.0f * count;
                break;
            }
            count++;
        }

        for (int i = 0; i < maxTextSize; i++) {
            textPaint.setTextSize(1.0f * i);
            if (baseSize < textPaint.measureText("N")) {
                bestSize = 1.0f * i;
                break;
            }

        }
        textPaint.setTextSize(bestSize);

        float textWidth;
        for (String text : aText) {
            textWidth = textPaint.measureText(text);
            canvas.drawText(text, baseLineX - textWidth / 2, baseLineY, textPaint);
            canvas.rotate(90, viewWidth / 2, viewHeight / 2);
        }
    }

    private void drawSide(Canvas canvas) {
        if (!isSizeChange) {
            Log.i(TAG, "drawSide: return");
            return;
        }
        //safeRotate(canvas);
        int size = viewHeight > viewWidth ? viewWidth : viewHeight;
        canvas.drawRect(0, 0, size, size, sidePaint);

    }

    //修复-179跳到179的界面bug
    private void safeRotate(Canvas canvas) {
//        Log.i(TAG, "safeRotate: "+rotation);
        int r = -rotation;
        canvas.rotate(r, viewWidth / 2, viewHeight / 2);
        if (r > 90 || r < -90) {
            preRotation = r;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (getVisibility()) {
            case View.GONE:
            case View.INVISIBLE:
                this.setBackgroundColor(Color.argb(0, 0, 0, 0));
                this.willNotDraw();
                return;
            case View.VISIBLE:
                this.setWillNotDraw(false);
                break;
            default:
                break;
        }
        //       if(willDraw) {
        int radius = Math.min(getWidth(), getHeight()) / 2;//求圆的半径，通过宽和高的小的一个值确定
        this.radius = radius;
        //safeRotate(canvas);//必须先旋转再画其他才能达到动画效果，不让静止不动。
        drawCenterCircle(canvas, radius);
        drawText(canvas, radius);
        drawLineCircle(canvas, radius);
        drawNorthTriangle(canvas, radius);
        //drawSide(canvas);
        // postInvalidate();
//            if(count==5) {
//                willDraw = false;
//            }
//            count++;
//        }
//        Log.i(TAG, "onDraw: "+willDraw+count);

    }

    public void setRatation(int rotation) {
        this.rotation = rotation;
        //Log.i(TAG, "setRatation: set " + rotation);
        // postInvalidateDelayed(defaultDelay);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {//确定view的位置
        super.onLayout(changed, left, top, right, bottom);
        l = left;
        r = right;
        t = top;
        b = bottom;
        Log.i(TAG, "onLayout: " + left + "--" + right);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//测量view自身大小
        //当有一个是match_parent的时候，那个是改变不了大小的
        //只有两个都声明为数值才能生效，宽高相同
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int desireWidth = widthSpecSize > heightSpecSize ? heightSpecSize : widthSpecSize;//正方形所以这样子
        int desireHeight = desireWidth;
        //Log.i(TAG, "onMeasure: "+widthSpecSize + "---"+heightSpecSize);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {//宽和高都指定为wrap_content,自己指定宽高
            //setMeasuredDimension(defaultWidth, defaultHeight);
            desireWidth = defaultWidth;
            desireHeight = defaultHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {//宽指定为wrap_content,自己指定
            //setMeasuredDimension(defaultWidth, heightSpecSize);
            desireWidth = defaultWidth;
            desireHeight = defaultHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {//高指定为wrap_content,自己指定高
            //setMeasuredDimension(widthSpecSize, defaultHeight);
            desireWidth = defaultWidth;
            desireHeight = defaultHeight;
        }
        //Log.i(TAG, "onMeasure: "+desireWidth + "---"+desireWidth);
        setMeasuredDimension(desireWidth, desireHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//当view大小发生改变的时候记录下新的大小
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(compassDoubleListener!=null)
        {
            compassDoubleListener.onThreeClick(event);
            if(isSizeChange){
                //Log.i(TAG, "onTouchEvent: ");
                return viewSizeHandle(event);//必须return才能完成里面的逻辑
            }
        }
        if (this.mParams != null) {
            return viewDrawHandle(event);
        }

        return super.onTouchEvent(event);
    }



    private boolean viewSizeHandle(MotionEvent event) {
        if(event.getPointerCount()!=2){
            //Log.i(TAG, "viewSizeHandle: 2 finger");
           // return false;
        }
        //Log.i(TAG, "viewSizeHandle: 1");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "viewSizeHandle: down"+event.getActionIndex());
                xInView = event.getX();
                yInView = event.getY();
                xMoveInView = xInView;
                yMoveInView = yInView;
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount()==2) {
                    //Log.i(TAG, "onTouchEvent:0 x:"+event.getX(0)+" y:"+event.getY(0));
                    //Log.i(TAG, "onTouchEvent:1 x:"+event.getX(1)+" y:"+event.getY(1));
                    float xDis = event.getX(0)-event.getX(1);
                    float yDis = event.getY(0)-event.getY(1);
                    //Log.i(TAG, "onTouchEvent:* x:"+xDis+" y:"+yDis);
                    updateViewSize((float) Math.sqrt(xDis*xDis+yDis+yDis));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TAG, "viewSizeHandle: point down"+event.getActionIndex());
                break;

            case MotionEvent.ACTION_UP:
                preDis =0 ;
                Log.i(TAG, "viewSizeHandle: up"+event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.i(TAG, "viewSizeHandle: point up"+event.getActionIndex());
            //case MotionEvent.ACTION_HOVER_MOVE
            default:
                break;
        }
        return true;

    }

    private float preDis;
    private void updateViewSize(float dis) {
        if(preDis==0){
            preDis = dis;
            return;
        }
        Log.i(TAG, "updateViewSize: ");
        compassSizeChangeListener.sizeChange(dis-preDis);
        preDis = dis;
    }
    private boolean viewDrawHandle(MotionEvent event) {
        Log.i(TAG, "viewDrawHandle: ");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();// 第一次按下x的值
                yDownInScreen = event.getRawY();// - getStatusBarHeight();// 第一次按下y的值
                xMoveInScreen = event.getRawX();// 第一次按下x的值
                yMoveInScreen = event.getRawY();// - getStatusBarHeight();// 第一次按下y的值
                break;
            /**
             * 如果没有移动，就是下面的case MotionEvent.ACTION_MOVE没有执行，就会符合case
             * MotionEvent.ACTION_UP里面的条件
             */
            case MotionEvent.ACTION_MOVE:
                xMoveInScreen = event.getRawX();
                yMoveInScreen = event.getRawY();//; - getStatusBarHeight();
                //Log.i("float", Math.abs(xDownInScreen - xInScreen) + "");
                int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(xDownInScreen - xMoveInScreen) < touchSlop
                        && Math.abs(yDownInScreen - yMoveInScreen) < touchSlop) {
                    xMoveInScreen = xDownInScreen;
                    yMoveInScreen = yDownInScreen;
                }
                //Log.i(TAG, xDownInScreen+" down "+ yDownInScreen+"     "+xMoveInScreen+" move "+yMoveInScreen +"    "+xInView +" view "+yInView);
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xMoveInScreen && yDownInScreen == yMoveInScreen) {
                    //Log.i("up", "up");
                }
                break;
            default:
                break;
        }
        return true;
    }


    private int count = 0;
    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        count = 0;
        mParams.x = (int) (xMoveInScreen - xInView);
        mParams.y = (int) (yMoveInScreen - yInView);
        //Log.i(TAG, mParams.x+" update "+mParams.y);
        windowManager.updateViewLayout(this, mParams);

    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
        Log.i(TAG, mParams.height + "setParams: " + mParams.width);
    }

    public boolean switchSizeChange() {
        isSizeChange = !isSizeChange;
        Log.i(TAG, "switchSizeChange: before - " + isSizeChange);
        //postInvalidate();
        return isSizeChange;
    }


    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public int getRadius() {
        return radius;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }

    public int getR() {
        return r;
    }

    public int getB() {
        return b;
    }

    public boolean isSizeChange() {
        return isSizeChange;
    }

    public void setCompassSizeChangeListener(CompassSizeChangeListener compassSizeChangeListener) {
        this.compassSizeChangeListener = compassSizeChangeListener;
    }

    public void setCompassDoubleListener(CompassDoubleListener compassDoubleListener) {
        this.compassDoubleListener = compassDoubleListener;
    }

}
