package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.ui.BaseView;

import io.reactivex.Observable;

public interface MedCollectContract {
    //Model的接口,数据请求
    interface IMedCollectModel{
        Observable<BaseBean<Object>> medCollect(int page, int rows);
    }

    //View的接口，表明View要做的事情
    interface MedCollectView extends BaseView {
        void medCollectResult(BaseBean<Object> baseBean);
    }
}
