package xxhui.space.floatingcompass;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import xxhui.space.floatingcompass.Module.ScreenSize;
import xxhui.space.floatingcompass.interfaces.CompassDoubleListener;
import xxhui.space.floatingcompass.interfaces.CompassSizeChangeListener;
import xxhui.space.floatingcompass.mvp.Imp.CompassMainPresenter;
import xxhui.space.floatingcompass.mvp.abstracts.MVPCompatActivity;
import xxhui.space.floatingcompass.mvp.interfaces.MainViewEvent;
import xxhui.space.floatingcompass.service.CompassService;
import xxhui.space.floatingcompass.util.PermissionUtil;
import xxhui.space.floatingcompass.util.PhoneMSGUtil;
import xxhui.space.floatingcompass.util.VibratorUtil;
import xxhui.space.floatingcompass.view.CompassView;
import xxhui.space.floatingcompass.view.SimpleCompassConstraintLayout;

public class MainActivity extends MVPCompatActivity<MainViewEvent, CompassMainPresenter> implements MainViewEvent, CompassSizeChangeListener, CompassDoubleListener {

    private static final String TAG = "MainActivity";
    private CompassView compassView;
    private SimpleCompassConstraintLayout viewgroup;
    private Toolbar toolbar;

    private ScreenSize screenSize;
    //旋转compassView的辅助度数：记录前一次的度数
    private float preR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        screenSize = PhoneMSGUtil.getScreenSize(this);
    }

    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compassView = findViewById(R.id.compass);
        compassView.setCompassSizeChangeListener(this);
        compassView.setCompassDoubleListener(this);
        viewgroup = findViewById(R.id.simple_viewgroup);
    }

    @Override
    protected CompassMainPresenter createPresenter() {
        //创建Presenter
        return new CompassMainPresenter(this);
    }

    //加载菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.compass_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //逻辑框架
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        int id = item.getItemId();
        switch (id) {
            case R.id.switch_bg:
                switchBackground();
                break;
            case R.id.reply:
                showDialog();
                break;
            case R.id.switch_float:
                switchFloat();
                break;
            default:
                showTip();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 展示提示的dialog
     */
    private void showDialog(){
        AlertDialog.Builder dialog =new AlertDialog.Builder(this);
        dialog.setView(R.layout.tip_layout);
        dialog.show();
    }

    private boolean isFloat = false;

    private void switchFloat() {
        if (!PermissionUtil.getAppOps(getApplicationContext())) {
            Toast.makeText(MainActivity.this, "没有悬浮窗权限，需要开启权限", Toast.LENGTH_SHORT).show();
            PermissionUtil.askForPermission(MainActivity.this);
        }
        if (compassView.getVisibility() != View.VISIBLE) {
            stopService(new Intent(MainActivity.this, CompassService.class));
            compassView.setVisibility(View.VISIBLE);
            isFloat = false;
        } else {
            if (compassView.isSizeChange()) {
                this.canUpdateSize();
            }
            Intent intent = new Intent(MainActivity.this, CompassService.class);
            intent.putExtra("width",compassView.getLayoutParams().width);
            intent.putExtra("height",compassView.getLayoutParams().height);
            startService(intent);
            VibratorUtil.startVibrator(MainActivity.this, 100);
            compassView.setVisibility(View.GONE);
            isFloat = true;
        }
    }

    private boolean isBlack = false;

    private void switchBackground() {//切换背景
        if (!isBlack) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.simple_viewgroup);
            viewGroup.setBackgroundColor(Color.BLACK);
            toolbar.setBackgroundColor(Color.BLACK);
        } else {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.simple_viewgroup);
            viewGroup.setBackgroundColor(Color.WHITE);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        isBlack = !isBlack;
    }

    @Override
    public void handleCompassDegree(double resultValues) {
        float temp = (float) -resultValues;
        if (Math.abs(preR - temp) > 0.8) {//提升程序稳定性，不会出现抖动
            ObjectAnimator.ofFloat(compassView, "rotation", preR, temp).setDuration(250).start();
        }
        preR = temp;
    }

    private int minSize = 300;

    @Override
    public void sizeChange(float size) {
        Log.i(TAG, "sizeChange: " + size);
        if (compassView.isSizeChange()) {
            ViewGroup.LayoutParams params = compassView.getLayoutParams();
            if (params.width + size < minSize||params.height + size < minSize) {
                params.width = minSize;
                params.height = minSize;
            } else {
                params.width += size;
                params.height += size;
            }
            int maxSize = screenSize.getScreenWidth()>screenSize.getScreenHeight()?screenSize.getScreenHeight():screenSize.getScreenWidth();
            if (params.width + size > maxSize||params.height + size > maxSize) {
                params.width = maxSize;
                params.height = maxSize;
            } else {
                params.width += size;
                params.height += size;
            }
            compassView.setLayoutParams(params);
        }
        Log.i(TAG, "sizeChange: " + size);
    }

    public void canUpdateSize() {
        compassView.switchSizeChange();
        viewgroup.postInvalidate();
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void finishActivity() {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onThreeClick(MotionEvent event) {
        mPresenter.onThreeClick(event);
    }

    private int count = 0;
    private long firstClick;
    private long secondClick;

    public void showTip() {
        count++;
        if (count == 1) {
            firstClick = System.currentTimeMillis();
        } else if (count == 2) {

        } else if (count == 3) {
            count = 0;
            secondClick = System.currentTimeMillis();
            if (secondClick - firstClick < 1000) {
                //双击事件
                Toast.makeText(this,"menu",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "doubleClick: ");
            } else {
                firstClick = System.currentTimeMillis();
                count = 1;
            }
        }
        Log.i(TAG, "showTip: ");
    }
}
