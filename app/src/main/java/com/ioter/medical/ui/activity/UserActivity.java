package com.ioter.medical.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import butterknife.ButterKnife;
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
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        title.setText("账号设置");
        edtUser.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.USER_NAME));
        edtPass.setText("");
        edtPass1.setText("");
        edtPass2.setText("");
    }


    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        String password = edtPass.getText().toString();
        String newpassword = edtPass1.getText().toString();
        String twopassword = edtPass2.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast("请输入初始密码");
            return;
        }
        if (TextUtils.isEmpty(newpassword)) {
            ToastUtil.toast("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(twopassword)) {
            ToastUtil.toast("请输入确认密码");
            return;
        }

        btnSave.setEnabled(false);
        mPresenter.setting(password, newpassword, twopassword);
    }

    @Override
    public void settingResult(BaseBean<String> baseBean) {
        if (baseBean == null) {
            btnSave.setEnabled(true);
            ToastUtil.toast("修改密码失败");
            return;
        }
        btnSave.setEnabled(true);
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("密码修改成功");
            ACache.get(AppApplication.getApplication()).put(PASS_WORD, edtPass1.getText().toString());
            finish();
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        btnSave.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
