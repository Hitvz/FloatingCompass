package xxhui.space.floatingcompass.mvp.Imp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import xxhui.space.floatingcompass.R;
import xxhui.space.floatingcompass.interfaces.CompassUpdateToView;
import xxhui.space.floatingcompass.mvp.abstracts.BasePresenter;
import xxhui.space.floatingcompass.mvp.interfaces.ViewFuntionInterface;
import xxhui.space.floatingcompass.mvp.interfaces.ViewInterface;
import xxhui.space.floatingcompass.util.CompassUtil;

import static android.content.ContentValues.TAG;

/**
 * Created by hui on 2017/3/22.
 */

public class CompassPresenter extends BasePresenter<ViewInterface> implements CompassUpdateToView, ViewFuntionInterface {

    private CompassUtil compassUtil;

    public CompassPresenter(ViewInterface view) {
        if (!isViewAttached()) {
            attachView(view);
        }
        compassUtil = new CompassUtil((AppCompatActivity) getView(), this);
        compassUtil.registerListener();
    }

    @Override
    public void updateToView(double resultValues) {
        getView().compassDegree(resultValues);
    }


    @Override
    public void detachView() {
        super.detachView();
        compassUtil.unregisterListener();
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
            //System.exit(0);
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
                    //双击事件
                    getView().canUpdateSize();
                    Log.i(TAG, "doubleClick: ");
                } else {
                    firstClick = System.currentTimeMillis();
                    count = 1;
                }
            }
        }
    }

}
