package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.OutRegisterModule;
import com.ioter.medical.ui.activity.OutRegisterActivity;

import dagger.Component;

@ActivityScope
@Component(modules = OutRegisterModule.class,dependencies = AppComponent.class)
public interface OutRegisterComponent {
    void inject(OutRegisterActivity EntericalRegisterActivity);
}