package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public interface MedRegisterContract {
    //Model的接口,数据请求
    interface IMedRegisterModel{
        Observable<BaseBean<Object>> medRegister(String HandOverUserId, BigDecimal Weight,String WasteTypeId);
    }

    //View的接口，表明View要做的事情
    interface MedRegisterView extends BaseView {
        void medRegisterResult(BaseBean<Object> baseBean);
    }
}
