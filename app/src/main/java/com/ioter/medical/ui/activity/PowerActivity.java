package com.ioter.medical.ui.activity;

import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int setLayout() {
        return R.layout.activity_power;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        title.setText("功率设置");
        initview();
    }

    private void initview() {
        seekBar1.setOnSeekBarChangeListener(this);
    }


    @OnClick(R.id.btn_sure)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

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
