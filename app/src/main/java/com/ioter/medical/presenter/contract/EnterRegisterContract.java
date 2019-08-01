package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import java.math.BigDecimal;
import java.util.Map;

import io.reactivex.Observable;

public interface EnterRegisterContract {
    //Model的接口,数据请求
    interface IEnterRegisterModel{
        Observable<BaseBean<Object>> EnterRegister(Map<String,Object> map);
    }

    //View的接口，表明View要做的事情
    interface EnterRegisterView extends BaseView {
        void EnterRegisterResult(BaseBean<Object> baseBean);
    }
}
