package xxhui.space.floatingcompass.Module;

/**
 * Created by hui on 2017/2/1.
 */

public class ScreenSize {
    private int screenWidth;
    private int screenHeight;

    public ScreenSize() {
    }

    public ScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    @Override
    public String toString() {
        return "ScreenSize{" +
                "screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                '}';
    }
}
