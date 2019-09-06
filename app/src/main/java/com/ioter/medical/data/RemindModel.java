package com.ioter.medical.data;

import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.RemindContract;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class RemindModel implements RemindContract.IRemindModel{
    private ApiService mApiService;

    public RemindModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<List<Remind>>> remind(Map<String,Integer> map) {
        return mApiService.getallmessage(map);
    }
}