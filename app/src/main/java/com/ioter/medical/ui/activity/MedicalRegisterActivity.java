package com.ioter.medical.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.util.SoundManage;
import com.ioter.medical.di.component.AppComponent;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalRegisterActivity extends BaseActivity {

    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_room)
    TextView tvRoom;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.radio1)
    RadioGroup radio1;
    @BindView(R.id.radio2)
    RadioGroup radio2;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;

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
        radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.garbage_infected:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_infected);
                        break;
                    case R.id.garbage_medical:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_medical);
                        break;
                    case R.id.garbage_damaging:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_damaging);
                        break;
                }
            }
        });

        radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.garbage_chemical:
                        radio1.clearCheck();
                        radio2.check(R.id.garbage_chemical);
                        break;
                    case R.id.garbage_pathological:
                        radio1.clearCheck();
                        radio2.check(R.id.garbage_pathological);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ScanBarcode();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showBarCode(String barcode) {
        super.showBarCode(barcode);
        tvUser.setText(barcode);
    }

    @OnClick({R.id.btn_commit, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:

                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}
