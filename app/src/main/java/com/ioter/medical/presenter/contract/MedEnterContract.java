package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import java.util.Map;

import io.reactivex.Observable;

public interface MedEnterContract {
    //Model的接口,数据请求
    interface IMedEnterModel{
        Observable<BaseBean<Object>> medEnter(Map<String, Object> map);
    }

    //View的接口，表明View要做的事情
    interface MedEnterView extends BaseView {
        void medEnterResult(BaseBean<Object> baseBean);
    }
}
