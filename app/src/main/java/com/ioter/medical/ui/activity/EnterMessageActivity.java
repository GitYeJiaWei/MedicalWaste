package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Detail;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerEnterMessageComponent;
import com.ioter.medical.di.module.EnterMessageModule;
import com.ioter.medical.presenter.EnterMessagePresenter;
import com.ioter.medical.presenter.contract.EnterMessageContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EnterMessageActivity extends BaseActivity<EnterMessagePresenter> implements EnterMessageContract.EnterMessageView {

    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_epc)
    TextView tvEpc;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_totalWeight)
    TextView tvTotalWeight;
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    @BindView(R.id.message_enter)
    LinearLayout messageEnter;
    private MedicalCollectAdapter medicalCollectAdapter;
    private ArrayList<EPC> epclist = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_enter_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerEnterMessageComponent.builder().appComponent(appComponent).enterMessageModule(new EnterMessageModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("入库详情");
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String state = intent.getStringExtra("state");
        if (state.equals("MedicalEnter") ||state.equals("EnterSure")) {
            btnBack.setVisibility(View.VISIBLE);
            messageEnter.setVisibility(View.GONE);
        }else if (state.equals("EnterCheck")){
            btnBack.setVisibility(View.GONE);
            messageEnter.setVisibility(View.VISIBLE);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        mPresenter.EnterMessage(map);

        medicalCollectAdapter = new MedicalCollectAdapter(this, "enterMessage");
        listLease.setAdapter(medicalCollectAdapter);
    }

    @Override
    public void EnterMessageResult(BaseBean<Detail> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("查询失败");
            finish();
        }
        if (baseBean.getCode() == 0) {
            if (baseBean.getData() != null) {
                Detail detail = baseBean.getData();
                tvNum.setText(detail.getId());
                tvName.setText(detail.getDelivererName());
                tvTime.setText(detail.getDeliverTime());
                tvUser.setText(detail.getReceiverName());
                tvEpc.setText(detail.getDushbinEpc());
                tvWeight.setText(detail.getReceivedWeight() + "");
                tvTotalWeight.setText(detail.getDeliverWeight() + "");

                for (int i = 0; i < detail.getWasteViews().size(); i++) {
                    EPC epc = new EPC();
                    epc.setId(detail.getWasteViews().get(i).getId());
                    epc.setDepartmentName(detail.getWasteViews().get(i).getDepartmentName());
                    epc.setWasteType(detail.getWasteViews().get(i).getWasteType());
                    epc.setWeight(detail.getWasteViews().get(i).getWeight());
                    epclist.add(epc);
                }
                medicalCollectAdapter.updateDatas(epclist);
            }
        }
    }


    @OnClick({R.id.btn_back, R.id.btn_commit, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_commit:
                String id = tvNum.getText().toString().trim();
                if (TextUtils.isEmpty(id)) {
                    ToastUtil.toast("确认入库失败");
                    return;
                }
                Map<String,String> map = new HashMap<>();
                map.put("id",id);

                ApiService apIservice = toretrofit().create(ApiService.class);
                Observable<BaseBean<Object>> qqDataCall = apIservice.stockinconfirm(map);
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
                                               ToastUtil.toast("确认入库成功");
                                               finish();
                                           }
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           ToastUtil.toast("确认入库失败");
                                       }

                                       @Override
                                       public void onComplete() {
                                       }//订阅
                                   }
                        );
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}
