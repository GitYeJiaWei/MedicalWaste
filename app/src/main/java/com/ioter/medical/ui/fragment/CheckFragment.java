package com.ioter.medical.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.Code1;
import com.ioter.medical.common.ScreenUtils;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.data.http.ApiService;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.ui.activity.CheckMessageActivity;
import com.ioter.medical.ui.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 医废查询
 */
public class CheckFragment extends BaseFragment {
    @BindView(R.id.sp_kuqu)
    Spinner spKuqu;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.btn_lease)
    Button btnLease;
    @BindView(R.id.btn_scan)
    Button btnScan;
    private List<Map<String, String>> dataList;
    private String selected;
    private int mYear, mMonth, mDay;
    private Dialog dataPickerDialog = null;
    private int mStatus = 0;

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
        initDate();
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
                                       return;
                                   }
                                   if (baseBean.getCode() == 0) {
                                       //ToastUtil.toast("医废类型查询");
                                       if (baseBean.getData() != null) {
                                           dataList = (List<Map<String, String>>) baseBean.getData();
                                           initSpinner();
                                       }
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

    @Override
    public void myOnKeyDwon() {
        super.myOnKeyDwon();
        ((MainActivity) mActivity).ScanBarcode();
    }

    private void initSpinner() {
        /*
         * 动态添显示下来菜单的选项，可以动态添加元素
         */
        List<String> list = new ArrayList<>();
        list.add("全部类型垃圾");
        for (int i = 0; i < dataList.size(); i++) {
            list.add(dataList.get(i).get("Name"));
        }


        /*
         * 第二个参数是显示的布局
         * 第三个参数是在布局显示的位置id
         * 第四个参数是将要显示的数据
         */
        ArrayAdapter adapter2 = new ArrayAdapter(AppApplication.getApplication(), R.layout.item, R.id.text_item, list);
        spKuqu.setAdapter(adapter2);

        if (TextUtils.isEmpty(selected)) {
            selected = list.get(0);
            spKuqu.setSelection(0, true);
        }

        spKuqu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //将选择的元素显示出来
                selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initDate() {
        //初始化日期
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void showBarCode(String barcode) {
        if (barcode.contains("iotId")) {
            ToastUtil.toast("请扫描医废二维码");
        }
        if (barcode.contains("iotEPC")) {
            Code1 code1 = AppApplication.getGson().fromJson(barcode, Code1.class);
            String bar = code1.getIotEPC();
            Intent intent = new Intent(AppApplication.getApplication(), CheckMessageActivity.class);
            intent.putExtra("id", bar);
            startActivity(intent);
        }
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            if (mStatus == 0)
                SetDateTimeToTextView(tvStartTime);
            if (mStatus == 1)
                SetDateTimeToTextView(tvEndTime);
        }
    };

    void SetDateTimeToTextView(TextView txtView) {
        String newDateStr = mYear + "-" + (mMonth + 1) + "-" + mDay;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            Date date = sdf.parse(newDateStr);
            txtView.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnClick({R.id.btn_lease, R.id.btn_scan, R.id.tv_startTime, R.id.tv_endTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_lease:
                if (!ScreenUtils.Utils.isFastClick()) return;

                String WasteTypeId = null;
                if (!selected.equals("全部类型垃圾")) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).get("Name").equals(selected)) {
                            WasteTypeId = dataList.get(i).get("Id");
                        }
                    }
                }
                Intent intent = new Intent(AppApplication.getApplication(), CheckMessageActivity.class);
                intent.putExtra("WasteTypeId", WasteTypeId);
                intent.putExtra("Begin", tvStartTime.getText().toString());
                intent.putExtra("End", tvEndTime.getText().toString());
                startActivity(intent);
                break;
            case R.id.btn_scan:
                if (!ScreenUtils.Utils.isFastClick()) return;

                ((MainActivity) mActivity).ScanBarcode();
                break;
            case R.id.tv_startTime:
                if (!ScreenUtils.Utils.isFastClick()) return;

                mStatus = 0;
                dataPickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, mdateListener, mYear, mMonth, mDay);
                dataPickerDialog.show();
                break;
            case R.id.tv_endTime:
                if (!ScreenUtils.Utils.isFastClick()) return;

                mStatus = 1;
                dataPickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, mdateListener, mYear, mMonth, mDay);
                dataPickerDialog.show();
                break;
        }
    }
}
