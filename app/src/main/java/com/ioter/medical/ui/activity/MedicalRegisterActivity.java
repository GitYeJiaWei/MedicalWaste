package com.ioter.medical.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Code;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedRegisterComponent;
import com.ioter.medical.di.module.MedRegisterModule;
import com.ioter.medical.presenter.MedRegisterPresenter;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import print.Print;

public class MedicalRegisterActivity extends BaseActivity<MedRegisterPresenter> implements MedRegisterContract.MedRegisterView {
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
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List<Map<String, String>> dataList;
    private String WasteTypeId = null;
    private String HandOverUserId = null;
    private WasteViewsBean wasteViewsBean;

    @Override
    public int setLayout() {
        return R.layout.activity_medical_collect;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedRegisterComponent.builder().appComponent(appComponent).medRegisterModule(new MedRegisterModule(this))
                .build().inject(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ToastUtil.toast("打印机断开");
                if (Print.IsOpened()) {
                    try {
                        Print.PortClose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == 2) {
                ToastUtil.toast("上盖开");
            } else if (msg.what == 3) {
                ToastUtil.toast("打印机温度异常");
            } else if (msg.what == 4) {
                ToastUtil.toast("传感器检测到无纸");
            }
        }
    };

    private void getState() {
        AppApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] statusData = Print.GetRealTimeStatus((byte) 3);//检测上盖和温度异常
                    byte[] statusData1 = Print.GetRealTimeStatus((byte) 4);//检测有无纸
                    if (statusData.length == 0) {
                        handler.sendEmptyMessage(1);
                    } else {
                        if ((statusData[0] & 0x20) == 32) {
                            handler.sendEmptyMessage(2);
                            Log.d("getState", "上盖开");
                        } else {
                            Log.d("getState", "上盖关");
                        }

                        if ((statusData[0] & 0x40) == 64) {
                            handler.sendEmptyMessage(3);
                            Log.d("getState", "打印机温度异常");
                        } else {
                            Log.d("getState", "打印机温度正常");
                        }

                        if ((statusData1[0] & 0x60) == 0) {
                            Log.d("getState", "传感器检测到有纸");
                        } else {
                            handler.sendEmptyMessage(4);
                            Log.d("getState", "传感器检测到无纸");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    @Override
    public void init() {
        title.setText("医废登记");

        dataList = new ArrayList<>();
        //医废类型查询
        initWasteTypes();


        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);

        getState();
    }

    private void initWasteTypes() {
        dataList.clear();

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.wastetypes();
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(MedicalRegisterActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("医废类型请求失败");
                                       finish();
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       //ToastUtil.toast("医废类型查询");
                                       if (baseBean.getData() != null) {
                                           dataList = (List<Map<String, String>>) baseBean.getData();
                                           String[] from = {"Id", "Name"};
                                           int[] to = {R.id.id, R.id.text};

                                           SimpleAdapter adapter = new SimpleAdapter(getApplication(), dataList, R.layout.gridview2_item, from, to);
                                           gridview.setAdapter(adapter);
                                           gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                               @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                               @Override
                                               public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                                       long arg3) {
                                                   WasteTypeId = dataList.get(arg2).get("Id").toString();
                                                   for (int i = 0; i < dataList.size(); i++) {
                                                       if (i == arg2) {
                                                           LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview.getChildAt(i)).getChildAt(0);
                                                           linearLayout.setBackground(getDrawable(R.drawable.button_back));
                                                       } else {
                                                           LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview.getChildAt(i)).getChildAt(0);
                                                           linearLayout.setBackground(getDrawable(R.drawable.back_text));
                                                       }
                                                   }
                                               }
                                           });
                                       }
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   mypDialog.cancel();
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                                   mypDialog.cancel();
                               }//订阅
                           }
                );
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
        String bar = null;
        if (barcode.contains("iotId")) {
            Code code = AppApplication.getGson().fromJson(barcode, Code.class);
            bar = code.getIotId();
        } else {
            ToastUtil.toast("垃圾袋二维码");
            return;
        }

        if (bar == null) {
            ToastUtil.toast("扫描失败，请重新扫描");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.getuser(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(MedicalRegisterActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("扫描失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       Map<String, String> map1 = (Map<String, String>) baseBean.getData();
                                       tvUser.setText(map1.get("Name"));
                                       tvRoom.setText(map1.get("Department"));
                                       HandOverUserId = map1.get("Id");
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   mypDialog.cancel();
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                                   mypDialog.cancel();
                               }//订阅
                           }
                );
    }

    @OnClick({R.id.btn_commit, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (!ScreenUtils.Utils.isFastClick()) return;

                if (TextUtils.isEmpty(WasteTypeId)) {
                    ToastUtil.toast("请选择废物类型");
                    return;
                }
                if (TextUtils.isEmpty(HandOverUserId)) {
                    ToastUtil.toast("请扫描交接人二维码");
                    return;
                }

                String weight = tvWeight.getText().toString();
                if (TextUtils.isEmpty(weight)) {
                    ToastUtil.toast("请填写称重信息");
                    return;
                }

                //称重数据
                BigDecimal bigDecimal = new BigDecimal(weight);
                double b1 = bigDecimal.doubleValue();
                Log.d("EnterDouble", "b1" + b1 + "  bigDecimal" + bigDecimal);
                if (b1 < 0.01 || b1 > 99.99) {
                    ToastUtil.toast("称重信息范围0.01~99.99之间");
                    return;
                }

                btnCommit.setEnabled(false);
                mPresenter.medRegister(HandOverUserId, bigDecimal, WasteTypeId);
                break;
            case R.id.btn_cancle:
                if (!ScreenUtils.Utils.isFastClick()) return;

                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private void clearData(){
        tvWeight.setText("");
        initWasteTypes();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交成功：");
        builder.setMessage("是否打印");
        builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(false);
        //设置正面按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                printMessage(dialog);
                btnCommit.setEnabled(true);
            }
        });
        //设置反面按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnCommit.setEnabled(true);
                //setResult(RESULT_OK);
                dialog.dismiss();
                //finish();
                clearData();
            }
        });
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }

    private void printMessage(DialogInterface dialog) {
        if (!Print.IsOpened()) {
            dialog.dismiss();
            ToastUtil.toast("请连接打印机");
            Intent intent = new Intent(MedicalRegisterActivity.this, BleActivity.class);
            intent.putExtra("print", "print");
            startActivityForResult(intent, 1);
        } else {
            try {
                PrintTestPage();//打印信息

                //setResult(RESULT_OK);
                dialog.dismiss();
                clearData();
                //finish();
            } catch (Exception e) {
                ToastUtil.toast(e.getMessage());
            }
        }
    }

    public void PrintTestPage() {
        try {
            //进入页模式。
            Print.SelectPageMode();
            //设置打印区域。
            Print.SetPageModePrintArea(0, 0, 600, 310);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(50, 80);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintText("***医院  医废交接单***", 0, 2, 0);
            //设置打印区域。
            Print.SetPageModePrintArea(0, 135, 200, 175);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(0, 0);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintQRCode("{iotEPC:" + wasteViewsBean.getId() + "}", 3, 48, 1);
            //设置打印区域。
            Print.SetPageModePrintArea(80, 95, 300, 215);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(0, 0);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintText(wasteViewsBean.getWasteType() + " 重量:" + wasteViewsBean.getWeight() + "kg", 0, 2, 0);
            Print.PrintText("科室:" + wasteViewsBean.getDepartmentName(), 0, 2, 0);
            Print.PrintText("移交人员:" + wasteViewsBean.getHandOverUserName(), 0, 2, 0);
            Print.PrintText("回收人员:" + wasteViewsBean.getCollectUserName(), 0, 2, 0);
            Print.PrintText("收集时间:" + wasteViewsBean.getCollectionTime().substring(0,16), 0, 2, 0);
            //打印。
            Print.PrintDataInPageMode();
            //标签空隙校准
            Print.GotoNextLabel();
        } catch (Exception e) {
            ToastUtil.toast(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    PrintTestPage();//打印信息
                    clearData();

                    //setResult(RESULT_OK);
                    //finish();
                } catch (Exception e) {
                    ToastUtil.toast(e.getMessage());
                }
            }
            if (resultCode == RESULT_CANCELED) {
                try {
                    ToastUtil.toast("连接打印机失败!");
                    //clearData();
                    //setResult(RESULT_OK);
                    //finish();
                } catch (Exception e) {
                    ToastUtil.toast(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);

        super.onBackPressed();
    }

    @Override
    public void medRegisterResult(BaseBean<WasteViewsBean> baseBean) {
        if (baseBean == null) {
            btnCommit.setEnabled(true);
            ToastUtil.toast("提交失败");
            return;
        }
        if (baseBean.getCode() == 0 && baseBean.getData() != null) {
            wasteViewsBean = baseBean.getData();
            createDialog();
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        btnCommit.setEnabled(true);
    }
}
