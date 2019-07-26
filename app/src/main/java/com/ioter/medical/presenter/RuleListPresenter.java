package com.ioter.medical.presenter;


import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.common.rx.subscriber.ProgressSubcriber;
import com.ioter.medical.common.util.NetUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.presenter.contract.RuleListContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RuleListPresenter extends BasePresenter<RuleListContract.IFeeRuleModel,RuleListContract.FeeRuleView>{
    @Inject
    public RuleListPresenter(RuleListContract.IFeeRuleModel iFeeRuleModel, RuleListContract.FeeRuleView feeRuleView) {
        super(iFeeRuleModel, feeRuleView);
    }

    public void feeRule(){
        if (!NetUtils.isConnected(mContext)){
            ToastUtil.toast(R.string.error_network_unreachable);
            return;
        }
        mModel.feeRule()
                .subscribeOn(Schedulers.io())//访问数据在子线程
                .observeOn(AndroidSchedulers.mainThread())//拿到数据在主线程
                .subscribe(new ProgressSubcriber<BaseBean<FeeRule>>(mContext,mView) {
                    @Override
                    public void onNext(BaseBean<FeeRule> baseBean) {
                        //当Observable发生事件的时候触发
                        mView.feeRuleResult(baseBean);
                    }
                });
    }
}
