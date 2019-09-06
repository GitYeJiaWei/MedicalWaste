package com.ioter.medical.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerRemindComponent;
import com.ioter.medical.di.module.RemindModule;
import com.ioter.medical.presenter.RemindPresenter;
import com.ioter.medical.presenter.contract.RemindContract;
import com.ioter.medical.ui.adapter.RemindMessageAdapter;
import com.ioter.medical.ui.widget.AutoListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RemindActivity extends BaseActivity<RemindPresenter> implements RemindContract.RemindView {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //下一页初始化为1
    int nextpage = 1;
    @BindView(R.id.list_lease)
    AutoListView listLease;
    //每一页加载多少数据
    private int number = 10;
    private ArrayList<Remind> epclist = new ArrayList<>();
    private RemindMessageAdapter remindMessageAdapter;

    @Override
    public int setLayout() {
        return R.layout.activity_remind;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerRemindComponent.builder().appComponent(appComponent).remindModule(new RemindModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        title.setText("消息提醒");
        Map<String, Integer> map = new HashMap<>();
        map.put("Page", nextpage);
        map.put("Rows", number);
        mPresenter.medOut(map);

        remindMessageAdapter = new RemindMessageAdapter(RemindActivity.this, "remind");
        listLease.setAdapter(remindMessageAdapter);

        listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                nextpage++;

                Map<String, Integer> map = new HashMap<>();
                map.put("Page", nextpage);
                map.put("Rows", number);
                mPresenter.medOut(map);
            }

            @Override
            public void onAfterScroll(int firstVisibleItem) {
            }
        });

        listLease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                read(epclist.get(position).getId());
            }
        });
    }

    private void read(String id){
        final Map<String, String> map = new HashMap<>();
        map.put("messageId", id);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean> qqDataCall = apIservice.SetRead(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(RemindActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("读取中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("读取失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       ToastUtil.toast("读取成功!");
                                   } else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   mypDialog.cancel();
                                   ToastUtil.toast(e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                                   mypDialog.cancel();
                               }//订阅
                           }
                );
    }

    @Override
    public void remindResult(BaseBean<List<Remind>> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                //通知listview改变UI中的数据
                remindMessageAdapter.updateDatas(epclist);
                listLease.onLoadComplete();
                listLease.setResultSize(baseBean.getData().size());
            }
        } else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }


}
