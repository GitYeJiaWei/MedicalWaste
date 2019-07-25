package com.ioter.medical.ui.activity;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

public class MedicalOutActivity extends BaseActivity {

    @Override
    public int setLayout() {
        return R.layout.activity_medical_out;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("医废出库");
    }
}
