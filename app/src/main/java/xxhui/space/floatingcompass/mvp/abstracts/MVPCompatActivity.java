package xxhui.space.floatingcompass.mvp.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hui on 2017/3/6.
 */

/**
 * 通过继承AppCompatActivity来将Presenter对象初始化和资源回收
 * @param <V> view的接口，用于更新view
 * @param <T> BasePresenter的子类，用于处理具体逻辑，然后调用view的方法更新view
 */
public abstract class MVPCompatActivity<V , T extends BasePresenter<V>> extends AppCompatActivity {
    protected T mPresenter;//Presenter对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract T createPresenter();
}
