package com.ioter.medical.ui.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.BaseEpc;
import com.ioter.medical.bean.Code;
import com.ioter.medical.bean.Code1;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerOutRegisterComponent;
import com.ioter.medical.di.module.OutRegisterModule;
import com.ioter.medical.presenter.OutRegisterPresenter;
import com.ioter.medical.presenter.contract.OutRegisterContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;

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

public class OutRegisterActivity extends BaseActivity<OutRegisterPresenter> implements OutRegisterContract.OutRegisterView {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_totalWeight)
    TextView tvTotalWeight;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    private String HandOverUserId = null;
    private HashMap<String, String> map = new HashMap<>();
    private MedicalCollectAdapter medicalCollectAdapter;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ArrayList<String> DustbinEpcs = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_out_register;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerOutRegisterComponent.builder().appComponent(appComponent).outRegisterModule(new OutRegisterModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("出库登记");

        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);

        medicalCollectAdapter = new MedicalCollectAdapter(this, "outRegister");
        listLease.setAdapter(medicalCollectAdapter);
    }

    //获取EPC群读数据
    @Override
    public void handleUi(BaseEpc baseEpc) {
        super.handleUi(baseEpc);
        if (map.containsValue(baseEpc._EPC)) {
            return;
        }
        map.put("dustbinepc", baseEpc._EPC);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.getdustbininfo(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       Map<String, String> stringMap = (Map<String, String>) baseBean.getData();

                                       EPC epc = new EPC();
                                       epc.setId(stringMap.get("Epc"));
                                       epc.setDepartmentName(stringMap.get("Epc"));
                                       epc.setWasteType(stringMap.get("Epc"));
                                       epc.setWeight(12.5);
                                       epclist.add(epc);


                                       medicalCollectAdapter.updateDatas(epclist);

                                       double a = 0;
                                       for (int i = 0; i < epclist.size(); i++) {
                                           a += epclist.get(i).getWeight();
                                           DustbinEpcs.add(epclist.get(i).getId());
                                       }
                                       tvWeight.setText(a + "");
                                       tvTotalWeight.setText(a + "");
                                       //ToastUtil.toast("扫描成功");
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast("扫描失败");
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ScanBarcode();
            }
        }
        if (keyCode == 139) {
            if (event.getRepeatCount() == 0) {
                readTag("扫描");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 139) {
            if (event.getRepeatCount() == 0) {
                readTag("停止");
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void readTag(String state) {
        if (state.equals("扫描")) {
            if (AppApplication.mReader.startInventoryTag((byte) 0, (byte) 0)) {
                loopFlag = true;
                new TagThread(10).start();
            } else {
                AppApplication.mReader.stopInventory();
                loopFlag = false;
                ToastUtil.toast("扫描失败");
            }
        } else {
            AppApplication.mReader.stopInventory();
            loopFlag = false;
        }
    }

    @Override
    public void showBarCode(String barcode) {
        super.showBarCode(barcode);
        if (barcode.contains("iotId")) {
            Code code = AppApplication.getGson().fromJson(barcode, Code.class);
            String bar = code.getIotId();
            getId(bar);
        }
        if (barcode.contains("iotEPC")) {
            ToastUtil.toast("请扫描交接人二维码");
        }
    }

    private void getId(String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.getuser(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("扫描失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0) {
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
                                   ToastUtil.toast("扫描失败");
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
    }

    @Override
    public void OutRegisterResult(BaseBean<Object> baseBean) {

    }

    @OnClick({R.id.btn_commit, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (TextUtils.isEmpty(HandOverUserId)) {
                    ToastUtil.toast("请扫描交接人二维码");
                    return;
                }
                String weight = tvWeight.getText().toString();
                if (TextUtils.isEmpty(weight)) {
                    ToastUtil.toast("复核重量不能为空");
                    return;
                }

                //称重数据
                BigDecimal bigDecimal = new BigDecimal(weight);

                Map<String, Object> map = new HashMap<>();
                map.put("DelivererId", "");
                map.put("ReceiverId", HandOverUserId);
                map.put("ReceiveTotalWeight", bigDecimal);
                map.put("DustbinEpcs", AppApplication.getGson().toJson(DustbinEpcs));
                mPresenter.OutRegister(map);
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}
