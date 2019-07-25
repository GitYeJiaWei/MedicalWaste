package com.ioter.medical.di.component;


import com.ioter.medical.di.ActivityScope;
import com.ioter.medical.di.module.LoginModule;
import com.ioter.medical.ui.activity.LoginActivity;

import dagger.Component;

/**
 * 下面的注解，代表使用LoginModule
 * 用来将@Inject和@Module联系起来的桥梁，从@Module中获取依赖并将依赖注入给@Inject
 */
@ActivityScope
@Component(modules = LoginModule.class,dependencies = AppComponent.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
