package com.ioter.medical.ui.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

public class InformActivity extends BaseActivity {

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_host)
    EditText etHost;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int setLayout() {
        return R.layout.activity_inform;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        title.setText("通讯设置");

        String ip = ACache.get(AppApplication.getApplication()).getAsString("BASE_URL");
        if (ip == null) {
            ip = ApiService.BASE_URL;
        }
        etIp.setText(ip);
    }


    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        String ip = etIp.getText().toString();
        if (TextUtils.isEmpty(ip)) {
            ToastUtil.toast("IP地址或端口号不能为空");
        } else {
            ACache.get(AppApplication.getApplication()).put("BASE_URL", ip);
            ToastUtil.toast("保存成功");
            finish();
        }

    }
}
