package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Detail;
import com.ioter.medical.ui.BaseView;

import java.util.Map;

import io.reactivex.Observable;

public interface EnterMessageContract {
    //Model的接口,数据请求
    interface IEnterMessageModel{
        Observable<BaseBean<Detail>> EnterMessage(Map<String, Object> map);
    }

    //View的接口，表明View要做的事情
    interface EnterMessageView extends BaseView {
        void EnterMessageResult(BaseBean<Detail> baseBean);
    }
}
