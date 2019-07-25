package com.ioter.medical.di.component;


import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.SettingModule;
import com.ioter.medical.ui.activity.UserActivity;

import dagger.Component;

@ActivityScope
@Component(modules = SettingModule.class,dependencies = AppComponent.class)
public interface SettingComponent {
    void inject(UserActivity userActivity);
}
