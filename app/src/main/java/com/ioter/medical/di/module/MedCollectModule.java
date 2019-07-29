package com.ioter.medical.di.module;

import com.ioter.medical.data.MedCollectModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedCollectContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MedCollectModule {
    MedCollectContract.MedCollectView mView;

    //Module的构造函数，传入一个view，提供给Component
    public MedCollectModule(MedCollectContract.MedCollectView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  MedCollectContract.MedCollectView provideView(){
        return mView;
    }

    @Provides
    public MedCollectContract.IMedCollectModel provideModel(ApiService apiService){
        return new MedCollectModel(apiService);
    }
}
