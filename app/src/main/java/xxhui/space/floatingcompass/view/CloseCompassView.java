package xxhui.space.floatingcompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CloseCompassView extends View {

    private static final String TAG = "CloseCompassView";

    private Paint colseCirclePaint;

    private int radius;//圆半径

    //设置wrap_content的时候的默认宽和高
    private int defaultWidth = 400;
    private int defaultHeight = defaultWidth;
    //记录view的大小
    private int viewWidth;
    private int viewHeight;

    public CloseCompassView(Context context) {
        super(context);
        initView(context);
    }

    public CloseCompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        Log.i(TAG, "initView: " + getWidth() + " " + getHeight());
        int radius = Math.min(getWidth(), getHeight()) / 2;//求圆的半径，通过宽和高的小的一个值确定
        viewWidth = radius;
        viewHeight = radius;
        // 给View设置背景颜色，便于观察
        setBackgroundColor(Color.argb(0, 0, 0, 0));//透明（0）~~不透明（255），三个255是白色
        colseCirclePaint = new Paint();
        colseCirclePaint.setColor(Color.argb(120, 0, 199, 140));
    }

    private void drawCenterCircle(Canvas canvas, int radius) {
        radius = radius * 9 / 10;
        int circleX = viewWidth / 2;
        int circleY = viewHeight / 2;
        colseCirclePaint.setStyle(Paint.Style.STROKE);
        colseCirclePaint.setStrokeWidth(7);//设置画笔粗细
        canvas.drawCircle(circleX, circleY, radius, colseCirclePaint);
        canvas.rotate(45, circleX, circleY);
        canvas.drawLine(circleX - radius, circleY, circleX + radius, circleY, colseCirclePaint);
        canvas.rotate(90, circleX, circleY);
        canvas.drawLine(circleX - radius, circleY, circleX + radius, circleY, colseCirclePaint);
        Log.i(TAG, "drawCenterCircle  radius:" + radius + " viewWidth:" + (viewWidth / 2) + " viewHeight:" + (viewHeight / 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenterCircle(canvas, radius);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//当view大小发生改变的时候记录下新的大小
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        radius = Math.min(getWidth(), getHeight()) / 2;//求圆的半径，通过宽和高的小的一个值确定
        Log.i(TAG, "onSizeChanged: " + getWidth() + " " + getHeight());
    }
}
