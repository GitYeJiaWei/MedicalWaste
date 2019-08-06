package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import java.util.Map;

import io.reactivex.Observable;

public interface OutRegisterContract {
    //Model的接口,数据请求
    interface IOutRegisterModel{
        Observable<BaseBean<Object>> OutRegister(Map<String, Object> map);
    }

    //View的接口，表明View要做的事情
    interface OutRegisterView extends BaseView {
        void OutRegisterResult(BaseBean<Object> baseBean);
    }
}
