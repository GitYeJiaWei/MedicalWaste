package com.ioter.medical.di.module;

import com.ioter.medical.data.EnterRegisterModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.EnterRegisterContract;

import dagger.Module;
import dagger.Provides;

@Module
public class EnterRegisterModule {
    EnterRegisterContract.EnterRegisterView mView;

    //Module的构造函数，传入一个view，提供给Component
    public EnterRegisterModule(EnterRegisterContract.EnterRegisterView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  EnterRegisterContract.EnterRegisterView provideView(){
        return mView;
    }

    @Provides
    public EnterRegisterContract.IEnterRegisterModel provideModel(ApiService apiService){
        return new EnterRegisterModel(apiService);
    }
}
