package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.ui.BaseView;

import io.reactivex.Observable;

public interface RuleListContract {
    //Model的接口,数据请求
    interface IFeeRuleModel{
        Observable<BaseBean<FeeRule>> feeRule();
    }

    //View的接口，表明View要做的事情
    interface FeeRuleView extends BaseView {
        void feeRuleResult(BaseBean<FeeRule> baseBean);
    }
}
