package com.ioter.medical.di.module;

import com.ioter.medical.data.RemindModel;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.presenter.contract.RemindContract;

import dagger.Module;
import dagger.Provides;

@Module
public class RemindModule {
    RemindContract.RemindView mView;

    //Module的构造函数，传入一个view，提供给Component
    public RemindModule(RemindContract.RemindView feeRuleView){
        this.mView = feeRuleView;
    }

    //Provides注解代表提供的参数，为构造器传进来的
    @Provides
    public  RemindContract.RemindView provideView(){
        return mView;
    }

    @Provides
    public RemindContract.IRemindModel provideModel(ApiService apiService){
        return new RemindModel(apiService);
    }
}
