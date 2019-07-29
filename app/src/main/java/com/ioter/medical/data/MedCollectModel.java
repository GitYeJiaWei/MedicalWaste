package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedCollectContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MedCollectModel implements MedCollectContract.IMedCollectModel{
    private ApiService mApiService;

    public MedCollectModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<Object>> medCollect(int page, int rows) {
        Map<String,Integer> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",rows);
        return mApiService.wastelist(map);
    }
}