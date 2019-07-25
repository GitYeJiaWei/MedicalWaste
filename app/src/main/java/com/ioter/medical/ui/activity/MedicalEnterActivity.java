package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalEnterActivity extends BaseActivity {

    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_lease)
    Button btnLease;

    @Override
    public int setLayout() {
        return R.layout.activity_medical_enter;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("医废入库");
    }


    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        startActivity(new Intent(this,EnterRegisterActivity.class));
    }
}
