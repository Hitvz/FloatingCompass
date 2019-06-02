package xxhui.space.floatingcompass.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import xxhui.space.floatingcompass.R;
import xxhui.space.floatingcompass.broadcast.FloatingResetReceiver;

public class NotificationUtil {

    public static void doNotify(Context context) {
        context = context.getApplicationContext();
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher_web);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_web);
        mBuilder.setContentTitle(context.getResources().getString(R.string.notify_title_tip_function_on));
        mBuilder.setContentText(context.getResources().getString(R.string.notify_text_tip_function_on));
        mBuilder.setLargeIcon(btm);
        mBuilder.setAutoCancel(true);//自己维护通知的消失
        //构建一个Intent
        Intent resultIntent =new Intent (context,FloatingResetReceiver.class);
        //如果构建的PendingIntent已经存在，则替换它
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, new Random().nextInt(100), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 设置通知主题的意图
        mBuilder.setContentIntent(resultPendingIntent);
        //获取通知管理器对象
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;//通知栏点击“清除”按钮时，该通知将不会被清除
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //ChannelId为"1",ChannelName为"Channel1"
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager.notify(4551, notification);
    }

    public static void undoNotify(Context context) {
        ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
