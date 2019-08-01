package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockIn;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedEnterComponent;
import com.ioter.medical.di.module.MedEnterModule;
import com.ioter.medical.presenter.MedEnterPresenter;
import com.ioter.medical.presenter.contract.MedEnterContract;
import com.ioter.medical.ui.adapter.MedicalEnterAdapter;
import com.ioter.medical.ui.widget.AutoListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalEnterActivity extends BaseActivity<MedEnterPresenter> implements MedEnterContract.MedEnterView {

    @BindView(R.id.btn_lease)
    Button btnLease;
    private AutoListView listLease;
    private MedicalEnterAdapter medicalEnterAdapter;
    private ArrayList<StockIn> epclist = new ArrayList<>();
    //下一页初始化为1
    int nextpage = 1;
    //每一页加载多少数据
    private int number = 10;
    private String TAG = "ListTag";

    @Override
    public int setLayout() {
        return R.layout.activity_medical_enter;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedEnterComponent.builder().appComponent(appComponent).medEnterModule(new MedEnterModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("医废入库");
        listLease = findViewById(R.id.list_lease);
        listLease.setPageSize(number);

        Log.d(TAG, "nextpage: "+nextpage);
        Map<String, Object> map = new HashMap<>();
        map.put("Page", nextpage);
        map.put("Rows", number);
        mPresenter.medEnter(map);

        medicalEnterAdapter = new MedicalEnterAdapter(this, "enter");
        listLease.setAdapter(medicalEnterAdapter);

        listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                nextpage++;

                Log.d(TAG, "nextpage: "+nextpage);
                Map<String, Object> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medEnter(map);
            }

            @Override
            public void onAfterScroll(int firstVisibleItem) {
            }
        });

        listLease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.toast("获取该行的值：" + epclist.get(position).getId());
                Intent intent = new Intent(MedicalEnterActivity.this,EnterMessageActivity.class);
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

                Map<String, Object> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medEnter(map);
            }
        }
    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        Intent intent = new Intent(this,EnterRegisterActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    public void medEnterResult(BaseBean<List<StockIn>> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0) {
                if (baseBean.getData()!=null){
                    for (int i = 0; i < baseBean.getData().size(); i++) {
                        StockIn stockIn = baseBean.getData().get(i);
                        epclist.add(stockIn);
                    }
                    //通知listview改变UI中的数据
                    medicalEnterAdapter.updateDatas(epclist);
                    listLease.onLoadComplete();
                    listLease.setResultSize(baseBean.getData().size());
                }

            } else {
                ToastUtil.toast(baseBean.getMessage());
            }
        }
    }

}
