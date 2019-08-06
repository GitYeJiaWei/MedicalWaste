package com.ioter.medical.di.module;

import com.ioter.medical.data.MedOutModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedOutContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MedOutModule {
    MedOutContract.MedOutView mView;

    //Module的构造函数，传入一个view，提供给Component
    public MedOutModule(MedOutContract.MedOutView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  MedOutContract.MedOutView provideView(){
        return mView;
    }

    @Provides
    public MedOutContract.IMedOutModel provideModel(ApiService apiService){
        return new MedOutModel(apiService);
    }
}
