package xxhui.space.floatingcompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by hui on 2017/3/25.
 */

public class SimpleCompassConstraintLayout extends ConstraintLayout {
    private static final String TAG = "SimpleCompassCL" ;
    private CompassView compassView;

    public SimpleCompassConstraintLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public SimpleCompassConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int widgetsCount = this.getChildCount();
        for(int i = 0; i < widgetsCount; ++i) {
            View child = this.getChildAt(i);
           if(child instanceof CompassView){
               compassView = (CompassView) child;
           }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(compassView!=null){
            if(!compassView.isSizeChange()){
                return;
            }
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.argb(255,0,250,154));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawRect(compassView.getL(),compassView.getT(),compassView.getR(),compassView.getB(),paint);
        }else {
            Log.i(TAG, "onDraw: null");
        }
        super.onDraw(canvas);
    }


}
