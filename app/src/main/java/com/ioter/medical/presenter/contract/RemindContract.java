package com.ioter.medical.presenter.contract;


import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.ui.BaseView;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface RemindContract {
    //Model的接口,数据请求
    interface IRemindModel{
        Observable<BaseBean<List<Remind>>> remind(Map<String, Integer> map);
    }

    //View的接口，表明View要做的事情
    interface RemindView extends BaseView {
        void remindResult(BaseBean<List<Remind>> baseBean);
    }
}
