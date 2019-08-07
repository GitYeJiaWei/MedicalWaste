package com.ioter.medical.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.ui.activity.InformActivity;
import com.ioter.medical.ui.activity.LoginActivity;
import com.ioter.medical.ui.activity.MainActivity;
import com.ioter.medical.ui.activity.PowerActivity;
import com.ioter.medical.ui.activity.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    public void setBarCode(String barCode) {

    }


    @OnClick({R.id.lin_inform, R.id.lin_user, R.id.lin_message, R.id.lin_ble,R.id.btn_lease})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_inform:
                startActivity(new Intent(getActivity(), InformActivity.class));
                break;
            case R.id.lin_user:
                startActivity(new Intent(getActivity(), UserActivity.class));
                break;
            case R.id.lin_message:
                startActivity(new Intent(getActivity(), PowerActivity.class));
                break;
            case R.id.lin_ble:
                //startActivity(new Intent(getActivity(), BleActivity.class));
                break;
            case R.id.btn_lease:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }

}
