package xxhui.space.floatingcompass.mvp.interfaces;

/**
 * Created by hui on 2017/3/22.
 */

public interface ViewInterface {

    //处理compass度数
    void compassDegree(double resultValues);
    //结束activity
    void finishActivity();
    //切换可不可以调节大小
    void canUpdateSize();

}
