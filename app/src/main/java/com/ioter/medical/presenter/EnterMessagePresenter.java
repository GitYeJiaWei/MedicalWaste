package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Detail;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.EnterMessageContract;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EnterMessagePresenter extends BasePresenter<EnterMessageContract.IEnterMessageModel,EnterMessageContract.EnterMessageView> {

    @Inject
    public EnterMessagePresenter(EnterMessageContract.IEnterMessageModel iEnterMessageModel, EnterMessageContract.EnterMessageView EnterMessageView) {
        super(iEnterMessageModel, EnterMessageView);
    }

    public void EnterMessage(Map<String,Object> map){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.EnterMessage(map)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<Detail>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<Detail> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.EnterMessageResult(baseBean);
                    }
                });
    }
}
