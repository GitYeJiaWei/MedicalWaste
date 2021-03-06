package com.ioter.medical.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.common.BitmapUtil;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.di.component.DaggerMedCollectComponent;
import com.ioter.medical.di.module.MedCollectModule;
import com.ioter.medical.presenter.MedCollectPresenter;
import com.ioter.medical.presenter.contract.MedCollectContract;
import com.ioter.medical.ui.adapter.CheckMessageAdapter;
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

public class CheckMessageActivity extends BaseActivity<MedCollectPresenter> implements MedCollectContract.MedCollectView {
    @BindView(R.id.img_qr)
    ImageView imgQr;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_room)
    TextView tvRoom;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.img_sure)
    ImageView imgSure;
    @BindView(R.id.lin_text)
    LinearLayout linText;
    @BindView(R.id.list_lease)
    AutoListView listLease;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private CheckMessageAdapter checkMessageAdapter;
    private ArrayList<WasteViewsBean> epclist = new ArrayList<>();
    //下一页初始化为1
    int nextpage = 1;
    //每一页加载多少数据
    private int number = 10;
    private String WasteTypeId = null;
    private String Begin = null;
    private String End = null;

    @Override
    public int setLayout() {
        return R.layout.activity_check_message;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
        DaggerMedCollectComponent.builder().appComponent(appComponent).medCollectModule(new MedCollectModule(this))
                .build().inject(this);
    }

    @Override
    public void init() {
        title.setText("查询结果");

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");

        if (!TextUtils.isEmpty(id)) {
            getEPC(id);
            linText.setVisibility(View.VISIBLE);
            listLease.setVisibility(View.GONE);
        } else {
            linText.setVisibility(View.GONE);
            listLease.setVisibility(View.VISIBLE);

            listLease.setPageSize(number);

            WasteTypeId = intent.getStringExtra("WasteTypeId");
            Begin = intent.getStringExtra("Begin");
            End = intent.getStringExtra("End");
            Map<String, Object> map = new HashMap<>();
            if (!TextUtils.isEmpty(WasteTypeId)) {
                map.put("WasteTypeId", WasteTypeId);
            }
            if (!TextUtils.isEmpty(Begin)) {
                map.put("Begin", Begin + " 00:00:00");
            }
            if (!TextUtils.isEmpty(End)) {
                map.put("End", End + " 23:59:59");
            }
            map.put("Page", nextpage);
            map.put("Rows", number);
            mPresenter.medCollect(map);

            checkMessageAdapter = new CheckMessageAdapter(CheckMessageActivity.this, "check");
            listLease.setAdapter(checkMessageAdapter);

            listLease.setOnLoadListener(new AutoListView.OnLoadListener() {
                @Override
                public void onLoad() {
                    nextpage++;

                    Map<String, Object> map = new HashMap<>();
                    if (!TextUtils.isEmpty(WasteTypeId)) {
                        map.put("WasteTypeId", WasteTypeId);
                    }
                    if (!TextUtils.isEmpty(Begin)) {
                        map.put("Begin", Begin + " 00:00:00");
                    }
                    if (!TextUtils.isEmpty(End)) {
                        map.put("End", End + " 23:59:59");
                    }
                    map.put("Page", nextpage);
                    map.put("Rows", number);
                    mPresenter.medCollect(map);
                }

                @Override
                public void onAfterScroll(int firstVisibleItem) {
                }
            });

        }

        linText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckMessageActivity.this);
                View view = getLayoutInflater().inflate(R.layout.scan_dialog, null);
                final ImageView imageView = view.findViewById(R.id.img_scan);
                alertDialog.setView(view);
                alertDialog.setCancelable(true);
                AppApplication.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        Create2QR2(id, imageView);
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void getEPC(String bar) {
        final Map<String, String> map = new HashMap<>();
        map.put("id", bar);

        ApiService apIservice = toretrofit().create(ApiService.class);
        Observable<BaseBean<WasteViewsBean>> qqDataCall = apIservice.wastedetail(map);
        qqDataCall.subscribeOn(Schedulers.io())//请求数据的事件发生在io线程
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更新UI
                .subscribe(new Observer<BaseBean<WasteViewsBean>>() {
                               ProgressDialog mypDialog;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   mypDialog = new ProgressDialog(CheckMessageActivity.this);
                                   mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   mypDialog.setMessage("查询中...");
                                   mypDialog.setCanceledOnTouchOutside(false);
                                   mypDialog.show();
                               }

                               @Override
                               public void onNext(BaseBean<WasteViewsBean> baseBean) {
                                   if (baseBean == null) {
                                       ToastUtil.toast("查询失败");
                                       return;
                                   }
                                   if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                                       final WasteViewsBean wb = baseBean.getData();

                                       AppApplication.getExecutorService().execute(new Runnable() {
                                           @Override
                                           public void run() {
                                               Create2QR2(wb.getId(), imgQr);
                                           }
                                       });

                                       tvType.setText(wb.getWasteTypeName());
                                       tvRoom.setText(wb.getDepartmentName());
                                       tvWeight.setText("重量：" + wb.getWeight() + "kg");
                                       if (wb.getStatus().equals("待投递")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_01);
                                       }else if (wb.getStatus().equals("已投递")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_02);
                                       }else if (wb.getStatus().equals("待收运")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_03);
                                       }else if (wb.getStatus().equals("待入库")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_04);
                                       }else if (wb.getStatus().equals("待入库确认")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_05);
                                       }else if (wb.getStatus().equals("待出库")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_06);
                                       } else if (wb.getStatus().equals("待出库确认")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_07);
                                       }else if (wb.getStatus().equals("已出库")) {
                                           imgSure.setBackgroundResource(R.mipmap.mip_08);
                                       }
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


    //生成二维码的方法
    public void Create2QR2(String urls, final ImageView imageView) {
        String uri = urls;
        int mScreenWidth = 0;
        final Bitmap bitmap;
        try {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;

            bitmap = BitmapUtil.createQRImage(uri, mScreenWidth,
                    BitmapFactory.decodeResource(getResources(), 1));//自己写的方法

            if (bitmap != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void medCollectResult(BaseBean<Object> baseBean) {
        if (baseBean != null) {
            if (baseBean.getCode() == 0 && baseBean.getData() != null) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) baseBean.getData();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        WasteViewsBean epc = new WasteViewsBean();
                        epc.setId(list.get(i).get("Id") + "");
                        epc.setWasteTypeName(list.get(i).get("WasteTypeName") + "");
                        epc.setWeight((double) list.get(i).get("Weight"));
                        epc.setStatus(list.get(i).get("Status") + "");
                        epc.setHandOverTime(list.get(i).get("HandOverTime")+"");
                        epc.setReceivedTime(list.get(i).get("ReceivedTime")+"");
                        epc.setDepartmentName(list.get(i).get("DepartmentName")+"");
                        epclist.add(epc);
                    }
                    //通知listview改变UI中的数据
                    checkMessageAdapter.updateDatas(epclist);
                    listLease.onLoadComplete();
                    listLease.setResultSize(list.size());
                }

            } else {
                ToastUtil.toast(baseBean.getMessage());
            }
        }
    }
}
