package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.SettingContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingPresenter extends BasePresenter<SettingContract.ISettingModel,SettingContract.SettingView> {
    @Inject
    public SettingPresenter(SettingContract.ISettingModel iSettingModel, SettingContract.SettingView settingView) {
        super(iSettingModel, settingView);
    }

    public void setting(String passord,String newpassord,String twoPassword){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.setting(passord,newpassord,twoPassword)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<String>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<String> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.settingResult(baseBean);
                    }
                });
    }
}
