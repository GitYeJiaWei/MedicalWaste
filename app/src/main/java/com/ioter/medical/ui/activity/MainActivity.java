package com.ioter.medical.ui.activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.common.ActivityCollecter;
import com.ioter.medical.common.download.LoadingService;
import com.ioter.medical.common.download.Utils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerRuleListComponent;
import com.ioter.medical.di.module.RuleListModule;
import com.ioter.medical.presenter.RuleListPresenter;
import com.ioter.medical.presenter.contract.RuleListContract;
import com.ioter.medical.ui.adapter.DrawerListAdapter;
import com.ioter.medical.ui.adapter.DrawerListContent;
import com.ioter.medical.ui.adapter.MyFragmentPagerAdapter;
import com.ioter.medical.ui.fragment.BaseFragment;
import com.ioter.medical.ui.fragment.CheckFragment;
import com.ioter.medical.ui.fragment.CountFragment;
import com.ioter.medical.ui.fragment.HomeFragment;
import com.ioter.medical.ui.fragment.SettingFragment;
import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import print.Print;

/**
 * 主页
 */
public class MainActivity extends BaseActivity<RuleListPresenter>
        implements RuleListContract.FeeRuleView, HomeFragment.CallBackValue, CountFragment.CallBackTag {
    private String[] mOptionTitles;
    private int[] mBitMaps;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mleftLin;
    private TextView mVersionCode;
    private View headerView;
    private View mNetWorkTips;
    private ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    //退出时的时间
    private long mExitTime;

    private HomeFragment myFragment1 = null;
    private CountFragment myFragment2 = null;
    private CheckFragment myFragment3 = null;
    private SettingFragment myFragment4 = null;
    private List<Fragment> fragments;
    private List<String> titleList;
    private List<Integer> picList;
    private TabLayout tablayout;
    private Toolbar toolbar;
    private TextView title;
    private boolean isLoading;
    private String path;

    public static RFIDWithUHF mReader; //RFID扫描

    public static Barcode2DWithSoft barcode2DWithSoft = null;//二维扫码
    private boolean isread = true;
    private boolean mIsEditStatus = false;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerRuleListComponent.builder().appComponent(appComponent).ruleListModule(new RuleListModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        mPresenter.feeRule();
        //注册广播
        startScreenBroadcastReceiver();

        initview();
        selectItem(0);
        String key1 = ACache.get(AppApplication.getApplication()).getAsString("key1");
        if (TextUtils.isEmpty(key1)) {
            key1 = "10";
        }

        new InitBarCodeTask().execute();
        initUHF();
        mReader.setPower(Integer.valueOf(key1));

        //消息轮询
        AppApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                while (isread) {
                    read();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private int cycleCount = 3;//循环3次初始化

    //初始化RFID扫描
    public void initUHF() {
        cycleCount = 3;
        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {
            ToastUtil.toast(ex.getMessage());
            return;
        }

        if (mReader != null) {
            AppApplication.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    if (!mReader.init()) {
                        //ToastUtil.toast("init uhf fail,reset ...");
                        if (cycleCount > 0) {
                            cycleCount--;
                            if (mReader != null) {
                                mReader.free();
                            }
                            initUHF();
                        }
                    } else {
                        ToastUtil.toast("初始化成功");
                    }
                }
            });
        }
    }

    public class InitBarCodeTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            if (barcode2DWithSoft == null) {
                barcode2DWithSoft = Barcode2DWithSoft.getInstance();
            }
            boolean reuslt = false;
            if (barcode2DWithSoft != null) {
                reuslt = barcode2DWithSoft.open(MainActivity.this);
            }
            return reuslt;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                barcode2DWithSoft.setParameter(324, 1);
                barcode2DWithSoft.setParameter(300, 0); // Snapshot Aiming
                barcode2DWithSoft.setParameter(361, 0); // Image Capture Illumination

                // interleaved 2 of 5
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);

            } else {
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    //网络检测
    @Override
    protected void handleNetWorkTips(boolean has) {
        if (has) {
            mNetWorkTips.setVisibility(View.GONE);
        } else {
            mNetWorkTips.setVisibility(View.VISIBLE);
        }
    }

    //界面布局，数据初始化
    private void initview() {
        mDrawerLayout = findViewById(R.id.drawer_layout);//整个布局
        mDrawerList = findViewById(R.id.left_drawer);    //抽屉的list
        mleftLin = findViewById(R.id.left_lin);          //抽屉layout
        mVersionCode = findViewById(R.id.tv_versionCode);//抽屉下方版本号


        //Toolbar导航栏和文字title
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        title = findViewById(R.id.title);
        //设置toolbar为Action对象
        toolbar.setNavigationIcon(R.mipmap.button_daohang);

        //点击左边返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
                if (fragment instanceof HomeFragment || fragment instanceof CheckFragment ||
                        fragment instanceof CountFragment || fragment instanceof SettingFragment) {
                    if (mDrawerLayout.isDrawerVisible(mleftLin)) {
                        mDrawerLayout.closeDrawer(mleftLin);
                    } else {
                        mDrawerLayout.openDrawer(mleftLin);
                    }
                }
            }
        });

        //设置移除图片  如果不设置会默认使用系统灰色的图标
        //toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_action));
        //填充menu
        toolbar.inflateMenu(R.menu.toolbar_menu);
        //设置点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, RemindActivity.class));
                mIsEditStatus = false;
                invalidateOptionsMenu(); //重新绘制menu
                return false;
            }
        });

        mIsEditStatus = false;
        invalidateOptionsMenu(); //重新绘制menu

        //为抽屉头部 R.layout.layout_header 指定一个父布局抽屉 mDrawerList
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header, mDrawerList, false);
        mDrawerList.addHeaderView(headerView);
        TextView mTxt_username = headerView.findViewById(R.id.txt_username);
        mTxt_username.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_list_item, DrawerListContent.ITEMS));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());//抽屉的item点击

        try {
            mVersionCode.setText("当前版本号：" + Utils.getVersionName(this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //网络检测
        mNetWorkTips = findViewById(R.id.network_view);
        mNetWorkTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });

        //4个fragment
        myFragment1 = new HomeFragment();
        myFragment2 = new CountFragment();
        myFragment3 = new CheckFragment();
        myFragment4 = new SettingFragment();
        fragments = new ArrayList<>();
        fragments.add(myFragment1);
        fragments.add(myFragment2);
        fragments.add(myFragment3);
        fragments.add(myFragment4);

        //配置底部图片和数据
        mOptionTitles = getResources().getStringArray(R.array.options_array);
        titleList = new ArrayList<>();
        titleList.add(mOptionTitles[0]);
        titleList.add(mOptionTitles[1]);
        titleList.add(mOptionTitles[2]);
        titleList.add(mOptionTitles[5]);
        mBitMaps = new int[]{R.drawable.map_first, R.drawable.map_second,
                R.drawable.map_third, R.drawable.map_forth};
        picList = new ArrayList<>();
        picList.add(mBitMaps[0]);
        picList.add(mBitMaps[1]);
        picList.add(mBitMaps[2]);
        picList.add(mBitMaps[3]);

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, titleList);

        vpager = findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
                if (fragment == myFragment2) {
                    title.setText(mOptionTitles[1]);
                } else if (fragment == myFragment3) {
                    title.setText(mOptionTitles[2]);
                } else if (fragment == myFragment4) {
                    title.setText(mOptionTitles[5]);
                } else {
                    title.setText(mOptionTitles[0]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    //整个生命周期只调用一次
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // 动态设置Action menu的状态
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsEditStatus) {
            menu.findItem(R.id.action_message).setVisible(true);
            menu.findItem(R.id.action_un_message).setVisible(false);
        } else {
            menu.findItem(R.id.action_message).setVisible(false);
            menu.findItem(R.id.action_un_message).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void feeRuleResult(BaseBean<FeeRule> baseBean1) {
        if (baseBean1 == null) {
            ToastUtil.toast("获取数据失败");
            finish();
        }
        if (baseBean1.getCode() == 0 && baseBean1.getData() != null) {
            ACache.get(AppApplication.getApplication()).put("feeRule", baseBean1);

            mAdapter.notifyDataSetChanged();
            //在tablayout中填入vpager
            tablayout = findViewById(R.id.tablayout);
            tablayout.setupWithViewPager(vpager);
            //获取当前tab数量
            int tabCount = tablayout.getTabCount();
            //遍历循环tab数量,加载自定义的布局
            for (int i = 0; i < tabCount; i++) {
                //获取每个tab
                TabLayout.Tab tab = tablayout.getTabAt(i);
                View view = View.inflate(this, R.layout.tab_view, null);
                final ImageView iv = view.findViewById(R.id.iv);
                TextView tv = view.findViewById(R.id.tv);
                tv.setText(titleList.get(i));
                iv.setBackgroundResource(picList.get(i));
                if (i == 0)
                    iv.setFocusable(true);
                //给tab设置view
                tab.setCustomView(view);
            }

            //tab得选中监听
            tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tab.getCustomView().findViewById(R.id.iv).setFocusable(true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.getCustomView().findViewById(R.id.iv).setFocusable(false);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            //获取版本更新
            if (baseBean1.getData().getAutoUpdateInfo() != null && baseBean1.getData().getAutoUpdateInfo().getVersion() != null) {
                getVersionInfoFromServer(baseBean1);
            }
        } else {
            ToastUtil.toast(baseBean1.getMessage());
            finish();
        }
    }

    private void read() {
        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Remind>> qqDataCall = apIservice.GetNewMessage();
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Remind>>() {

                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<Remind> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("读取失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0) {
                                       if (baseBean.getData() != null) {
                                           ToastUtil.toast("有新消息了!");
                                           initNotifcation(baseBean.getData().getTitle());
                                           mIsEditStatus = true;
                                           invalidateOptionsMenu(); //重新绘制menu
                                       }
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {

                               }//订阅
                           }
                );
    }

    /**
     * 顶部通知栏
     *
     * @param title
     */
    private void initNotifcation(String title) {
        Intent intent = new Intent(this, RemindActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(AppApplication.getApplication())
                .setContentTitle("医废消息提醒")
                .setContentText(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)//获取默认铃声
                //.setSound(Uri.fromFile(new File("android.resource://" + getPackageName() + "/" + R.raw.barcodebeep)))
                .build();

        manager.notify(1, notification);
    }

    //获取回调的数据
    @Override
    public void SendMessageValue(String strValue) {
        if (strValue.equals("MedicalCollectActivity")) {
            startActivity(new Intent(this, MedicalCollectActivity.class));
        } else if (strValue.equals("MedicalEnterActivity")) {
            startActivity(new Intent(this, MedicalEnterActivity.class));
        } else if (strValue.equals("SettingFragment")) {
            vpager.setCurrentItem(3);
        } else if (strValue.equals("CheckFragment")) {
            vpager.setCurrentItem(2);
        } else if (strValue.equals("EnterCheckActivity")) {
            startActivity(new Intent(this, EnterCheckActivity.class));
        } else if (strValue.equals("MedicalOutActivity")) {
            startActivity(new Intent(this, MedicalOutActivity.class));
        }
    }

    //获取条件查询的回调
    @Override
    public void sendTag() {
        vpager.setCurrentItem(2);
    }

    /**
     * The click listener for ListView in the navigation drawer
     * 点击左侧抽屉的item
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(mleftLin);
            if (!NetUtils.isConnected(MainActivity.this)) {
                ToastUtil.toast(R.string.error_network_unreachable);
                return;
            }
            if (position == 0)//headView click
            {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            } else {
                //用来跳转到其它fragment或者activity
                selectItem(position);
            }
        }
    }

    public void selectItem(int position) {
        // update the no_items content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = HomeFragment.newInstance();
                break;
            case 3:
                finish();
                return;
        }
        if (fragment == null) {
            return;
        }
        mDrawerList.setItemChecked(position, true);//高亮选中项
        title.setText(mOptionTitles[position]);
        mDrawerLayout.closeDrawer(mleftLin);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ((BaseFragment) fragment).myOnKeyDwon();
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ((BaseFragment) fragment).myOnKeyUp();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void showBarCode(String barCodeText) {
        Fragment fragment = (BaseFragment) mAdapter.instantiateItem(vpager, vpager.getCurrentItem());
        if (fragment != null) {
            ((BaseFragment) fragment).showBarCode(barCodeText);
        }
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isread) {
            isread = false;
        }
        if (mReader != null) {
            mReader.free();
        }
        if (barcode2DWithSoft != null) {
            barcode2DWithSoft.stopScan();
            barcode2DWithSoft.close();
        }
        try {
            if (Print.IsOpened()) {
                Print.PortClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从服务器获取版本最新的版本信息
     */
    private void getVersionInfoFromServer(BaseBean<FeeRule> baseBean) {
        path = getExternalCacheDir() + "/1.1.1.jpg";
        //模拟从服务器获取信息
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        sharedPreferences.edit().putString("url", baseBean.getData().getAutoUpdateInfo().getFilePath()).commit();
        sharedPreferences.edit().putString("path", path).commit();
        //getExternalCacheDir获取到的路径 为系统为app分配的内存 卸载app后 该目录下的资源也会删除
        //比较版本信息
        try {
            int result = Utils.compareVersion(Utils.getVersionName(this), baseBean.getData().getAutoUpdateInfo().getVersion());
            if (result == -1) {//不是最新版本
                showDialog(baseBean);
            } else {
                //Toast.makeText(MainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跟新版本的信息的Dialog
     */
    private void showDialog(BaseBean<FeeRule> baseBean) {
        final Dialog dialog = new Dialog(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView version, content;
        Button left, right;
        View view = inflater.inflate(R.layout.version_update, null, false);
        version = view.findViewById(R.id.version);
        content = view.findViewById(R.id.content);
        left = view.findViewById(R.id.left);
        right = view.findViewById(R.id.right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            content.setText(Html.fromHtml(baseBean.getData().getAutoUpdateInfo().getUpdateInfo(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            content.setText(baseBean.getData().getAutoUpdateInfo().getUpdateInfo());
        }
        content.setMovementMethod(LinkMovementMethod.getInstance());
        version.setText("版本号：" + baseBean.getData().getAutoUpdateInfo().getVersion());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                downloadNewVersionFromServer();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(false);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        //dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        lp.width = wm.getDefaultDisplay().getWidth() / 10 * 9;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * 启动服务后台下载
     */
    private void downloadNewVersionFromServer() {
        if (new File(path).exists()) {
            new File(path).delete();
        }
        Toast.makeText(MainActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
        LoadingService.startUploadImg(this);
    }

    private MyReceive myReceive;//接受升级的广播

    /**
     * 定义广播接收者 接受下载状态
     */
    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.loading_over".equals(action)) {
                isLoading = false;
            } else if ("android.intent.action.loading".equals(action)) {
                isLoading = true;
            }
        }
    }

    private ScreenBroadcastReceiver mScreenReceiver;//接受屏幕状态的广播

    /**
     * 定义广播接收者 接受屏幕状态
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                // 开屏
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                ActivityCollecter.finishAll();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                // 解锁
            }
        }
    }

    /**
     * 注册广播
     */
    private void startScreenBroadcastReceiver() {
        mScreenReceiver = new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);//注册屏幕开关的广播

        myReceive = new MyReceive();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("android.intent.action.loading_over");
        filter1.addAction("android.intent.action.loading");
        registerReceiver(myReceive, filter1);//注册升级的广播
    }
}




