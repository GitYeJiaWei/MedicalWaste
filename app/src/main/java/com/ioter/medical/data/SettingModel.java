package com.ioter.medical.data;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.SettingContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class SettingModel implements SettingContract.ISettingModel{
    private ApiService mApiService;

    public SettingModel(ApiService apiService){
        this.mApiService = apiService;
    }

    @Override
    public Observable<BaseBean<String>> setting(String password, String newpassword, String twoPassword) {
        Map<String,String> map = new HashMap<>();
        map.put("OldPassword",password);
        map.put("ConfirmPassword",newpassword);
        map.put("NewPassword",twoPassword);
        return mApiService.setting(map);
    }
}
