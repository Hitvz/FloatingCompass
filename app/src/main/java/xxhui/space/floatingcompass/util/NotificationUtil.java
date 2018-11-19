package xxhui.space.floatingcompass.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import xxhui.space.floatingcompass.MainActivity;
import xxhui.space.floatingcompass.R;

public class NotificationUtil {

    public static void doNotify(Context context){
        context = context.getApplicationContext();
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher_web);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher_web)
                .setContentTitle("5 new message")
                .setContentText("twain@android.com");
        mBuilder.setTicker("New message");//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(12);
        mBuilder.setLargeIcon(btm);
        //mBuilder.setAutoCancel(true);//自己维护通知的消失
        Notification notification =mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;//通知栏点击“清除”按钮时，该通知将不会被清除

        //构建一个Intent
        Intent resultIntent = new Intent(context,MainActivity.class);
        //封装一个Intent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);//如果构建的PendingIntent已经存在，则替换它
        // 设置通知主题的意图
        mBuilder.setContentIntent(resultPendingIntent);
        //获取通知管理器对象
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notification );
    }

    public static void undoNotify(Context context){
        ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
