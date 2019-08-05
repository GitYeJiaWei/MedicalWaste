package com.ioter.medical.di.module;

import com.ioter.medical.data.EnterMessageModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.EnterMessageContract;

import dagger.Module;
import dagger.Provides;

@Module
public class EnterMessageModule {
    EnterMessageContract.EnterMessageView mView;

    //Module的构造函数，传入一个view，提供给Component
    public EnterMessageModule(EnterMessageContract.EnterMessageView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  EnterMessageContract.EnterMessageView provideView(){
        return mView;
    }

    @Provides
    public EnterMessageContract.IEnterMessageModel provideModel(ApiService apiService){
        return new EnterMessageModel(apiService);
    }
}
