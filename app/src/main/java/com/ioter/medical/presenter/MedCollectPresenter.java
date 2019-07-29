package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.MedCollectContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MedCollectPresenter extends BasePresenter<MedCollectContract.IMedCollectModel,MedCollectContract.MedCollectView> {

    @Inject
    public MedCollectPresenter(MedCollectContract.IMedCollectModel iMedCollectModel, MedCollectContract.MedCollectView medCollectView) {
        super(iMedCollectModel, medCollectView);
    }

    public void medCollect(int page, int rows){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.medCollect(page,rows)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<Object>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<Object> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.medCollectResult(baseBean);
                    }
                });
    }
}
