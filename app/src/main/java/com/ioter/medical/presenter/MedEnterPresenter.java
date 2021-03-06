package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockIn;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.MedEnterContract;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MedEnterPresenter extends BasePresenter<MedEnterContract.IMedEnterModel,MedEnterContract.MedEnterView> {

    @Inject
    public MedEnterPresenter(MedEnterContract.IMedEnterModel iMedEnterModel, MedEnterContract.MedEnterView medEnterView) {
        super(iMedEnterModel, medEnterView);
    }

    public void medEnter(Map<String,Object> map){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.medEnter(map)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<List<StockIn>>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<List<StockIn>> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.medEnterResult(baseBean);
                    }
                });
    }
}
