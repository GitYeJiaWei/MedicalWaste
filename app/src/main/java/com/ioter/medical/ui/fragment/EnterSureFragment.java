package com.ioter.medical.ui.fragment;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockIn;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedEnterComponent;
import com.ioter.medical.di.module.MedEnterModule;
import com.ioter.medical.presenter.MedEnterPresenter;
import com.ioter.medical.presenter.contract.MedEnterContract;
import com.ioter.medical.ui.activity.EnterMessageActivity;
import com.ioter.medical.ui.adapter.MedicalEnterAdapter;
import com.ioter.medical.ui.widget.AutoListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ioter.medical.AppApplication.TAG;

public class EnterSureFragment extends BaseFragment<MedEnterPresenter> implements MedEnterContract.MedEnterView {

    private AutoListView listLease;
    private MedicalEnterAdapter medicalEnterAdapter;
    private ArrayList<StockIn> epclist = new ArrayList<>();
    //下一页初始化为1
    int nextpage = 1;
    //每一页加载多少数据
    private int number = 10;
    private int Status = 2;
    private boolean isPrepared = false;

    public static EnterSureFragment newInstance() {
        return new EnterSureFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_enter_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedEnterComponent.builder().appComponent(appComponent).medEnterModule(new MedEnterModule(this))
                .build().inject(this);
    }

    @Override
    public void init(View view) {
        Log.d(TAG, "init已确认: "+isPrepared);
        isPrepared = true;

        listLease = view.findViewById(R.id.list_lease);
        listLease.setPageSize(number);

        Log.d(TAG, "nextpage: " + nextpage);
        Map<String, Object> map = new HashMap<>();
        map.put("Status", Status);
        map.put("Page", nextpage);
        map.put("Rows", number);
        mPresenter.medEnter(map);

        medicalEnterAdapter = new MedicalEnterAdapter(getActivity(), "enterCheck");
        listLease.setAdapter(medicalEnterAdapter);

        listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                nextpage++;

                Log.d(TAG, "nextpage: " + nextpage);
                Map<String, Object> map = new HashMap<>();
                map.put("Status", Status);
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
                if (epclist.size()==0){
                    return;
                }
                Intent intent = new Intent(getActivity(), EnterMessageActivity.class);
                intent.putExtra("id", epclist.get(position).getId());
                intent.putExtra("state", "EnterSure");

                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleTouser为true表示当前界面正在展示（fragment滑动的时候调用）
        Log.d(TAG, "setUserVisibleHint已确认: "+isVisibleToUser +"isPrepared已确认"+isPrepared);
        if (isPrepared && isVisibleToUser) {
            //加载数据
            nextpage = 1;
            epclist.clear();

            Map<String, Object> map = new HashMap<>();
            map.put("Status",Status);
            map.put("Page", nextpage);
            map.put("Rows", number);
            mPresenter.medEnter(map);
        }
    }

    @Override
    public void medEnterResult(BaseBean<List<StockIn>> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0) {
                if (baseBean.getData() != null) {
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

