package xxhui.space.floatingcompass.impl;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import xxhui.space.floatingcompass.mvp.interfaces.CompassFunction;


/**
 * Created by hui on 2017/1/16.
 * 用于监听传感器，用户获取数据，通过注入的CompassUpdateToView借口对象将数据返回
 */

public class ImpSensorEventListener implements SensorEventListener {
    private CompassFunction compassFunction;
    private float[] gravityValues;//重力向量值，通过SensorEvent得到；
    private float[] geomagneticValues;//磁场向量值，通过SensorEvent得到；
    private float[] R = new float[9];//或者R矩阵
    private float[] resultValues = new float[3];

    public ImpSensorEventListener(CompassFunction compassFunction) {
        this.compassFunction = compassFunction;
    }

    /**
     * 传感器值改变时调用
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagneticValues = event.values;
        }
        if (gravityValues == null || geomagneticValues == null) {
            return;
        }
        //调用getRotaionMatrix获得变换矩阵R[]
        SensorManager.getRotationMatrix(R, null, gravityValues, geomagneticValues);
        SensorManager.getOrientation(R, resultValues);
        //经过SensorManager.getOrientation(R, values);得到的values值为弧度
        //转换为角度
        double values = Math.toDegrees(resultValues[0]);
        compassFunction.updateToView(values);
    }

    /**
     * 当一个传感器的准确性已经改变的时候调用。
     * note:精度小于3个都不可信
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Log.i("Compass", "sensorType" + sensor.getStringType());
        }
        if (accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            Log.i("Compass", " 不需要校验" + " sensorType" + sensor.getType());
        } else {
            Log.i("Compass", "需要校准 accuracy=" + accuracy + " sensorType" + sensor.getType());
        }
    }
}
