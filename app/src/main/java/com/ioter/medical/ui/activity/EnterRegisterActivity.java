package com.ioter.medical.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android_serialport_api.SerialPortUtil;
import butterknife.BindView;
import butterknife.OnClick;
import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerEnterRegisterComponent;
import com.ioter.medical.di.module.EnterRegisterModule;
import com.ioter.medical.presenter.EnterRegisterPresenter;
import com.ioter.medical.presenter.contract.EnterRegisterContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;
import com.ioter.medical.ui.widget.SwipeListLayout;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


public class EnterRegisterActivity extends BaseActivity<EnterRegisterPresenter> implements EnterRegisterContract.EnterRegisterView {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_user)
    EditText tvUser;
    @BindView(R.id.tv_room)
    TextView tvRoom;
    @BindView(R.id.tv_totalWeight)
    TextView tvTotalWeight;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_user)
    Button btnUser;
    @BindView(R.id.btn_weight)
    Button btnWeight;
    private String HandOverUserId = null;
    private HashMap<String, String> mapEpc = new HashMap<>();
    private Map<String, String> map = new HashMap<>();
    private MedicalCollectAdapter medicalCollectAdapter;
    public ArrayList<EPC> epclist = new ArrayList<>();
    private ArrayList<String> WasteIds = new ArrayList<>();
    private static Set<SwipeListLayout> sets = new HashSet();
    static EditText tvWeight;

    public static void refreshTextView(double data) {
        tvWeight.setText(data + "");
    }

    @Override
    public int setLayout() {
        return R.layout.activity_enter_register;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerEnterRegisterComponent.builder().appComponent(appComponent).enterRegisterModule(new EnterRegisterModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        title.setText("入库登记");
        tvWeight = findViewById(R.id.tv_weight);
        SerialPortUtil.openSrialPort(2);

        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);

        medicalCollectAdapter = new MedicalCollectAdapter(this, "enterRegister");
        listLease.setAdapter(medicalCollectAdapter);

        listLease.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    // OnScrollListener.SCROLL_STATE_FLING; //屏幕处于甩动状态
                    // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                    // OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;// 手指接触状态
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        medicalCollectAdapter.setCallBackDelete(new MedicalCollectAdapter.CallBackDelete() {
            @Override
            public void onDeleteItem(EPC epc) {
                mapEpc.remove(epc.getId());

                epclist.remove(epc);
                medicalCollectAdapter.updateDatas(epclist);
                double sum = 0;
                for (int i = 0; i < epclist.size(); i++) {
                    BigDecimal bd1 = new BigDecimal(Double.toString(epclist.get(i).getWeight()));
                    BigDecimal bd2 = new BigDecimal(Double.toString(sum));
                    sum = bd1.add(bd2).doubleValue();
                }
                WasteIds.remove(epc.getId());
                tvTotalWeight.setText("总重量：" + sum + "kg");
                tvWeight.setText(sum + "");
            }
        });
    }

    public static class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }
    }

    public void showBarCode(String barcode) {
        if (barcode.startsWith("BB")) {
            getId(barcode);
        }
        if (barcode.startsWith("AA")) {
            if (mapEpc.containsKey(barcode)) {
                ToastUtil.toast("重复的医废二维码");
                return;
            }
            getEPC(barcode);
        }
    }

    private void getId(String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("cardCode", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.getuser(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(EnterRegisterActivity.this);
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
                                       HandOverUserId = map1.get("Id");
                                       ToastUtil.toast("扫描成功");
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

    private void getEPC(final String bar) {
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<WasteViewsBean>> qqDataCall = apIservice.wastedetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<WasteViewsBean>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(EnterRegisterActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<WasteViewsBean> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("扫描失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       mapEpc.put(bar, bar);
                                       EPC epc = new EPC();
                                       epc.setId(baseBean.getData().getId());
                                       epc.setDepartmentName(baseBean.getData().getDepartmentName());
                                       epc.setWasteType(baseBean.getData().getWasteTypeName());
                                       epc.setWeight(baseBean.getData().getWeight());
                                       Collections.reverse(epclist);
                                       epclist.add(epc);

                                       Collections.reverse(epclist);
                                       medicalCollectAdapter.updateDatas(epclist);

                                       double sum = 0;
                                       for (int i = 0; i < epclist.size(); i++) {
                                           BigDecimal bd1 = new BigDecimal(Double.toString(epclist.get(i).getWeight()));
                                           BigDecimal bd2 = new BigDecimal(Double.toString(sum));
                                           sum = bd1.add(bd2).doubleValue();
                                       }
                                       WasteIds.add(baseBean.getData().getId());
                                       tvTotalWeight.setText("总重量：" + sum + "kg");
                                       tvWeight.setText(sum + "");
                                       //ToastUtil.toast("扫描成功");
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

    @OnClick({R.id.btn_commit, R.id.btn_cancle, R.id.btn_user, R.id.btn_weight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (!ScreenUtils.Utils.isFastClick()) return;

                if (TextUtils.isEmpty(HandOverUserId)) {
                    ToastUtil.toast("请扫描交接人二维码");
                    return;
                }
                String weight = tvWeight.getText().toString();
                if (TextUtils.isEmpty(weight)) {
                    ToastUtil.toast("复核重量不能为空");
                    return;
                }
                String DushbinEpc = tvRoom.getText().toString();
                if (TextUtils.isEmpty(DushbinEpc)) {
                    ToastUtil.toast("周转桶标签不能为空");
                    return;
                }
                //称重数据
                BigDecimal bigDecimal = new BigDecimal(weight);
                double b1 = bigDecimal.doubleValue();
                Log.d("EnterDouble", "b1" + b1 + "  bigDecimal" + bigDecimal);
                if (b1 < 0.01 || b1 > 999.99) {
                    ToastUtil.toast("复核重量范围0.01~999.99之间");
                    return;
                }

                btnCommit.setEnabled(false);

                Map<String, Object> map = new HashMap<>();
                map.put("ReceiverId", HandOverUserId);
                map.put("DushbinEpc", DushbinEpc);
                map.put("ReceivedWeight", bigDecimal);
                map.put("WasteIds", AppApplication.getGson().toJson(WasteIds));
                mPresenter.EnterRegister(map);
                break;
            case R.id.btn_cancle:
                if (!ScreenUtils.Utils.isFastClick()) return;

                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_user:
                String user = tvUser.getText().toString().trim();
                if (user.toUpperCase().startsWith("AA") || user.toUpperCase().startsWith("BB")) {
                    showBarCode(user.toUpperCase());
                } else {
                    ToastUtil.toast("二维码不符合");
                }
                break;
            case R.id.btn_weight:
                //读取稳定重量
                byte[] params = new byte[]{0x11, 0x41, 0x3F, 0x11, 0x0D};
                SerialPortUtil.sendSerialPort(params);
                break;
        }
    }

    private void clearData() {
        tvWeight.setText("");
        tvRoom.setText("");
        WasteIds.clear();
        tvTotalWeight.setText("总重量：AAAkg");
        epclist.clear();
        medicalCollectAdapter.updateDatas(epclist);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        SerialPortUtil.closeSerialPort();
        super.onDestroy();
    }

    @Override
    public void EnterRegisterResult(BaseBean<Object> baseBean) {
        btnCommit.setEnabled(true);
        if (baseBean == null) {
            ToastUtil.toast("提交失败");
            return;
        }
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("提交成功");
            clearData();
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        btnCommit.setEnabled(false);
    }
}
