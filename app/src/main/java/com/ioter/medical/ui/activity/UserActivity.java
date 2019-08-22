package com.ioter.medical.ui.activity;

import android.widget.Button;
import android.widget.EditText;


import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerSettingComponent;
import com.ioter.medical.di.module.SettingModule;
import com.ioter.medical.presenter.SettingPresenter;
import com.ioter.medical.presenter.contract.SettingContract;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ioter.medical.ui.activity.LoginActivity.PASS_WORD;

public class UserActivity extends BaseActivity<SettingPresenter> implements SettingContract.SettingView {

    @BindView(R.id.edt_user)
    EditText edtUser;
    @BindView(R.id.edt_pass)
    EditText edtPass;
    @BindView(R.id.edt_pass1)
    EditText edtPass1;
    @BindView(R.id.edt_pass2)
    EditText edtPass2;
    @BindView(R.id.btn_save)
    Button btnSave;

    @Override
    public int setLayout() {
        return R.layout.activity_user;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerSettingComponent.builder().appComponent(appComponent).settingModule(new SettingModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("账号设置");
        edtUser.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.USER_NAME));
        edtPass.setText(ACache.get(AppApplication.getApplication()).getAsString(PASS_WORD));
        edtPass1.setText(ACache.get(AppApplication.getApplication()).getAsString(PASS_WORD));
        edtPass2.setText(ACache.get(AppApplication.getApplication()).getAsString(PASS_WORD));
    }


    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        String password = edtPass.getText().toString();
        String newpassword = edtPass1.getText().toString();
        String twopassword = edtPass2.getText().toString();
        mPresenter.setting(password,newpassword,twopassword);
    }

    @Override
    public void settingResult(BaseBean<String> baseBean) {
        if (baseBean == null){
            ToastUtil.toast("修改密码失败");
            return;
        }
        if (baseBean.getCode()==0){
            ToastUtil.toast("密码修改成功");
            ACache.get(AppApplication.getApplication()).put(PASS_WORD, edtPass1.getText().toString());
            finish();
        }else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtil.toast("操作失败,请退出重新登录");
    }
}
