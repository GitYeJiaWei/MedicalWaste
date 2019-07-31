package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.MedEnterModule;
import com.ioter.medical.ui.activity.MedicalEnterActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MedEnterModule.class,dependencies = AppComponent.class)
public interface MedEnterComponent {
    void inject(MedicalEnterActivity medicalEnterActivity);
}