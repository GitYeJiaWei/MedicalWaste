package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockOut;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedOutContract;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class MedOutModel implements MedOutContract.IMedOutModel{
    private ApiService mApiService;

    public MedOutModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<List<StockOut>>> medOut(Map<String,Integer> map) {
        return mApiService.stockoutlist(map);
    }
}