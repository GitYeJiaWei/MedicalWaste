package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.EnterRegisterContract;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class EnterRegisterModel implements EnterRegisterContract.IEnterRegisterModel{
    private ApiService mApiService;

    public EnterRegisterModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> EnterRegister(Map<String,Object> map) {
        return mApiService.stocksave(map);
    }
}