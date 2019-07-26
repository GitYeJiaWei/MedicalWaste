package com.ioter.medical.data;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.RuleListContract;

import io.reactivex.Observable;

public class FeeRuleModel implements RuleListContract.IFeeRuleModel {
    private ApiService mApiService;

    public FeeRuleModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<FeeRule>> feeRule() {
        return mApiService.rulelist();
    }
}
