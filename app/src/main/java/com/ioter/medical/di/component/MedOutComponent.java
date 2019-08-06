package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.MedOutModule;
import com.ioter.medical.ui.activity.MedicalOutActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MedOutModule.class,dependencies = AppComponent.class)
public interface MedOutComponent {
    void inject(MedicalOutActivity medicalOutActivity);
}