package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockOut;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.module.MedOutModule;
import com.ioter.medical.presenter.MedOutPresenter;
import com.ioter.medical.presenter.contract.MedOutContract;
import com.ioter.medical.di.component.DaggerMedOutComponent;
import com.ioter.medical.ui.adapter.MedicalEnterAdapter;
import com.ioter.medical.ui.adapter.MedicalOutAdapter;
import com.ioter.medical.ui.widget.AutoListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalOutActivity extends BaseActivity<MedOutPresenter> implements MedOutContract.MedOutView {

    @BindView(R.id.btn_lease)
    Button btnLease;
    private AutoListView listLease;
    private MedicalOutAdapter medicalOutAdapter;
    private ArrayList<StockOut> epclist = new ArrayList<>();
    //下一页初始化为1
    int nextpage = 1;
    //每一页加载多少数据
    private int number = 15;
    private String TAG = "ListTag";

    @Override
    public int setLayout() {
        return R.layout.activity_medical_out;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedOutComponent.builder().appComponent(appComponent).medOutModule(new MedOutModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("医废出库");
        listLease = findViewById(R.id.list_lease);
        listLease.setPageSize(number);

        Log.d(TAG, "nextpage: "+nextpage);
        Map<String, Integer> map = new HashMap<>();
        map.put("Page", nextpage);
        map.put("Rows", number);
        mPresenter.medOut(map);

        medicalOutAdapter = new MedicalOutAdapter(this, "out");
        listLease.setAdapter(medicalOutAdapter);

        listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                nextpage++;

                Log.d(TAG, "nextpage: "+nextpage);
                Map<String, Integer> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medOut(map);
            }

            @Override
            public void onAfterScroll(int firstVisibleItem) {
            }
        });

        listLease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MedicalOutActivity.this,OutMessageActivity.class);
                intent.putExtra("id",epclist.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode ==RESULT_OK){
                nextpage = 1;
                epclist.clear();

                Map<String, Integer> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medOut(map);
            }
        }
    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        if (!ScreenUtils.Utils.isFastClick()) return;

        Intent intent = new Intent(this,OutRegisterActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    public void medOutResult(BaseBean<List<StockOut>> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0) {
                if (baseBean.getData()!=null){
                    for (int i = 0; i < baseBean.getData().size(); i++) {
                        StockOut stockOut = baseBean.getData().get(i);
                        epclist.add(stockOut);
                    }
                    //通知listview改变UI中的数据
                    medicalOutAdapter.updateDatas(epclist);
                    listLease.onLoadComplete();
                    listLease.setResultSize(baseBean.getData().size());
                }
            } else {
                ToastUtil.toast(baseBean.getMessage());
            }
        }
    }
}
