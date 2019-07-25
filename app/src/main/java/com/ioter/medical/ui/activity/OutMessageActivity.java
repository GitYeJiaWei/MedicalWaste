package com.ioter.medical.ui.activity;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

public class OutMessageActivity extends BaseActivity {

    @Override
    public int setLayout() {
        return R.layout.activity_out_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("出库详情");
    }
}
