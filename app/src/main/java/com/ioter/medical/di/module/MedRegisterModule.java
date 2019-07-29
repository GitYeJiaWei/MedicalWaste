package com.ioter.medical.di.module;

import com.ioter.medical.data.MedRegisterModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MedRegisterModule {
    MedRegisterContract.MedRegisterView mView;

    //Module的构造函数，传入一个view，提供给Component
    public MedRegisterModule(MedRegisterContract.MedRegisterView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  MedRegisterContract.MedRegisterView provideView(){
        return mView;
    }

    @Provides
    public MedRegisterContract.IMedRegisterModel provideModel(ApiService apiService){
        return new MedRegisterModel(apiService);
    }
}
