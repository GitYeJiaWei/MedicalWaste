package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.RemindContract;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RemindPresenter extends BasePresenter<RemindContract.IRemindModel,RemindContract.RemindView> {

    @Inject
    public RemindPresenter(RemindContract.IRemindModel iRemindModel, RemindContract.RemindView remindView) {
        super(iRemindModel, remindView);
    }

    public void remind(Map<String,Integer> map){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.remind(map)
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<List<Remind>>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<List<Remind>> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.remindResult(baseBean);
                    }
                });
    }
}
