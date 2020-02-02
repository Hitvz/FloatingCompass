package xxhui.space.floatingcompass.service;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import xxhui.space.floatingcompass.mvp.interfaces.CompassFunction;
import xxhui.space.floatingcompass.util.CompassUtil;
import xxhui.space.floatingcompass.view.CloseCompassView;
import xxhui.space.floatingcompass.view.CompassView;

import static android.content.ContentValues.TAG;

/**
 * Created by hui on 2017/1/18.
 */

public class CompassService extends Service implements CompassFunction {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams closeCircleParams;
    private float preR;
    private CompassView compassView;
    private CompassUtil compassUtil;
    private CloseCompassView closeCompassView;

    private int width;
    private int height;
    private int defaultSize = 300;
    private int leftTopX;
    private int leftTopY;
    private int closeCircleRadius;
    private boolean isWillCloseCircle;
    private int closeCircleWidth;
    private int statusBarHeight;//状态栏高度

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        width = intent.getIntExtra("width", -1);
        height = intent.getIntExtra("height", -1);
        leftTopX = intent.getIntExtra("leftTopX", -1);
        leftTopY = intent.getIntExtra("leftTopY", -1);
        closeCircleRadius = intent.getIntExtra("closeCircleRadius", -1);
        isWillCloseCircle = intent.getBooleanExtra("isWillCloseCircle", false);
        closeCircleWidth = intent.getIntExtra("closeCircleWidth", -1);
        statusBarHeight = intent.getIntExtra("statusBarHeight", -1);
        if (width == -1 || height == -1) {
            width = defaultSize;
            height = width;
        }
        windowManager = ((WindowManager) getSystemService(WINDOW_SERVICE));
        view();
        return super.onStartCommand(intent, flags, startId);
    }

    private void view() {
        buildCompass();
        buildCloseCompass();
    }

    public void buildCloseCompass() {
        closeCircleParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                PixelFormat.TRANSLUCENT);
        closeCircleParams.width = closeCircleWidth;
        closeCircleParams.height = closeCircleWidth;
        closeCircleParams.type = WindowManager.LayoutParams.TYPE_TOAST;//这个是重点,error在锁屏之下，状态栏之下，比较友好的展示。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0新特性
            closeCircleParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        closeCircleParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        closeCircleParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//要设置这个，其他应用才能正常使用物理菜单和返回键
        closeCircleParams.gravity = Gravity.LEFT | Gravity.TOP;
        closeCircleParams.x = leftTopX;
        closeCircleParams.y = leftTopY;
        closeCompassView = new CloseCompassView(getApplicationContext());
        windowManager.addView(closeCompassView, closeCircleParams);
        closeCompassView.setVisibility(View.GONE);
    }

    public void buildCompass() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                PixelFormat.TRANSLUCENT);
        params.width = width;
        params.height = height;
        // params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//这个是重点,error在锁屏之上，状态栏之上
        params.type = WindowManager.LayoutParams.TYPE_TOAST;//这个是重点,error在锁屏之下，状态栏之下，比较友好的展示。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0新特性
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//要设置这个，其他应用才能正常使用物理菜单和返回键
        params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;// 透明状态栏
        params.gravity = Gravity.CENTER;
        compassUtil = new CompassUtil(getApplicationContext(), this);
        compassUtil.registerListener();
        compassView = new CompassView(getApplicationContext());
        compassView.setParams(params);
        windowManager.addView(compassView, params);
        params.gravity = Gravity.LEFT | Gravity.TOP;//等窗口设置为居中显示后，设置为左上角为重心，不然其他重心都会出现偏移
        if (isWillCloseCircle) {
            compassView.setCompassFunction(this);
            compassView.setLeftTopX(leftTopX + closeCircleWidth / 2 - closeCircleRadius);
            compassView.setLeftTopY(leftTopY + statusBarHeight + closeCircleWidth / 2 - closeCircleRadius);
            compassView.setCloseCircleRadius(closeCircleRadius);
            compassView.setWillCloseCircle(isWillCloseCircle);
        }
    }

    @Override
    public void updateToView(double resultValues) {//这个旋转的属性动画和CompassView里面的rotate一个都不能少，在悬浮指南针的里面，普通在viewgroup中并不用这两个条件
        float temp = (float) -resultValues;
        if (Math.abs(preR - temp) > 0.8) {//提升程序稳定性，不会出现抖动
            ObjectAnimator.ofFloat(compassView, "rotation", preR, temp).setDuration(50).start();
        }
        preR = temp;
    }

    @Override
    public void closeCompass(int action) {
        if (closeCompassView == null) {
            return;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                closeCompassView.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_UP:
                closeCompassView.setVisibility(View.GONE);
                if (compassView.isCloseLocation()) {
                    //todo closeCompass
                    compassView.setVisibility(View.GONE);
                    Intent closeCompassIntent = new Intent();
                    closeCompassIntent.setAction("closeCompass");
                    sendBroadcast(closeCompassIntent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (closeCompassView != null) {
            closeCompassView.setVisibility(View.GONE);
        }
        compassUtil.unregisterListener();
        compassView.setVisibility(View.GONE);//可见性设置为看不见，不然会残留自定义View
        super.onDestroy();
    }


}

