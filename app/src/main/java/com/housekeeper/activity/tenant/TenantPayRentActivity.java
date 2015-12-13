package com.housekeeper.activity.tenant;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.UserAppDto;
import com.ares.house.dto.app.UserHouseListAppDto;
import com.ares.house.dto.app.UserRentAppDto;
import com.gelitenight.waveview.library.WaveHelper;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.view.CircleProgress;
import com.gelitenight.waveview.library.WaveView;
import com.housekeeper.activity.view.ConfirmPayDialog;
import com.housekeeper.client.Constants;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.TransferInfo;
import com.housekeeper.client.net.JSONRequest;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sth on 12/11/15.
 */
public class TenantPayRentActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout keeperLayout = null;
    private CircleImageView headImageView = null;
    private TextView nameTextView = null;
    private TextView dateTextView = null;
    private TextView mortgageMoneyTextView = null; // 押金
    private TextView firstMonthMoneyTextView = null; // 首付租金

    private CircleProgress circleProgress = null;
    private WaveView waveView = null;
    private WaveHelper mWaveHelper;

    private TextView monthStatusTextView = null;
    private TextView prePayTextView = null;

    private TextView monthTextView = null;

    private TextView surplusDayTextView = null;
    private ProgressBar progressBar = null;

    private TextView moneyMonthTextView = null;
    private TextView payTextView = null;

    private UserHouseListAppDto userDto;
    private UserRentAppDto appDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_payrent);

        userDto = (UserHouseListAppDto) this.getIntent().getSerializableExtra("DTO");

        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestUserRentInfo();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText(userDto.getCommunity() + " " + userDto.getHouseNum());

        keeperLayout = (LinearLayout) this.findViewById(R.id.keeperLayout);
        keeperLayout.setOnClickListener(this);

        headImageView = (CircleImageView) this.findViewById(R.id.headImageView);
        nameTextView = (TextView) this.findViewById(R.id.nameTextView);

        dateTextView = (TextView) this.findViewById(R.id.dateTextView); // 2015-01-01 至 2015-12-31
        mortgageMoneyTextView = (TextView) this.findViewById(R.id.mortgageMoneyTextView);
        firstMonthMoneyTextView = (TextView) this.findViewById(R.id.firstMonthMoneyTextView);
        surplusDayTextView = (TextView) this.findViewById(R.id.surplusDayTextView);

        circleProgress = (CircleProgress) this.findViewById(R.id.circleProgress);
        circleProgress.setType(CircleProgress.ARC);
        circleProgress.setPaintWidth(20);
        circleProgress.setSubPaintColor(Color.parseColor("#FED262"));

        waveView = (WaveView) this.findViewById(R.id.waveView);

        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);
        progressBar.incrementProgressBy(1);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        waveView = (WaveView) this.findViewById(R.id.waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setBorder(2, Color.parseColor("#88FED262"));

        monthStatusTextView = (TextView) this.findViewById(R.id.monthStatusTextView);
        prePayTextView = (TextView) this.findViewById(R.id.prePayTextView);
        monthTextView = (TextView) this.findViewById(R.id.monthTextView);
        moneyMonthTextView = (TextView) this.findViewById(R.id.moneyMonthTextView);
        payTextView = (TextView) this.findViewById(R.id.payTextView);
        payTextView.setOnClickListener(this);
    }

    private void requestUserRentInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("leaseId", userDto.getLeaseId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.LEASE_USER_RENT_INFO, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, UserRentAppDto.class);
                    AppMessageDto<UserRentAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        appDto = dto.getData();

                        responseUserRentInfo();

                    } else {
                        Toast.makeText(TenantPayRentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseUserRentInfo() {
        headImageView.setImageURL(Constants.HOST_IP + appDto.getAgentLogo());
        nameTextView.setText(appDto.getAgentUserName());

        dateTextView.setText(appDto.getBeginTimeStr() + " 至 " + appDto.getEndTimeStr());
        mortgageMoneyTextView.setText(appDto.getMortgageMoney() + " 元");
        firstMonthMoneyTextView.setText(appDto.getFirstMonthMoney() + " 元");
        moneyMonthTextView.setText(appDto.getMonthMoney());

        progressBar.setProgress(appDto.getSurplusDayPercentage());

        this.setCircleProgress(100 * appDto.getMonth() / appDto.getTotalMonth());

        waveView.setWaterLevelRatio(1.0f * appDto.getMonth() / appDto.getTotalMonth());
        waveView.setWaveColor(Color.parseColor("#66FED262"), Color.parseColor("#eeFED262"));
        mWaveHelper = new WaveHelper(waveView);
        mWaveHelper.start();

        if (appDto.getPaymentStatus() == 'd') {
            monthStatusTextView.setText("本月已付");
            prePayTextView.setVisibility(View.GONE);
            monthTextView.setText(Html.fromHtml("<font color=#23AFF5>" + appDto.getMonth() + "</font>/" + appDto.getTotalMonth()));
            surplusDayTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            payTextView.setText("本月已付");
            payTextView.setEnabled(false);
            payTextView.setBackgroundColor(Color.parseColor("#cccccc"));

        } else if (appDto.getPaymentStatus() == 'c') {
            monthStatusTextView.setText("支付确认中");
            prePayTextView.setVisibility(View.GONE);
            monthTextView.setText(Html.fromHtml("<font color=#23AFF5>" + appDto.getMonth() + "</font>/" + appDto.getTotalMonth()));
            surplusDayTextView.setVisibility(View.VISIBLE);
            surplusDayTextView.setText(Html.fromHtml("剩余<font color=#FB9B01>" + appDto.getSurplusDay() + "</font>天"));
            progressBar.setVisibility(View.VISIBLE);
            payTextView.setText("支付确认中");
            payTextView.setEnabled(false);
            payTextView.setBackgroundColor(Color.parseColor("#cccccc"));

        } else {
            if (appDto.isAtOncePay()) {
                monthStatusTextView.setText("立即支付");
                prePayTextView.setVisibility(View.GONE);
                monthTextView.setText(Html.fromHtml("<font color=#23AFF5>" + appDto.getMonth() + "</font>/" + appDto.getTotalMonth()));
                surplusDayTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                surplusDayTextView.setText("立即支付");
                payTextView.setText("立即支付");
                payTextView.setEnabled(true);

            } else {
                monthStatusTextView.setText(appDto.getPayEndTimeStr());
                prePayTextView.setVisibility(View.VISIBLE);
                monthTextView.setText(Html.fromHtml("<font color=#23AFF5>" + appDto.getMonth() + "</font>/" + appDto.getTotalMonth()));
                surplusDayTextView.setText(Html.fromHtml("剩余<font color=#FB9B01>" + appDto.getSurplusDay() + "</font>天"));
                surplusDayTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                payTextView.setText("去支付");
                payTextView.setEnabled(true);
            }
        }

    }

    private void requestPay() {
        try {
            ConfirmPayDialog dialog = new ConfirmPayDialog(this);
            dialog.setTransferInfo(new TransferInfo(appDto.getHouseId(), Double.parseDouble(appDto.getTotalPay()), Double.parseDouble(appDto.getSurplusMoney())));
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCircleProgress(final int progress) {

        new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                for (int i = 0; i <= 100; i++) {
                    if (i > progress)
                        break;

                    publishProgress(i);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                circleProgress.setmSubCurProgress(values[0]);
            }
        }.execute(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.keeperLayout: {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + appDto.getAgentTelphone()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            break;

            case R.id.payTextView: {
                this.requestPay();
            }
            break;
        }
    }
}
