package xxhui.space.floatingcompass.mvp.abstracts;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by hui on 2017/3/6.
 */

/**
 * 逻辑操作的抽象
 * @param <T> view的对象，用于操作view
 */
public abstract class BasePresenter<T> {
    protected Reference<T> mViewRef; //View接口类型的弱引用

    public void attachView(T view){
        mViewRef = new WeakReference<T>(view);//建立关联
    }

    protected T getView(){
        return mViewRef.get();
    }

    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView(){
        if(mViewRef!=null){
            mViewRef.clear();
            mViewRef = null;
        }
    }
}
