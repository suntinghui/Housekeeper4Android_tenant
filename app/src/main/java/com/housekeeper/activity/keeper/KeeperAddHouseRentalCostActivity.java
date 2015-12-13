package com.housekeeper.activity.keeper;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.HouseAddListAppDto;
import com.ares.house.dto.app.LandlordRentalFeeAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.view.SublimePickerDialog;
import com.housekeeper.activity.view.XuRadioView;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.AdapterUtil;
import com.housekeeper.utils.DateUtil;
import com.housekeeper.utils.StringUtil;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sth on 9/29/15.
 */
public class KeeperAddHouseRentalCostActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText beginTimeTextView = null;
    private EditText yearCountEditText = null;
    private EditText yearMoneyEditText = null;
    private EditText letLeaseDayEditText = null;
    private EditText heatingFeesMoneyEditText = null;
    private EditText propertyFeesMoneyEditText = null;
    private TextView violateMonthTextView = null;

    private RelativeLayout allDateLayout = null; // 所有的交租日期
    private LinearLayout dateLayout = null; // 所有的交租日期


    private XuRadioView heatingTentantRadioView = null;
    private XuRadioView heatingLandlordRadioView = null;

    private XuRadioView propertyTenantRadioView = null;
    private XuRadioView propertyLandlordRadioView = null;

    private HouseAddListAppDto infoDto = null;
    private LandlordRentalFeeAppDto feeDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_keeper_add_house_rental_cost);

        infoDto = (HouseAddListAppDto) this.getIntent().getSerializableExtra("DTO");

        this.initView();

        requestFeeInfo();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("房屋租赁费用");

        ((Button) this.findViewById(R.id.commitBtn)).setOnClickListener(this);

        this.beginTimeTextView = (EditText) this.findViewById(R.id.beginTimeTextView);
        this.beginTimeTextView.setText(DateUtil.getCurrentDate2());
        this.beginTimeTextView.setOnClickListener(this);
        this.beginTimeTextView.addTextChangedListener(this);

        this.yearCountEditText = (EditText) this.findViewById(R.id.yearCountEditText);
        this.yearCountEditText.addTextChangedListener(this);

        this.yearMoneyEditText = (EditText) this.findViewById(R.id.yearMoneyEditText);

        this.letLeaseDayEditText = (EditText) this.findViewById(R.id.letLeaseDayEditText);
        this.letLeaseDayEditText.addTextChangedListener(this);

        this.heatingFeesMoneyEditText = (EditText) this.findViewById(R.id.heatingFeesMoneyEditText);
        this.propertyFeesMoneyEditText = (EditText) this.findViewById(R.id.propertyFeesMoneyEditText);
        this.violateMonthTextView = (TextView) this.findViewById(R.id.violateMonthTextView);

        this.allDateLayout = (RelativeLayout) this.findViewById(R.id.allDateLayout);
        this.dateLayout = (LinearLayout) this.findViewById(R.id.dateLayout);

        this.heatingTentantRadioView = (XuRadioView) this.findViewById(R.id.heatingTentantRadioView);
        this.heatingTentantRadioView.setTitle("租户承担");
        this.heatingTentantRadioView.setOnClickListener(this);

        this.heatingLandlordRadioView = (XuRadioView) this.findViewById(R.id.heatingLandlordRadioView);
        this.heatingLandlordRadioView.setTitle("房东承担");
        this.heatingLandlordRadioView.setOnClickListener(this);

        this.propertyTenantRadioView = (XuRadioView) this.findViewById(R.id.propertyTenantRadioView);
        this.propertyTenantRadioView.setTitle("租户承担");
        this.propertyTenantRadioView.setOnClickListener(this);

        this.propertyLandlordRadioView = (XuRadioView) this.findViewById(R.id.propertyLandlordRadioView);
        this.propertyLandlordRadioView.setTitle("房东承担");
        this.propertyLandlordRadioView.setOnClickListener(this);

    }

    private void requestFeeInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("houseId", infoDto.getId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.HOUSE_LANDLORDRENTALFEE, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, LandlordRentalFeeAppDto.class);
                    AppMessageDto<LandlordRentalFeeAppDto> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        feeDto = dto.getData();

                        responseFeeInfo();

                    } else {
                        Toast.makeText(KeeperAddHouseRentalCostActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseFeeInfo() {
        if (feeDto.getBeginTime() == 0) {
            beginTimeTextView.setText(DateUtil.getCurrentDate2());
        } else {
            beginTimeTextView.setText(DateUtil.getDate2(feeDto.getBeginTime()));
        }

        yearCountEditText.setText(feeDto.getYearCount() + "");
        yearMoneyEditText.setText(feeDto.getYearMoney());
        letLeaseDayEditText.setText(feeDto.getLetLeaseDay() + "");
        heatingFeesMoneyEditText.setText(feeDto.getHeatingFeesMoney());
        propertyFeesMoneyEditText.setText(feeDto.getPropertyFeesMoney());
        violateMonthTextView.setText("每年扣" + feeDto.getViolateMonth() + "月租金");

        heatingTentantRadioView.setChecked(feeDto.isHeatingFees());
        heatingLandlordRadioView.setChecked(!feeDto.isHeatingFees());

        propertyTenantRadioView.setChecked(feeDto.isPropertyFees());
        propertyLandlordRadioView.setChecked(!feeDto.isPropertyFees());

        try {
            this.yearCountEditText.setSelection(this.yearCountEditText.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestSetFeeInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("houseId", infoDto.getId() + "");
        map.put("beginTime", DateUtil.string2MilliSec(beginTimeTextView.getText().toString()) + "");
        map.put("yearCount", yearCountEditText.getText().toString().trim());
        map.put("letLeaseDay", letLeaseDayEditText.getText().toString().trim());
        map.put("yearMoney", yearMoneyEditText.getText().toString().trim());
        map.put("heatingFeesMoney", heatingFeesMoneyEditText.getText().toString().trim());
        map.put("heatingFees", String.valueOf(heatingTentantRadioView.isChecked()));
        map.put("propertyFeesMoney", propertyFeesMoneyEditText.getText().toString().trim());
        map.put("propertyFees", String.valueOf(propertyTenantRadioView.isChecked()));

        JSONRequest request = new JSONRequest(this, RequestEnum.HOUSE_SETLANDLORDRENT, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(KeeperAddHouseRentalCostActivity.this, "设置成功", Toast.LENGTH_SHORT).show();

                        finish();

                    } else {
                        Toast.makeText(KeeperAddHouseRentalCostActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }


    private boolean checkValue() {
        if (TextUtils.isEmpty(this.yearCountEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入租期", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(this.yearCountEditText.getText().toString().trim()) < 1) {
            Toast.makeText(this, "租期不能少于1年", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(this.yearCountEditText.getText().toString().trim()) > 5) {
            Toast.makeText(this, "租期不能大于5年", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(this.letLeaseDayEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入让租期", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(this.yearMoneyEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入年租金", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(this.heatingFeesMoneyEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入取暖费", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(this.propertyFeesMoneyEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入物业费", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.beginTimeTextView: {
                SublimeOptions options = new SublimeOptions();
                options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
                options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER);

                final SublimePickerDialog dialog = new SublimePickerDialog(this, options);
                dialog.setCallback(new SublimePickerDialog.Callback() {
                    @Override
                    public void onCancelled() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onDateTimeRecurrenceSet(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
                        beginTimeTextView.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                    }
                });
                dialog.show();
            }
            break;

            case R.id.heatingTentantRadioView: {
                if (heatingTentantRadioView.isChecked())
                    return;

                heatingTentantRadioView.setChecked(!heatingTentantRadioView.isChecked());
                heatingLandlordRadioView.setChecked(!heatingLandlordRadioView.isChecked());
            }
            break;

            case R.id.heatingLandlordRadioView: {
                if (heatingLandlordRadioView.isChecked())
                    return;

                heatingTentantRadioView.setChecked(!heatingTentantRadioView.isChecked());
                heatingLandlordRadioView.setChecked(!heatingLandlordRadioView.isChecked());
            }
            break;

            case R.id.propertyTenantRadioView: {
                if (propertyTenantRadioView.isChecked())
                    return;

                propertyTenantRadioView.setChecked(!propertyTenantRadioView.isChecked());
                propertyLandlordRadioView.setChecked(!propertyLandlordRadioView.isChecked());
            }
            break;

            case R.id.propertyLandlordRadioView: {
                if (propertyLandlordRadioView.isChecked())
                    return;

                propertyTenantRadioView.setChecked(!propertyTenantRadioView.isChecked());
                propertyLandlordRadioView.setChecked(!propertyLandlordRadioView.isChecked());
            }
            break;

            case R.id.commitBtn:
                if (checkValue()) {
                    requestSetFeeInfo();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            int yearCount = Integer.parseInt(this.yearCountEditText.getText().toString().trim());
            int letLeaseDay = Integer.parseInt(this.letLeaseDayEditText.getText().toString().trim());

            if (yearCount < 6 && yearCount > 0) {
                ArrayList<String> list = DateUtil.getYaoYAO(beginTimeTextView.getText().toString(), yearCount, letLeaseDay);

                dateLayout.removeAllViews();

                for (String str : list) {
                    TextView textView = new TextView(this);
                    textView.setText(str);
                    textView.setTextSize(13);
                    textView.setTextColor(getResources().getColor(R.color.blueme));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, AdapterUtil.dip2px(this, 10), 0, 0);
                    dateLayout.addView(textView, params);
                }

                allDateLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();

            allDateLayout.setVisibility(View.GONE);
        }
    }
}
