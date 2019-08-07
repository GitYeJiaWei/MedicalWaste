package com.ioter.medical.ui.activity;

import android.os.Bundle;
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
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerEnterRegisterComponent;
import com.ioter.medical.di.module.EnterRegisterModule;
import com.ioter.medical.presenter.EnterRegisterPresenter;
import com.ioter.medical.presenter.contract.EnterRegisterContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EnterRegisterActivity extends BaseActivity<EnterRegisterPresenter> implements EnterRegisterContract.EnterRegisterView {

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
    @BindView(R.id.tv_totalWeight)
    TextView tvTotalWeight;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    private String HandOverUserId = null;
    private HashMap<String,String> map = new HashMap<>();
    private MedicalCollectAdapter medicalCollectAdapter;
    private ArrayList<EPC> epclist = new ArrayList<>();
    private ArrayList<String> WasteIds = new ArrayList<>();

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
        setTitle("入库登记");

        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);

        medicalCollectAdapter = new MedicalCollectAdapter(this, "enterRegister");
        listLease.setAdapter(medicalCollectAdapter);
    }

    //获取EPC群读数据
    @Override
    public void handleUi(BaseEpc baseEpc) {
        super.handleUi(baseEpc);
        if (map.containsKey(baseEpc._EPC)) {
            return;
        }
        tvRoom.setText(baseEpc._EPC);
        map.put(baseEpc._EPC,"");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ScanBarcode();
            }
        }
        if (keyCode == 139){
            if (event.getRepeatCount() == 0) {
                readTag("扫描");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 139){
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
            Code1 code1 = AppApplication.getGson().fromJson(barcode, Code1.class);
            String bar = code1.getIotEPC();
            getEPC(bar);
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

    private void getEPC(String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<WasteViewsBean>> qqDataCall = apIservice.wastedetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<WasteViewsBean>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<WasteViewsBean> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("扫描失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0) {
                                       EPC epc = new EPC();
                                       epc.setId(baseBean.getData().getId());
                                       epc.setDepartmentName(baseBean.getData().getDepartmentName());
                                       epc.setWasteType(baseBean.getData().getWasteType());
                                       epc.setWeight(baseBean.getData().getWeight());
                                       epclist.add(epc);

                                       medicalCollectAdapter.updateDatas(epclist);

                                       double a =0;
                                       for (int i = 0; i < epclist.size(); i++) {
                                           a+=epclist.get(i).getWeight();
                                           WasteIds.add(epclist.get(i).getId());
                                       }
                                       tvWeight.setText(a+"");
                                       tvTotalWeight.setText(a+"");
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
                String DushbinEpc = tvRoom.getText().toString();
                if (TextUtils.isEmpty(DushbinEpc)){
                    ToastUtil.toast("周转桶标签不能为空");
                    return;
                }
                //称重数据
                BigDecimal bigDecimal = new BigDecimal(weight);

                Map<String, Object> map = new HashMap<>();
                map.put("ReceiverId", HandOverUserId);
                map.put("DushbinEpc", DushbinEpc);
                map.put("ReceivedWeight", bigDecimal);
                map.put("WasteIds", AppApplication.getGson().toJson(WasteIds));
                mPresenter.EnterRegister(map);
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    @Override
    public void EnterRegisterResult(BaseBean<Object> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("提交失败");
            return;
        }
        if (baseBean.getCode() == 0) {
            ToastUtil.toast("提交成功");
            setResult(RESULT_OK);
            finish();
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

}
