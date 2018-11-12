package xxhui.space.floatingcompass.mvp.Imp;

import xxhui.space.floatingcompass.mvp.interfaces.CompassFunction;

/**
 * Created by Hitvz on 2018/11/12.
 */

public class CompassFunctionImpl implements CompassFunction {

    private CompassMainPresenter presenter;

    public CompassFunctionImpl(CompassMainPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 通过CompassUtil的ImpSensorEventListener將变化之值通知更新
     * @param resultValues 返回compass的旋转角
     */
    @Override
    public void updateToView(double resultValues) {
        presenter.updateToView(resultValues);
    }
}
