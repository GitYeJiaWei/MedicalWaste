package com.ioter.medical.di.module;

import com.ioter.medical.data.OutRegisterModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.OutRegisterContract;

import dagger.Module;
import dagger.Provides;

@Module
public class OutRegisterModule {
    OutRegisterContract.OutRegisterView mView;

    //Module的构造函数，传入一个view，提供给Component
    public OutRegisterModule(OutRegisterContract.OutRegisterView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  OutRegisterContract.OutRegisterView provideView(){
        return mView;
    }

    @Provides
    public OutRegisterContract.IOutRegisterModel provideModel(ApiService apiService){
        return new OutRegisterModel(apiService);
    }
}
