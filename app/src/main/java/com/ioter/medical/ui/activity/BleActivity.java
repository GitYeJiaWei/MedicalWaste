package com.ioter.medical.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ioter.medical.R;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import print.Print;

public class BleActivity extends BaseActivity {

    @BindView(R.id.btn_bl)
    Button btnBl;
    @BindView(R.id.tv_ble)
    TextView tvBle;
    @BindView(R.id.btn_print)
    Button btnPrint;
    private BluetoothAdapter mBluetoothAdapter;
    private String MedicalRegister = null;

    @Override
    public int setLayout() {
        return R.layout.activity_ble;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("蓝牙设置");

        EnableBluetooth();
        MedicalRegister = getIntent().getStringExtra("print");
        getState();
    }

    private void getState() {
        try {
            byte[] statusData = Print.GetRealTimeStatus((byte)3);
            byte[] statusData1 = Print.GetRealTimeStatus((byte)4);
            if (statusData.length ==0){
                ToastUtil.toast("打印机未打开");
                tvBle.setText("请连接打印机");
                btnBl.setText("蓝牙连接");
                return;
            }else {
                //ToastUtil.toast(statusData[0]);
                if ((statusData[0]&0x20) == 32){
                    ToastUtil.toast("上盖开");
                    Log.d("getState", "上盖开");
                }else {
                    Log.d("getState", "上盖关");
                }

                if ((statusData[0]&0x40) == 64){
                    ToastUtil.toast("打印机温度异常");
                    Log.d("getState", "打印机温度异常");
                }else {
                    Log.d("getState", "打印机温度正常");
                }

                if ((statusData1[0]&0x60) == 0){
                    Log.d("getState", "传感器检测到有纸");
                }else {
                    ToastUtil.toast("传感器检测到无纸");
                    Log.d("getState", "传感器检测到无纸");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Print.IsOpened()) {
            tvBle.setText("已连接成功");
            btnBl.setText("断开连接");
        } else {
            tvBle.setText("请连接打印机");
            btnBl.setText("蓝牙连接");
        }
    }

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }


    @OnClick({R.id.btn_bl, R.id.btn_print})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bl:
                if (!ScreenUtils.Utils.isFastClick()) return;

                if (btnBl.getText().toString().equals("断开连接")) {
                    try {
                        if (Print.IsOpened()) {
                            Print.PortClose();
                            getState();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        //校验是否已具有模糊定位权限
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    100);
                        } else {
                            //具有权限
                            Intent serverIntent = new Intent(this, Activity_DeviceList.class);
                            startActivityForResult(serverIntent, 1);
                            return;
                        }
                    } else {
                        //系统不高于6.0直接执行
                        Intent serverIntent = new Intent(this, Activity_DeviceList.class);
                        startActivityForResult(serverIntent, 1);
                    }
                }
                break;
            case R.id.btn_print:
                if (!ScreenUtils.Utils.isFastClick()) return;

                if (!Print.IsOpened()) {
                    ToastUtil.toast("请连接打印机");
                    return;
                }
                PrintTestPage();
                break;
        }

    }

    public void PrintTestPage() {
        try {
            //进入页模式。
            Print.SelectPageMode();
            //设置打印区域。
            Print.SetPageModePrintArea(0, 0, 600, 200);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(50, 10);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintText("***医院  医废交接单***",0,2,0);
            //设置打印区域。
            Print.SetPageModePrintArea(0, 70, 200, 200);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(0, 0);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintQRCode("abcdef", 5, 48, 1);
            //设置打印区域。
            Print.SetPageModePrintArea(110, 50, 300, 200);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(0, 0);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintText("损伤性垃圾 重量:510.54kg",0,1,0);
            Print.PrintText("科室:急诊科",0,0,0);
            Print.PrintText("移交人员:AAA",0,0,0);
            Print.PrintText("回收人员:AAA",0,0,0);
            Print.PrintText("收集时间:2019-08-01 18:00:00",0,1,0);
            //打印。
            Print.PrintDataInPageMode();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String strIsConnected = data.getExtras().getString("is_connected");
                    if (strIsConnected.equals("NO")) {
                        tvBle.setText("连接失败");
                        btnBl.setText("蓝牙连接");
                        ToastUtil.toast("连接失败!");
                    } else {
                        tvBle.setText("已连接成功");
                        btnBl.setText("断开连接");
                        ToastUtil.toast("连接成功!");
                        if (!TextUtils.isEmpty(MedicalRegister)) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
        }
    }
}