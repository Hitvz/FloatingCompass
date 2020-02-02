package xxhui.space.floatingcompass.util;

/**
 * Created by Hitvz on 2018/11/12.
 */

import android.content.Context;
import android.content.SharedPreferences;

import xxhui.space.floatingcompass.module.CompassPreferences;

/**
 * 存储应用配置
 */
public class CompassStorageUtil {

    private Context context;

    private CompassPreferences preferences = new CompassPreferences();

    public CompassPreferences getPreferences(Context context){
        this.context = context.getApplicationContext();
        SharedPreferences sharedPreferences = getSharedPreferences();
        preferences.setRadius(sharedPreferences.getFloat(CompassPreferences.KEY_RADIUS,300));
        preferences.setBgStatus(sharedPreferences.getInt(CompassPreferences.KEY_BG,0));
        preferences.setFloatingCloseAble(sharedPreferences.getBoolean(CompassPreferences.KEY_FLOATING_CLOSE_ABLE,false));
        return preferences;
    }

    public void setPreferences(Context context , CompassPreferences preferences){
        if(preferences==null) return;
        boolean isPreferencesChange = false;
        this.context = context.getApplicationContext();
        SharedPreferences.Editor editor = getEditor();
        if(preferences.getRadius()!=400.0f){
            editor.putFloat(CompassPreferences.KEY_RADIUS,preferences.getRadius());
            isPreferencesChange = true;
        }
        editor.putInt(CompassPreferences.KEY_BG,preferences.getBgStatus());
        editor.putBoolean(CompassPreferences.KEY_FLOATING_CLOSE_ABLE,preferences.isFloatingCloseAble());
        if(isPreferencesChange){
            editor.commit();
        }
    }

    private SharedPreferences getSharedPreferences(){
        return this.context.getSharedPreferences("CompassPreferences",Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor(){
        return getSharedPreferences().edit();
    }


}
