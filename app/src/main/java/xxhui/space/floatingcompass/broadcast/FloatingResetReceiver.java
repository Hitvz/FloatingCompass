package xxhui.space.floatingcompass.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xxhui.space.floatingcompass.MainActivity;

public class FloatingResetReceiver extends BroadcastReceiver {

    private static final String TAG = "FloatingResetReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent jumpIntent = new Intent(context, MainActivity.class);
        jumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        jumpIntent.putExtra("floatingReset",true);
        context.startActivity(jumpIntent);
    }

}
