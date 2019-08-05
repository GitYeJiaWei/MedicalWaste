package com.ioter.medical.di.component;

import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.EnterMessageModule;
import com.ioter.medical.ui.activity.EnterMessageActivity;

import dagger.Component;

@ActivityScope
@Component(modules = EnterMessageModule.class,dependencies = AppComponent.class)
public interface EnterMessageComponent {
    void inject(EnterMessageActivity EntericalMessageActivity);
}