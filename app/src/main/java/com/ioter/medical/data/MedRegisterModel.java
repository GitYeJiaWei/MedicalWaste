package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MedRegisterModel implements MedRegisterContract.IMedRegisterModel{
    private ApiService mApiService;

    public MedRegisterModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> medRegister(HashMap<String,Object> map) {
        return mApiService.wastesave(map);
    }
}