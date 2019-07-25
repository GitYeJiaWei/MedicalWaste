package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import io.reactivex.Observable;

public interface SettingContract {
    //Model的接口,数据请求
    interface ISettingModel{
        Observable<BaseBean<String>> setting(String password, String newpassword, String twoPassword);
    }

    //View的接口，表明View要做的事情
    interface SettingView extends BaseView {
        void settingResult(BaseBean<String> baseBean);
    }
}
