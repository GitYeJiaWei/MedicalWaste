package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import java.util.Date;
import java.util.Map;

import io.reactivex.Observable;

public interface MedCollectContract {
    //Model的接口,数据请求
    interface IMedCollectModel{
        Observable<BaseBean<Object>> medCollect(Map<String,Object> map);
    }

    //View的接口，表明View要做的事情
    interface MedCollectView extends BaseView {
        void medCollectResult(BaseBean<Object> baseBean);
    }
}
