package xxhui.space.floatingcompass.mvp.interfaces;

/**
 * Created by hui on 2017/1/16.
 */

public interface CompassFunction {
    /**
     * 在Compassutil中返回数据
     * @param resultValues 返回compass的旋转角
     */
    void updateToView(double resultValues);
}
