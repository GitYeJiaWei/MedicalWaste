package com.ioter.medical.ui.activity;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

public class OutRegisterActivity extends BaseActivity {

    @Override
    public int setLayout() {
        return R.layout.activity_out_register;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("出库登记");
    }
}
