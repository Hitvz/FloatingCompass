package xxhui.space.floatingcompass.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import xxhui.space.floatingcompass.interfaces.CompassUpdateToView;


/**
 * Created by hui on 2017/1/16.
 * 1、获取SensorManager，对其设置监听，监听为自己实现的ImpSensorEventListener
 * 2、构造注入compassUpdateToView，再设置注入ImpSensorEventListener，传递了两次，最后到达ImpSensorEventListener
 * 3、可以添加功能声明，防止没有传感器的手机不能打开程序，可以添加以下功能声明：
 * <uses-feature android:name="android.hardware.sensor.accelerometer" android:required="false"></uses-feature>
 * <uses-feature android:name="android.hardware.sensor.compass" android:required="false"></uses-feature>
 */

public class CompassUtil {
    private Context context;//得到上下文，以获得SensorManager
    private SensorManager manager;
    private Sensor aSensor;//加速度传感器
    private Sensor mSensor;//磁场传感器
    private ImpSensorEventListener listener;

    public CompassUtil(Context context, CompassUpdateToView compassUpdateToView) {
        this.context = context.getApplicationContext();
        manager =  this.getSensorManager();
        listener = new ImpSensorEventListener(compassUpdateToView);
    }

    private SensorManager getSensorManager(){
        return (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerListener(){
        aSensor=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//得到加速度传感器
        mSensor=manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//得到磁场传感器
        manager.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener(){
        manager.unregisterListener(listener);//注销所有注册的传感器
    }


}
