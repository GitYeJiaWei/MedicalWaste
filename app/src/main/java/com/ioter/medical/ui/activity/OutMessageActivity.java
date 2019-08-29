package com.ioter.medical.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.bean.OutDetail;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.ui.adapter.OutRegisterAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OutMessageActivity extends BaseActivity {

    @BindView(R.id.tv_num)
    TextView tvNum;
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
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private OutRegisterAdapter outRegisterAdapter;
    private ArrayList<EPC> epclist = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_out_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        title.setText("出库详情");
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        outRegisterAdapter = new OutRegisterAdapter(this, "outRegister");
        listLease.setAdapter(outRegisterAdapter);

        Map<String, String> map = new HashMap<>();
        map.put("id", id);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<OutDetail>> qqDataCall = apIservice.stockoutdetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<OutDetail>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(OutMessageActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<OutDetail> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("查询失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       OutDetail detail = baseBean.getData();
                                       tvNum.setText(detail.getId());
                                       tvName.setText(detail.getDelivererName());
                                       tvTime.setText(detail.getDeliverTime());
                                       tvUser.setText(detail.getReceiverName());
                                       tvWeight.setText(detail.getReceivedWeight() + "");
                                       tvTotalWeight.setText("总重量：" + detail.getDeliverWeight() + "kg");
                                       //ToastUtil.toast("查询成功");
                                       for (int i = 0; i < detail.getDushbinViews().size(); i++) {
                                           EPC epc = new EPC();
                                           epc.setId(detail.getDushbinViews().get(i).getEpc());
                                           epc.setWasteType(detail.getDushbinViews().get(i).getWasteType());
                                           epc.setWeight(detail.getDushbinViews().get(i).getWeight());
                                           epclist.add(epc);
                                       }
                                       outRegisterAdapter.updateDatas(epclist);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
