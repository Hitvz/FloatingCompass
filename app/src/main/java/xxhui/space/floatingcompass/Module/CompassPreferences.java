package xxhui.space.floatingcompass.Module;

/**
 * Created by Hitvz on 2018/11/12.
 */

/**
 * 应用设置
 */
public class CompassPreferences {

    public static final String KEY_RADIUS = "radius";

    public static final String KEY_BG = "bgStatus";

    private float radius = 300 ;//指南针半径

    private int bgStatus = 0 ;//背景顔色


    public int getBgStatus() {
        return bgStatus;
    }

    public void setBgStatus(int bgStatus) {
        this.bgStatus = bgStatus;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "CompassPreferences{" +
                "radius=" + radius +
                ", bgStatus=" + bgStatus +
                '}';
    }
}
