package com.ioter.medical.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.TextureView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.EPC;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CheckMessageActivity extends BaseActivity {
    private ArrayList<EPC> epclist = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_check_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    @Override
    public void init() {
        setTitle("查询结果");
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if (!TextUtils.isEmpty(id)){
            getEPC(id);
        }else {
            String WasteTypeId = intent.getStringExtra("WasteTypeId");
            String Begin = intent.getStringExtra("Begin");
            String End = intent.getStringExtra("End");
            Map<String,Object> map = new HashMap<>();
            if (TextUtils.isEmpty(WasteTypeId)){
                map.put("WasteTypeId",WasteTypeId);
            }
            if (TextUtils.isEmpty(Begin)){
                map.put("Begin",Begin);
            }
            if (TextUtils.isEmpty(End)){
                map.put("End",End);
            }
            //map.put("")

        }
    }

    private void getEPCList(Map<String,Object> map){
        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.wastelist(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("查询失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData()!=null) {
                                       EPC epc = new EPC();
                                       /*epc.setId(baseBean.getData().getId());
                                       epc.setDepartmentName(baseBean.getData().getDepartmentName());
                                       epc.setWasteType(baseBean.getData().getWasteType());
                                       epc.setWeight(baseBean.getData().getWeight());
                                       epclist.add(epc);*/

                                       //medicalCollectAdapter.updateDatas(epclist);

                                       ToastUtil.toast("查询成功");
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
    }

    private void getEPC(String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<WasteViewsBean>> qqDataCall = apIservice.wastedetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<WasteViewsBean>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(BaseBean<WasteViewsBean> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("查询失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData()!=null) {
                                       EPC epc = new EPC();
                                       epc.setId(baseBean.getData().getId());
                                       epc.setDepartmentName(baseBean.getData().getDepartmentName());
                                       epc.setWasteType(baseBean.getData().getWasteType());
                                       epc.setWeight(baseBean.getData().getWeight());
                                       epclist.add(epc);

                                       //medicalCollectAdapter.updateDatas(epclist);

                                       ToastUtil.toast("查询成功");
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
    }
}
