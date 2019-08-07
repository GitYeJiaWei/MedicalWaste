package com.ioter.medical.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Code;
import com.ioter.medical.common.http.BaseUrlInterceptor;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedRegisterComponent;
import com.ioter.medical.di.module.MedRegisterModule;
import com.ioter.medical.presenter.MedRegisterPresenter;
import com.ioter.medical.presenter.contract.MedRegisterContract;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.btn_cancle)
    Button btnCancle;
    @BindView(R.id.gridview)
    GridView gridview;
    private List<Map<String, String>> dataList;
    private String WasteTypeId = null;
    private String HandOverUserId = null;

    @Override
    public int setLayout() {
        return R.layout.activity_medical_collect;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedRegisterComponent.builder().appComponent(appComponent).medRegisterModule(new MedRegisterModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        setTitle("医废登记");

        //医废类型查询
        initWasteTypes();

        tvName.setText(ACache.get(AppApplication.getApplication()).getAsString(LoginActivity.REAL_NAME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str_time = simpleDateFormat.format(date);
        tvTime.setText(str_time);
    }

    private void initWasteTypes() {
        dataList = new ArrayList<>();

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
                                       ToastUtil.toast("医废类型请求失败");
                                       finish();
                                   }
                                   if (baseBean.getCode() == 0) {
                                       //ToastUtil.toast("医废类型查询");
                                       if (baseBean.getData() != null) {
                                           dataList = (List<Map<String, String>>) baseBean.getData();
                                           String[] from = {"Id", "Name"};
                                           int[] to = {R.id.id, R.id.text};

                                           SimpleAdapter adapter = new SimpleAdapter(getApplication(), dataList, R.layout.gridview2_item, from, to);
                                           gridview.setAdapter(adapter);
                                           gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                               @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                               @Override
                                               public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                                       long arg3) {
                                                   WasteTypeId = dataList.get(arg2).get("Id").toString();
                                                   for (int i = 0; i < dataList.size(); i++) {
                                                       if (i == arg2) {
                                                           LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview.getChildAt(i)).getChildAt(0);
                                                           linearLayout.setBackground(getDrawable(R.drawable.button_back));
                                                       } else {
                                                           LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview.getChildAt(i)).getChildAt(0);
                                                           linearLayout.setBackground(getDrawable(R.drawable.back_text));
                                                       }
                                                   }
                                               }
                                           });
                                       }
                                   }else {
                                       ToastUtil.toast(baseBean.getMessage());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ToastUtil.toast("医废类型请求失败");
                               }

                               @Override
                               public void onComplete() {
                               }//订阅
                           }
                );
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
        String bar = null;
        if (barcode.contains("iotId")){
            Code code = AppApplication.getGson().fromJson(barcode, Code.class);
            bar = code.getIotId();
        }else {
            ToastUtil.toast("垃圾袋二维码");
            return;
        }

        if (bar == null){
            ToastUtil.toast("扫描失败，请重新扫描");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
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
                                   if (baseBean.getCode() == 0) {
                                       Map<String,String> map1 = (Map<String, String>) baseBean.getData();
                                       tvUser.setText(map1.get("Name"));
                                       tvRoom.setText(map1.get("Department"));
                                       HandOverUserId = map1.get("Id");
                                       ToastUtil.toast("扫描成功");
                                   }else {
                                       ToastUtil.toast(baseBean.getMessage());
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
                if(TextUtils.isEmpty(WasteTypeId)){
                    ToastUtil.toast("请选择废物类型");
                    return;
                }
                if (TextUtils.isEmpty(HandOverUserId)){
                    ToastUtil.toast("请扫描交接人二维码");
                    return;
                }

                String weight = tvWeight.getText().toString();
                //称重数据
                BigDecimal bigDecimal = new BigDecimal(weight);

                mPresenter.medRegister(HandOverUserId,bigDecimal,WasteTypeId);
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交成功：");
        builder.setMessage("是否继续提交");
        builder.setIcon(R.mipmap.ic_launcher_round);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //设置反面按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }

    @Override
    public void medRegisterResult(BaseBean<Object> baseBean) {
        if (baseBean == null) {
            ToastUtil.toast("提交失败");
           return;
        }
        if (baseBean.getCode() == 0){
            createDialog();
        }else {
            ToastUtil.toast(baseBean.getMessage());
        }
    }

}
