package com.ioter.medical.ui.fragment;

import android.view.View;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 医废查询
 */
public class CheckFragment extends BaseFragment {
    private List<Map<String, String>> dataList;
    public static CheckFragment newInstance() {
        return new CheckFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init(View view) {
        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.wastetypes();
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("请求失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0) {
                                       ToastUtil.toast("医废类型查询");
                                       if (baseBean.getData() != null) {
                                           dataList = (List<Map<String, String>>) baseBean.getData();

                                       }
                                   }else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast("请求失败");
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
    }

    @Override
    public void setBarCode(String barCode) {
    }
}
