package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedCollectComponent;
import com.ioter.medical.di.module.MedCollectModule;
import com.ioter.medical.presenter.MedCollectPresenter;
import com.ioter.medical.presenter.contract.MedCollectContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;
import com.ioter.medical.ui.widget.AutoListView;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalCollectActivity extends BaseActivity<MedCollectPresenter> implements MedCollectContract.MedCollectView {

    @BindView(R.id.btn_lease)
    Button btnLease;
    private AutoListView listLease;
    private MedicalCollectAdapter medicalCollectAdapter;
    private ArrayList<EPC> epclist = new ArrayList<>();
    //下一页初始化为1
    int nextpage = 1;
    //每一页加载多少数据
    private int number = 15;
    private String TAG = "ListTag";

    @Override
    public int setLayout() {
        return R.layout.activity_collect;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedCollectComponent.builder().appComponent(appComponent).medCollectModule(new MedCollectModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("医废收集");

        listLease = findViewById(R.id.list_lease);
        listLease.setPageSize(number);

        Log.d(TAG, "nextpage: "+nextpage);
        Map<String, Object> map = new HashMap<>();
        map.put("Page", nextpage);
        map.put("Rows", number);
        mPresenter.medCollect(map);

        medicalCollectAdapter = new MedicalCollectAdapter(this, "collect");
        listLease.setAdapter(medicalCollectAdapter);

        listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                nextpage++;

                Log.d(TAG, "nextpage: "+nextpage);
                Map<String, Object> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medCollect(map);
            }

            @Override
            public void onAfterScroll(int firstVisibleItem) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                nextpage = 1;
                epclist.clear();

                Map<String, Object> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medCollect(map);
            }
        }
    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        Intent intent = new Intent(this, MedicalRegisterActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    public void medCollectResult(BaseBean<Object> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) baseBean.getData();
                Log.d(TAG, "medCollectResult: ");
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        EPC epc = new EPC();
                        epc.setId(list.get(i).get("Id") + "");
                        epc.setCollectionTime(list.get(i).get("CollectionTime") + "");
                        epc.setDepartmentName(list.get(i).get("DepartmentName") + "");
                        epc.setHandOverUserName(list.get(i).get("HandOverUserName") + "");
                        epclist.add(epc);
                    }
                    //通知listview改变UI中的数据
                    medicalCollectAdapter.updateDatas(epclist);
                    listLease.onLoadComplete();
                    listLease.setResultSize(list.size());
                }

            } else {
                ToastUtil.toast(baseBean.getMessage());
            }
        }
    }
}
