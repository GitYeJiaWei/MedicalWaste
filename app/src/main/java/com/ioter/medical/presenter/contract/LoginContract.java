package com.ioter.medical.presenter.contract;


import com.ioter.medical.ui.BaseView;

import io.reactivex.Observable;

/**
 * 以往的MVP 都要定义3个接口 分别是 IModel IView IPresenter。
 * 写多了你就会发现，太特么占地方了。。。 这里介绍一种解决办法，
 * 就是引用一种契约类。说白了就是三个接口放一起。
 */
public interface LoginContract {
    //Model的接口,数据请求
    interface ILoginModel{
        Observable<Object> login(String userName, String password);
    }

    //View的接口，表明View要做的事情
    interface LoginView extends BaseView {
        void loginResult(Object baseBean);
    }
}
