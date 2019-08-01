package com.ioter.medical.ui.activity;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

public class EnterMessageActivity extends BaseActivity {

    @Override
    public int setLayout() {
        return R.layout.activity_enter_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("入库详情");
        String id = getIntent().getStringExtra("id");


    }
}
