package com.ioter.medical.ui.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.ActivityCollecter;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerLoginComponent;
import com.ioter.medical.di.module.LoginModule;
import com.ioter.medical.presenter.LoginPresenter;
import com.ioter.medical.presenter.contract.LoginContract;
import com.ioter.medical.ui.widget.LoadingButton;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 1.MVP 大家都知道 P的作用是让MV间接拥有肮脏的PY交易，而不是直接让他们进行交易。
 * 2.Rxjava 响应式编程 0.0 一个特别屌的地方就是你可以随便切换线程,异步
 * 3.Retrofit 代替Volley的东东，网络请求
 * 4.Dagger2 Android 的IOC框架，即控制反转，也叫依赖注入，解耦用的
 * 4.DataBinding MVVM的东东，用起来比较方便，可以让bean与View绑定，抛弃setText()!
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.LoginView {
    @BindView(R.id.txt_mobi)
    EditText txtMobi;
    @BindView(R.id.txt_password)
    EditText txtPassword;
    @BindView(R.id.btn_login)
    LoadingButton btnLogin;

    public static final String REAL_NAME = "realname";
    public static final String USER_NAME = "username";
    public static final String PASS_WORD = "password";
    public static final String USER_ID = "userid";
    public static final String TOKEN = "token";

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        //mPresenter 不再以new的形式创建
        //Dagger就是依赖注入，解耦用的。常见的使用地方就是注入Presenter到Activity中
        //其实就是使用Component创建一个Presenter，而Presenter所需的参数都是由Moudule提供的

        DaggerLoginComponent.builder().appComponent(appComponent).loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        String username = ACache.get(AppApplication.getApplication()).getAsString(USER_NAME);
        String password = ACache.get(AppApplication.getApplication()).getAsString(PASS_WORD);

        if (!TextUtils.isEmpty(username)) {
            txtMobi.setText(username);
        }
        if (!TextUtils.isEmpty(password)) {
            txtPassword.setText(password);
        }
        initView();
    }

    public void initView(){
        //初始化二维码扫描头
        if (Build.VERSION.SDK_INT > 21) {

            //扫条码 需要相机对应用开启相机和存储权限；
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
            }
            //读写内存权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat
                        .requestPermissions(
                                this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                2);
            }

        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
        }
    }

    @Override
    public void loginResult(Object baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("登陆失败");
            return;
        }
        Map<String,Object> baseMap = (Map<String, Object>) baseBean;
        if (baseMap.get("access_token")!=null){
            ACache.get(AppApplication.getApplication()).put(REAL_NAME, baseMap.get("RealName").toString());
            ACache.get(AppApplication.getApplication()).put(USER_NAME, txtMobi.getText().toString());
            ACache.get(AppApplication.getApplication()).put(PASS_WORD, txtPassword.getText().toString());
            ACache.get(AppApplication.getApplication()).put(USER_ID, baseMap.get("Id").toString());
            ACache.get(AppApplication.getApplication()).put(TOKEN, baseMap.get("access_token").toString());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            //ToastUtil.toast("登录成功");
        }else {
            ToastUtil.toast(baseMap.get("error_description").toString());
        }
    }

    @Override
    public void showError(String msg) {
        btnLogin.showButtonText();
        ToastUtil.toast("用户名或密码错误！");
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        String username = txtMobi.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.toast("请输入账号");
            return;
        }
        String password = txtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast("请输入密码");
            return;
        }
        ACache.get(AppApplication.getApplication()).remove(TOKEN);
        mPresenter.login(username, password);
    }

    @Override
    public void showLoading() {
        btnLogin.showLoading();
    }

    @Override
    public void dismissLoading() {
        btnLogin.showButtonText();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //ActivityCollecter.finishAll();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

