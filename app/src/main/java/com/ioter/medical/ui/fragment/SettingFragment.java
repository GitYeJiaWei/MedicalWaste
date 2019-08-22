package com.ioter.medical.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ioter.medical.R;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.ui.activity.BleActivity;
import com.ioter.medical.ui.activity.InformActivity;
import com.ioter.medical.ui.activity.PowerActivity;
import com.ioter.medical.ui.activity.ScanActivity;
import com.ioter.medical.ui.activity.UserActivity;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 设置
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.lin_inform)
    RelativeLayout linInform;
    @BindView(R.id.lin_user)
    RelativeLayout linUser;
    @BindView(R.id.lin_message)
    RelativeLayout linMessage;
    @BindView(R.id.lin_ble)
    RelativeLayout linBle;
    @BindView(R.id.btn_lease)
    Button btnLease;
    @BindView(R.id.lin_scan)
    RelativeLayout linScan;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.setting_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init(View view) {

    }

    @Override
    public void showBarCode(String barCode) {

    }


    @OnClick({R.id.lin_scan, R.id.lin_inform, R.id.lin_user, R.id.lin_message, R.id.lin_ble, R.id.btn_lease})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_inform:
                if (!ScreenUtils.Utils.isFastClick()) return;
                startActivity(new Intent(getActivity(), InformActivity.class));
                break;
            case R.id.lin_user:
                if (!ScreenUtils.Utils.isFastClick()) return;
                startActivity(new Intent(getActivity(), UserActivity.class));
                break;
            case R.id.lin_message:
                if (!ScreenUtils.Utils.isFastClick()) return;
                startActivity(new Intent(getActivity(), PowerActivity.class));
                break;
            case R.id.lin_ble:
                if (!ScreenUtils.Utils.isFastClick()) return;
                startActivity(new Intent(getActivity(), BleActivity.class));
                break;
            case R.id.btn_lease:
                if (!ScreenUtils.Utils.isFastClick()) return;
                getActivity().finish();
                break;
            case R.id.lin_scan:
                if (!ScreenUtils.Utils.isFastClick()) return;
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
        }
    }

}
