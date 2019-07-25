package com.ioter.medical.ui.activity;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

public class MedicalRegisterActivity extends BaseActivity {


    @Override
    public int setLayout() {
        return R.layout.activity_medical_collect;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("医废登记");
    }
}
