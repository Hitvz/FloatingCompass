package xxhui.space.floatingcompass.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import xxhui.space.floatingcompass.mvp.interfaces.MainViewEvent;

public class CloseCompassReceiver extends BroadcastReceiver {

    private static final String TAG = "CloseCompassReceiver";
    private MainViewEvent mainViewEvent;

    public CloseCompassReceiver(MainViewEvent mainViewEvent) {
        this.mainViewEvent = mainViewEvent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        mainViewEvent.finishActivity();
    }
}
