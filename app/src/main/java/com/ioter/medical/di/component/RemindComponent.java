package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.RemindModule;
import com.ioter.medical.ui.activity.MedicalOutActivity;
import com.ioter.medical.ui.activity.RemindActivity;

import dagger.Component;

@ActivityScope
@Component(modules = RemindModule.class,dependencies = AppComponent.class)
public interface RemindComponent {
    void inject(RemindActivity remindActivity);
}