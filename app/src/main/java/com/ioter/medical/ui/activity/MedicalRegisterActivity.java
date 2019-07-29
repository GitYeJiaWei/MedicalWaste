package com.ioter.medical.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.common.http.BaseUrlInterceptor;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.SoundManage;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.presenter.MedRegisterPresenter;
import com.ioter.medical.presenter.contract.MedRegisterContract;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicalRegisterActivity extends BaseActivity<MedRegisterPresenter> implements MedRegisterContract.MedRegisterView {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_room)
    TextView tvRoom;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.radio1)
    RadioGroup radio1;
    @BindView(R.id.radio2)
    RadioGroup radio2;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;

    @Override
    public int setLayout() {
        return R.layout.activity_medical_collect;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        setTitle("医废登记");
        radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.garbage_infected:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_infected);
                        break;
                    case R.id.garbage_medical:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_medical);
                        break;
                    case R.id.garbage_damaging:
                        radio2.clearCheck();
                        radio1.check(R.id.garbage_damaging);
                        break;
                }
            }
        });

        radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.garbage_chemical:
                        radio1.clearCheck();
                        radio2.check(R.id.garbage_chemical);
                        break;
                    case R.id.garbage_pathological:
                        radio1.clearCheck();
                        radio2.check(R.id.garbage_pathological);
                        break;
                }
            }
        });

        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.USER_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280) {
            if (event.getRepeatCount() == 0) {
                ScanBarcode();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showBarCode(String barcode) {
        super.showBarCode(barcode);
        tvUser.setText(barcode);

        Map<String, String> map = new HashMap<>();
        map.put("id", barcode);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //添加拦截器，自动追加参数
        builder.addInterceptor(new BaseUrlInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                //设置基础的URL
                .baseUrl(ApiService.BASE_URL)
                //设置内容格式,这种对应的数据返回值是Gson类型，需要导包
                .addConverterFactory(GsonConverterFactory.create())
                //设置支持RxJava，应用observable观察者，需要导包
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();

        ApiService apIservice = retrofit.create(ApiService.class);
        Observable<BaseBean<Object>> qqDataCall = apIservice.getuser(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<Object>>() {
                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(BaseBean<Object> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("扫描失败");
                                       return;
                                   }
                                  if (baseBean.getCode()==0){
                                      ToastUtil.toast("扫描成功");
                                  }

                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast("扫描失败");
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }

                );
    }

    @OnClick({R.id.btn_commit, R.id.btn_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                HashMap<String,Object> map = new HashMap<>();
                map.put("","");
                mPresenter.medRegister(map);
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    @Override
    public void medRegisterResult(BaseBean<Object> baseBean) {
        if (baseBean != null){

        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
    }
}
