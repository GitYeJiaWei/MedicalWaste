package com.ioter.medical.di.module;

import com.ioter.medical.data.MedEnterModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedEnterContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MedEnterModule {
    MedEnterContract.MedEnterView mView;

    //Module的构造函数，传入一个view，提供给Component
    public MedEnterModule(MedEnterContract.MedEnterView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  MedEnterContract.MedEnterView provideView(){
        return mView;
    }

    @Provides
    public MedEnterContract.IMedEnterModel provideModel(ApiService apiService){
        return new MedEnterModel(apiService);
    }
}
