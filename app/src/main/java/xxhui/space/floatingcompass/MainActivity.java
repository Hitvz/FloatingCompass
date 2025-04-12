package xxhui.space.floatingcompass;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import xxhui.space.floatingcompass.broadcast.CloseCompassReceiver;
import xxhui.space.floatingcompass.broadcast.FloatingResetReceiver;
import xxhui.space.floatingcompass.module.CompassPreferences;
import xxhui.space.floatingcompass.module.ScreenSize;
import xxhui.space.floatingcompass.interfaces.CompassDoubleListener;
import xxhui.space.floatingcompass.interfaces.CompassSizeChangeListener;
import xxhui.space.floatingcompass.mvp.Imp.CompassMainPresenter;
import xxhui.space.floatingcompass.mvp.abstracts.MVPCompatActivity;
import xxhui.space.floatingcompass.mvp.interfaces.MainViewEvent;
import xxhui.space.floatingcompass.service.CompassService;
import xxhui.space.floatingcompass.util.NotificationUtil;
import xxhui.space.floatingcompass.util.PermissionUtil;
import xxhui.space.floatingcompass.util.PhoneMSGUtil;
import xxhui.space.floatingcompass.util.VibratorUtil;
import xxhui.space.floatingcompass.view.CloseCompassView;
import xxhui.space.floatingcompass.view.CompassView;
import xxhui.space.floatingcompass.view.SimpleCompassConstraintLayout;

public class MainActivity extends MVPCompatActivity<MainViewEvent, CompassMainPresenter> implements MainViewEvent, CompassSizeChangeListener, CompassDoubleListener {

    private static final String TAG = "FcMainActivity";
    private CompassView compassView;
    private CloseCompassView closeCompassView;
    private SimpleCompassConstraintLayout viewGroup;
    private Toolbar toolbar;
    MenuItem notify;//获得隐藏的通知菜单
    private int statusBarHeight;//状态栏高度

    private ScreenSize screenSize;
    //旋转compassView的辅助度数：记录前一次的度数
    private float preR;

    private BroadcastReceiver floatingResetReceiver;

    private LinearLayout settingArea;

    private SwitchButton floatingCloseCompassSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        screenSize = PhoneMSGUtil.getScreenSize(this);
        getMaxSize();
        handlePreferences();
        //注册关闭compass的广播
        floatingResetReceiver = new CloseCompassReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("closeCompass");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(floatingResetReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(floatingResetReceiver, filter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCloseCompassViewSize();
    }

    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compassView = findViewById(R.id.compass);
        compassView.setCompassSizeChangeListener(this);
        compassView.setCompassDoubleListener(this);
        closeCompassView = findViewById(R.id.closeCompass);
        viewGroup = findViewById(R.id.simple_viewgroup);
        settingArea = findViewById(R.id.setting_area);
        settingArea.setVisibility(View.GONE);
        floatingCloseCompassSetting = findViewById(R.id.floating_close_compass_setting);
        floatingCloseCompassSetting.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                closeCompassView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                floatingCloseAble = isChecked;
            }
        });
    }

    public void handleIntent(Intent intent) {
        if (intent == null) {
            intent = getIntent();
        }
        if (intent.getBooleanExtra("floatingReset", false)) {
            switchFloat();
            NotificationUtil.undoNotify(getApplicationContext());
        }
    }

    public void handlePreferences() {
        new PreferencesTask().execute();
    }

    @Override
    protected CompassMainPresenter createPresenter() {
        //创建Presenter
        return new CompassMainPresenter(this);
    }

    //加载菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compass_menu, menu);
        notify = menu.findItem(R.id.notify);
        return super.onCreateOptionsMenu(menu);
    }

    //逻辑框架
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                if (!isFloat) {
                    NotificationUtil.undoNotify(getApplicationContext());
                } else {
                    NotificationUtil.doNotify(getApplicationContext());
                }
                break;
            case R.id.notify:
                PermissionUtil.askForNotificationPermission(getApplicationContext());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 展示提示的dialog
     */
    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(R.layout.tip_layout);
        dialog.show();
    }

    private boolean isFloat = false;

    private void switchFloat() {
        if (!PermissionUtil.getAppOps(getApplicationContext())) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.tip_floating_permission_need_on), Toast.LENGTH_SHORT).show();
            PermissionUtil.askForPermission(MainActivity.this);
        }
        if (compassView.getVisibility() != View.VISIBLE) {
            Log.d(TAG, "switchFloat: 开始停止服务");
            stopService(new Intent(MainActivity.this, CompassService.class));
            compassView.setVisibility(View.VISIBLE);
            closeCompassView.setVisibility(floatingCloseAble ? View.VISIBLE : View.GONE);
            isFloat = false;
        } else {
            if (compassView.isSizeChange()) {
                this.canUpdateSize();
            }
            statusBarHeight = PhoneMSGUtil.getStatusBarHeight(MainActivity.this, statusBarHeight);
            Intent intent = new Intent(MainActivity.this, CompassService.class);
            intent.putExtra("width", compassView.getLayoutParams().width);
            intent.putExtra("height", compassView.getLayoutParams().height);
            intent.putExtra("leftTopX", (int) closeCompassView.getX());
            intent.putExtra("leftTopY", (int) closeCompassView.getY());
            intent.putExtra("closeCircleRadius", closeCompassView.getLayoutParams().width * 9 / 10 / 2);
            intent.putExtra("closeCircleWidth", closeCompassView.getLayoutParams().width);
            intent.putExtra("isWillCloseCircle", floatingCloseAble);
            intent.putExtra("statusBarHeight", statusBarHeight);
            Log.d(TAG, "switchFloat: 开始启动服务");
            startService(intent);
            VibratorUtil.startVibrator(MainActivity.this, 100);
            compassView.setVisibility(View.GONE);
            closeCompassView.setVisibility(View.GONE);
            isFloat = true;
        }
    }

    private int bgStatus = 0;
    private int preBgStatus = -1;
    private boolean floatingCloseAble = false;//是否启用悬浮关闭区域

    private void switchBackground() {//切换背景
        if (preBgStatus == -1) {
            switchBackground(1);
            bgStatus = 1;
            preBgStatus = 0;
            return;
        } else if (bgStatus == 1) {
            switchBackground(0);
            bgStatus = 0;
        } else if (bgStatus == 0) {
            switchBackground(1);
            bgStatus = 1;
        }
    }

    private void switchBackground(int bgStatus) {//切换背景
        ViewGroup viewGroup;
        switch (bgStatus) {
            case 0:
                viewGroup = findViewById(R.id.simple_viewgroup);
                viewGroup.setBackgroundColor(Color.WHITE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 1:
                viewGroup = findViewById(R.id.simple_viewgroup);
                viewGroup.setBackgroundColor(Color.BLACK);
                toolbar.setBackgroundColor(Color.BLACK);
                break;
            default:
                break;
        }
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
    private int maxSize = 0;

    public void getMaxSize() {
        if (maxSize > 0) {
            return;
        }
        maxSize = screenSize.getScreenWidth() > screenSize.getScreenHeight() ? screenSize.getScreenHeight() : screenSize.getScreenWidth();
        setCloseCompassViewSize();
    }

    @Override
    public void sizeChange(float size) {
        getMaxSize();
        if (compassView.isSizeChange()) {
            ViewGroup.LayoutParams params = compassView.getLayoutParams();
            float bound = params.width < params.height ? params.height + size : params.width + size;//取大的值
            if (bound < minSize) {
                params.width = minSize;
                params.height = minSize;
            } else if (bound > maxSize) {
                params.width = maxSize;
                params.height = maxSize;
            } else {
                params.width += size;
                params.height += size;
            }
            compassView.setLayoutParams(params);
        }
    }

    public void setCloseCompassViewSize() {
        ViewGroup.LayoutParams params = closeCompassView.getLayoutParams();
        int closeSize = maxSize / 6;
        params.width = closeSize;
        params.height = closeSize;
        closeCompassView.setLayoutParams(params);
    }

    public void canUpdateSize() {
        boolean canUpdate = compassView.switchSizeChange();//更改指南针的可更新大小的标志位
        if (notify != null) {
            notify.setVisible(canUpdate);//更改大小时，可以设置通知栏权限
        }
        settingArea.setVisibility(canUpdate ? View.VISIBLE : View.GONE);
        if (!canUpdate) {//在结束调节大小的时候记录大小
            CompassPreferences preferences = new CompassPreferences();
            preferences.setRadius((compassView.getR() - compassView.getL()) / 2);
            preferences.setBgStatus(bgStatus);
            preferences.setFloatingCloseAble(floatingCloseAble);
            mPresenter.writeCompassPreferences(preferences);
        }
        viewGroup.postInvalidate();
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void finishActivity() {
        resourceClear();
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        resourceClear();
        if (floatingResetReceiver != null) {
            unregisterReceiver(floatingResetReceiver);
        }
        super.onDestroy();
    }

    public void resourceClear() {
        NotificationUtil.undoNotify(getApplicationContext());
    }

    @Override
    public void onThreeClick(MotionEvent event) {
        mPresenter.onThreeClick(event);
    }

    /**
     * 配置文件读取
     */
    class PreferencesTask extends AsyncTask<Void, Integer, CompassPreferences> {
        @Override
        protected CompassPreferences doInBackground(Void... voids) {
            CompassPreferences preferences = mPresenter.readCompassPreferences();
            int radius = (int) preferences.getRadius();
            int minSize = screenSize.getScreenHeight() > screenSize.getScreenWidth() ? screenSize.getScreenWidth() / 8 : screenSize.getScreenHeight() / 8;
            if (radius < minSize) {
                preferences.setRadius(minSize);
            }
            return preferences;
        }

        @Override
        protected void onPostExecute(CompassPreferences preferences) {
            Float radius = preferences.getRadius();
            ViewGroup.LayoutParams params = compassView.getLayoutParams();
            params.width = radius.intValue() * 2;
            params.height = radius.intValue() * 2;
            compassView.setLayoutParams(params);
            //compassView.invalidate();
            if (preferences.getBgStatus() != 0) {
                switchBackground(preferences.getBgStatus());
                bgStatus = preferences.getBgStatus();
                preBgStatus = 0;
            }
            floatingCloseAble = preferences.isFloatingCloseAble();
            closeCompassView.setVisibility(floatingCloseAble ? View.VISIBLE : View.GONE);
            floatingCloseCompassSetting.setChecked(floatingCloseAble);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 4551) {//通知栏权限开启提示
            Toast.makeText(MainActivity.this, getResources().getString(R.string.tip_floating_permission_on), Toast.LENGTH_SHORT).show();
            switchFloat();
        }
    }
}
