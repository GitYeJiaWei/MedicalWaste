package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.StockOut;
import com.ioter.medical.ui.BaseView;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface MedOutContract {
    //Model的接口,数据请求
    interface IMedOutModel{
        Observable<BaseBean<List<StockOut>>> medOut(Map<String, Integer> map);
    }

    //View的接口，表明View要做的事情
    interface MedOutView extends BaseView {
        void medOutResult(BaseBean<List<StockOut>> baseBean);
    }
}
