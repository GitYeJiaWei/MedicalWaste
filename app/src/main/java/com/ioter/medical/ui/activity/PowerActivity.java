package com.ioter.medical.ui.activity;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ioter.medical.ui.activity.MainActivity.mReader;

/**
 * 功率设置
 */
public class PowerActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.seekBar1)
    SeekBar seekBar1;
    @BindView(R.id.tvShow1)
    TextView tvShow1;
    @BindView(R.id.btn_sure)
    Button btnSure;

    @Override
    public int setLayout() {
        return R.layout.activity_power;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("功率设置");
        initview();

    }

    private void initview() {
        String key1 = ACache.get(AppApplication.getApplication()).getAsString("key1");
        if (TextUtils.isEmpty(key1)) {
            key1 = "15";
        }
        tvShow1.setText(key1);
        seekBar1.setProgress(Integer.valueOf(key1) - 5);

        seekBar1.setOnSeekBarChangeListener(this);

    }


    @OnClick(R.id.btn_sure)
    public void onViewClicked() {
        String key1 = tvShow1.getText().toString();
        ACache.get(AppApplication.getApplication()).put("key1", key1);
        mReader.setPower(Integer.valueOf(key1));

        ToastUtil.toast("保存成功");
        finish();
    }

    //拖动中
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar1:
                tvShow1.setText(progress + 5 + "");
                break;
        }
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
