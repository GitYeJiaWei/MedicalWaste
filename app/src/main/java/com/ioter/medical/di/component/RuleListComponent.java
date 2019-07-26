package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.RuleListModule;
import com.ioter.medical.ui.activity.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = RuleListModule.class,dependencies = AppComponent.class)
public interface RuleListComponent {
    void inject(MainActivity mainActivity);
}