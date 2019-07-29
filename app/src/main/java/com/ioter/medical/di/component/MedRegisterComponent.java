package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.MedRegisterModule;
import com.ioter.medical.ui.activity.MedicalRegisterActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MedRegisterModule.class,dependencies = AppComponent.class)
public interface MedRegisterComponent {
    void inject(MedicalRegisterActivity medicalRegisterActivity);
}