package xxhui.space.floatingcompass.mvp.interfaces;

import android.view.MotionEvent;

import xxhui.space.floatingcompass.Module.CompassPreferences;

/**
 * Created by hui on 2017/3/25.
 */

public interface MainPresenterFunction {
    //双击返回退出应用
    void onBackPressed();
    //双击事件处理
    void onThreeClick(MotionEvent event);

    CompassPreferences readCompassPreferences();

    void writeCompassPreferences(CompassPreferences preferences);
}
