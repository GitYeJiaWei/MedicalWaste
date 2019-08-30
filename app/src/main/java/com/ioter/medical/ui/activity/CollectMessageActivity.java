package com.ioter.medical.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import print.Print;

public class CollectMessageActivity extends BaseActivity {

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
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private WasteViewsBean wasteViewsBean;

    @Override
    public int setLayout() {
        return R.layout.activity_collect_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

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
        title.setText("收集详情");
        //获取打印机状态
        getState();

        String epc = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(epc)) {
            getEPC(epc);
        }
    }

    private void getEPC(final String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<WasteViewsBean>> qqDataCall = apIservice.wastedetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<WasteViewsBean>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(CollectMessageActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<WasteViewsBean> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("查询失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       wasteViewsBean = baseBean.getData();
                                       tvName.setText(wasteViewsBean.getCollectUserName());
                                       tvTime.setText(wasteViewsBean.getCollectionTime());
                                       tvUser.setText(wasteViewsBean.getHandOverUserName());
                                       tvRoom.setText(wasteViewsBean.getDepartmentName());
                                       tvWeight.setText(wasteViewsBean.getWeight() + "");
                                       tvType.setText(wasteViewsBean.getWasteType());
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

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("条码打印：");
        builder.setMessage("是否打印");
        builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(false);
        //设置正面按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                printMessage(dialog);
            }
        });
        //设置反面按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_OK);
                dialog.dismiss();
                finish();
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
            Intent intent = new Intent(CollectMessageActivity.this, BleActivity.class);
            intent.putExtra("print", "print");
            startActivityForResult(intent, 1);
        } else {
            try {
                PrintTestPage();//打印信息

                dialog.dismiss();
                finish();
            } catch (Exception e) {
                ToastUtil.toast(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    PrintTestPage();//打印信息

                    finish();
                } catch (Exception e) {
                    ToastUtil.toast(e.getMessage());
                }
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    public void PrintTestPage() {
        try {
            //进入页模式。
            Print.SelectPageMode();
            //设置打印区域。
            Print.SetPageModePrintArea(0, 0, 600, 280);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(50, 5);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintText("***医院  医废交接单***", 0, 2, 0);
            //设置打印区域。
            Print.SetPageModePrintArea(0, 90, 200, 280);
            //设置打印方向
            Print.SetPageModePrintDirection(0);
            //设置 X,Y 的坐标。
            Print.SetPageModeAbsolutePosition(0, 0);
            //打印二维码（你也可以打印文字和条码）。
            Print.PrintQRCode("{iotEPC:" + wasteViewsBean.getId() + "}", 3, 48, 1);
            //设置打印区域。
            Print.SetPageModePrintArea(80, 50, 300, 280);
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
        } catch (Exception e) {
            ToastUtil.toast(e.getMessage());
        }
    }

    @OnClick({R.id.btn_scan, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                if (!ScreenUtils.Utils.isFastClick()) return;
                createDialog();
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}
