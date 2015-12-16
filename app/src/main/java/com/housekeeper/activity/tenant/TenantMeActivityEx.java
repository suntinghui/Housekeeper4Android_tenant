package com.housekeeper.activity.tenant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.UserAppDto;
import com.ares.house.dto.app.UserHouseListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.HQReplacePayActivity;
import com.housekeeper.activity.InvestmentActivity;
import com.housekeeper.activity.TransferHistoryActivity;
import com.housekeeper.activity.WithdrawalsActivity;
import com.housekeeper.activity.YesterdayEarningsActivity;
import com.housekeeper.activity.view.TenantMeLayout;
import com.housekeeper.client.ActivityManager;
import com.housekeeper.client.Constants;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.AdapterUtil;
import com.readystatesoftware.viewbadger.BadgeView;
import com.umeng.analytics.MobclickAgent;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sth on 12/10/15.
 */
public class TenantMeActivityEx extends BaseActivity implements View.OnClickListener {

    private LinearLayout headLayout = null; // 我
    private TextView topCardTextView = null; // 银行卡
    private TextView topRecordTextView = null; // 看房记录
    private TextView topSettingTextView = null; // 系统

    private BadgeView countBadgeView = null; // 看房人数

    private CircleImageView headImageView = null;

    private LinearLayout hqAccountLayout = null;
    private TextView totalMoneyTextView = null;
    private TextView yesterdayEarningsTextView = null; // 昨日收益：0.00元

    private TextView tipTextView = null; // 鼓鼓理财，为您创造10%的活期理财收益

    private TextView moneyTextView = null;
    private TextView hqStatusTextView = null;

    private LinearLayout contentLayout = null;

    private UserAppDto userDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_me_ex);

        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestUserHouse();
    }

    private void initView() {
        headLayout = (LinearLayout) this.findViewById(R.id.headLayout);
        headLayout.setOnClickListener(this);

        headImageView = (CircleImageView) this.findViewById(R.id.headImageView);

        topCardTextView = (TextView) this.findViewById(R.id.topCardTextView);
        topCardTextView.setOnClickListener(this);

        topRecordTextView = (TextView) this.findViewById(R.id.topRecordTextView);
        topRecordTextView.setOnClickListener(this);

        countBadgeView = new BadgeView(this, topRecordTextView);
        countBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        countBadgeView.hide();

        topSettingTextView = (TextView) this.findViewById(R.id.topSettingTextView);
        topSettingTextView.setOnClickListener(this);

        hqAccountLayout = (LinearLayout) this.findViewById(R.id.hqAccountLayout);
        hqAccountLayout.setOnClickListener(this);
        totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);
        yesterdayEarningsTextView = (TextView) this.findViewById(R.id.yesterdayEarningsTextView);

        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        tipTextView.setText(Html.fromHtml("<font color=#23AFF5>鼓鼓理财，</font><font color=#333333>为您创造10%的活期理财收益</font>"));

        moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        hqStatusTextView = (TextView) this.findViewById(R.id.hqStatusTextView);

        this.findViewById(R.id.balanceLayout).setOnClickListener(this);
        this.findViewById(R.id.recordsLayout).setOnClickListener(this);
        this.findViewById(R.id.hqLayout).setOnClickListener(this);

        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
    }

    private void requestUserHouse() {
        JSONRequest request = new JSONRequest(this, RequestEnum.LEASE_USER_HOUSE, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, UserAppDto.class);
                    AppMessageDto<UserAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        userDto = dto.getData();

                        responseUserHouse();

                    } else {
                        Toast.makeText(TenantMeActivityEx.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseUserHouse() {
        headImageView.setImageURL(Constants.HOST_IP + userDto.getLogoUrl());
        totalMoneyTextView.setText(userDto.getHqMoney());
        yesterdayEarningsTextView.setText("昨日收益：" + userDto.getHqYesterday() + " 元");
        moneyTextView.setText(userDto.getSurplusMoney());
        hqStatusTextView.setText(userDto.isAutoPay() ? "已开启" : "未开启");
        if (userDto.getReserveCount() > 0) {
            countBadgeView.setText(userDto.getReserveCount() + "");
            countBadgeView.show(true);
        } else {
            countBadgeView.hide(false);
        }

        contentLayout.removeAllViews();
        for (UserHouseListAppDto dto : userDto.getHouses()) {
            TenantMeLayout layout = new TenantMeLayout(this);
            layout.setData(dto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, AdapterUtil.dip2px(this, 20));
            contentLayout.addView(layout, params);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headLayout: {
                Intent intent = new Intent(this, TenantPersonalVerifyActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.topCardTextView: {
                Intent intent = new Intent(this, TenantCardSettingActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.topRecordTextView: {
                Intent intent = new Intent(this, TenantLookListActivity.class);
                this.startActivityForResult(intent, 0);
            }
            break;

            case R.id.topSettingTextView: {
                Intent intent = new Intent(this, TenantSettingActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.hqAccountLayout: { // 活期账户，昨日收益
                InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_HQ);
                Intent intent = new Intent(this, InvestmentActivity.class);
                intent.putExtra("type", InvestmentActivity.TYPE_HQ);
                this.startActivity(intent);
            }
            break;

            case R.id.balanceLayout: { // 账户余额
                Intent intent = new Intent(this, WithdrawalsActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.recordsLayout: { // 交易记录
                Intent intent = new Intent(this, TransferHistoryActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.hqLayout: { // 活期代扣
                if (userDto == null) {
                    this.requestUserHouse();
                    return;
                }

                Intent intent = new Intent(this, HQReplacePayActivity.class);
                intent.putExtra("OPEN", userDto.isAutoPay());
                this.startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private long exitTimeMillis = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this); // 用来保存统计数据

            for (Activity act : ActivityManager.getInstance().getAllActivity()) {
                act.finish();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
