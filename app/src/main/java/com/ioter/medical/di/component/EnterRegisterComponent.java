package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.EnterRegisterModule;
import com.ioter.medical.ui.activity.EnterRegisterActivity;

import dagger.Component;

@ActivityScope
@Component(modules = EnterRegisterModule.class,dependencies = AppComponent.class)
public interface EnterRegisterComponent {
    void inject(EnterRegisterActivity EntericalRegisterActivity);
}