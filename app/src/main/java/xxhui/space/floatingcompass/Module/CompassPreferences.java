package xxhui.space.floatingcompass.Module;

/**
 * Created by Hitvz on 2018/11/12.
 */

/**
 * 应用设置
 */
public class CompassPreferences {

    public static final String KEY_RADIUS = "radius";

    private float radius = 400 ;//指南针半径

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
