package xxhui.space.floatingcompass.mvp.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hui on 2017/3/6.
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
