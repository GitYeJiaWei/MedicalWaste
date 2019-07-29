package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.MedCollectModule;
import com.ioter.medical.ui.activity.MedicalCollectActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MedCollectModule.class,dependencies = AppComponent.class)
public interface MedCollectComponent {
    void inject(MedicalCollectActivity medicalCollectActivity);
}