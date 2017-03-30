package xxhui.space.floatingcompass.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by hui on 2017/2/1.
 * 仅仅是控制震动，需要权限
 * <uses-permission android:name="android.permission.VIBRATE"/>
 */

public class VibratorUtil {

    /**
     * 设置震动为milliseconds的时长，当milliseconds为0时设置默认值为100毫秒
     * @param context
     * @param milliseconds
     */
    public static void startVibrator(Context context , long milliseconds){
        if(milliseconds==0l){
            milliseconds = 100;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(milliseconds);
        }

    }
}
