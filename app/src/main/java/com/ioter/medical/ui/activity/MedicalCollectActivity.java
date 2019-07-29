package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedCollectComponent;
import com.ioter.medical.di.module.MedCollectModule;
import com.ioter.medical.presenter.MedCollectPresenter;
import com.ioter.medical.presenter.contract.MedCollectContract;
import com.ioter.medical.ui.adapter.MedicalCollectAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicalCollectActivity extends BaseActivity<MedCollectPresenter> implements MedCollectContract.MedCollectView{


    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_lease)
    Button btnLease;
    private MedicalCollectAdapter medicalCollectAdapter;

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
        medicalCollectAdapter = new MedicalCollectAdapter(this,"collect");
        listLease.setAdapter(medicalCollectAdapter);

        mPresenter.medCollect(1,1);
    }


    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
        startActivity(new Intent(this,MedicalRegisterActivity.class));
    }

    @Override
    public void medCollectResult(BaseBean<Object> baseBean) {
        if (baseBean !=null){

        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
    }
}
