package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockOut;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.MedOutContract;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MedOutPresenter extends BasePresenter<MedOutContract.IMedOutModel,MedOutContract.MedOutView> {

    @Inject
    public MedOutPresenter(MedOutContract.IMedOutModel iMedOutModel, MedOutContract.MedOutView medOutView) {
        super(iMedOutModel, medOutView);
    }

    public void medOut(Map<String,Integer> map){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.medOut(map)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<List<StockOut>>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<List<StockOut>> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.medOutResult(baseBean);
                    }
                });
    }
}
