package xxhui.space.floatingcompass.mvp.Imp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import xxhui.space.floatingcompass.module.CompassPreferences;
import xxhui.space.floatingcompass.R;
import xxhui.space.floatingcompass.mvp.interfaces.CompassFunction;
import xxhui.space.floatingcompass.mvp.abstracts.BasePresenter;
import xxhui.space.floatingcompass.mvp.interfaces.MainPresenterFunction;
import xxhui.space.floatingcompass.mvp.interfaces.MainViewEvent;
import xxhui.space.floatingcompass.util.CompassStorageUtil;
import xxhui.space.floatingcompass.util.CompassUtil;

import static android.content.ContentValues.TAG;

/**
 * Created by hui on 2017/3/22.
 */

/**
 * 指南针的操作，
 */
public class CompassMainPresenter extends BasePresenter<MainViewEvent> implements MainPresenterFunction {

    private CompassUtil compassUtil;

    private CompassFunction compassFunction;

    private CompassStorageUtil storageUtil;

    public CompassMainPresenter(MainViewEvent view) {
        if (!isViewAttached()) {
            attachView(view);
        }
        compassFunction = new CompassFunctionImpl(this);
        compassUtil = new CompassUtil((AppCompatActivity) getView(), compassFunction);
        compassUtil.registerListener();//注册传感器
        storageUtil = new CompassStorageUtil();

    }

    /**
     * 通过CompassUtil的ImpSensorEventListener將变化之值通知更新
     * @param resultValues 返回compass的旋转角
     */
    public void updateToView(double resultValues) {
        getView().handleCompassDegree(resultValues);
    }


    @Override
    public void detachView() {
        super.detachView();
        compassUtil.unregisterListener();//注销传感器
    }

    private boolean mBackKeyPressed = false;

    public void onBackPressed() {
        if (!mBackKeyPressed) {
            Toast.makeText((AppCompatActivity) getView(),((AppCompatActivity) getView()).getResources().getString(R.string.exit_app) , Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 1000);
        } else {
            getView().finishActivity();
            System.exit(0);
        }
    }

    private int count = 0;
    private long firstClick;
    private long secondClick;

    /**
     * 调用写在onToucheEvent中
     *
     * @param event
     */

    @Override
    public void onThreeClick(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (count == 1) {
                firstClick = System.currentTimeMillis();
            } else if (count == 2) {

            } else if (count == 3) {
                count = 0;
                secondClick = System.currentTimeMillis();
                if (secondClick - firstClick < 1000) {
                    //三击事件
                    getView().canUpdateSize();
                    Log.i(TAG, "doubleClick: ");
                } else {
                    firstClick = System.currentTimeMillis();
                    count = 1;
                }
            }
        }
    }

    @Override
    public CompassPreferences readCompassPreferences() {
        return storageUtil.getPreferences((AppCompatActivity) getView());
    }

    @Override
    public void writeCompassPreferences(CompassPreferences preferences) {
        storageUtil.setPreferences((AppCompatActivity) getView(),preferences);
    }


}
