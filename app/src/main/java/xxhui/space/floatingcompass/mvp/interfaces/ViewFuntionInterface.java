package xxhui.space.floatingcompass.mvp.interfaces;

import android.view.MotionEvent;

/**
 * Created by hui on 2017/3/25.
 */

public interface ViewFuntionInterface {
    //双击返回退出应用
    void onBackPressed();
    //
    void onThreeClick(MotionEvent event);
}
