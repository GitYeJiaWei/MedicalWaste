package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MedRegisterModel implements MedRegisterContract.IMedRegisterModel{
    private ApiService mApiService;

    public MedRegisterModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> medRegister(String HandOverUserId, BigDecimal Weight, String WasteTypeId) {
        Map<String,Object> map = new HashMap<>();
        map.put("HandOverUserId",HandOverUserId);
        map.put("Weight",Weight);
        map.put("WasteTypeId",WasteTypeId);
        return mApiService.wastesave(map);
    }
}