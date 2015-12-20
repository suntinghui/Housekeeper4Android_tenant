package com.housekeeper.activity.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.house.dto.app.UserHouseListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.keeper.KeeperHouseInfoPublishActivity;
import com.housekeeper.activity.tenant.TenantPayRentActivity;
import com.housekeeper.client.Constants;
import com.housekeeper.client.net.ImageCacheManager;
import com.wufriends.housekeeper.R;

/**
 * Created by sth on 12/11/15.
 */
public class TenantMeLayout extends LinearLayout {

    private BaseActivity context;
    private CustomNetworkImageView headImageView;
    private TextView nameTextView;
    private TextView positionTextView;
    private TextView monthMoneyTextView;
    private TextView tipTextView1;
    private TextView surplusDayTextView;
    private TextView tipTextView2;
    private Button payBtn;

    private UserHouseListAppDto appDto;

    public TenantMeLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public TenantMeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(final Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_tenant_me, this);

        this.headImageView = (CustomNetworkImageView) this.findViewById(R.id.headImageView);
        this.nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        this.positionTextView = (TextView) this.findViewById(R.id.positionTextView);
        this.monthMoneyTextView = (TextView) this.findViewById(R.id.monthMoneyTextView);
        this.surplusDayTextView = (TextView) this.findViewById(R.id.surplusDayTextView);
        this.tipTextView1 = (TextView) this.findViewById(R.id.tipTextView1);
        this.tipTextView2 = (TextView) this.findViewById(R.id.tipTextView2);
        this.payBtn = (Button) this.findViewById(R.id.payBtn);

        this.payBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TenantPayRentActivity.class);
                intent.putExtra("DTO", appDto);
                context.startActivity(intent);
            }
        });

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, KeeperHouseInfoPublishActivity.class);
                intent.putExtra("houseId", appDto.getHouseId() + "");
                context.startActivity(intent);
            }
        });
    }

    public void setData(UserHouseListAppDto dto) {
        this.appDto = dto;

        this.headImageView.setImageUrl(Constants.HOST_IP + dto.getIndexImg(), ImageCacheManager.getInstance().getImageLoader());
        this.nameTextView.setText(dto.getCommunity() + "  " + dto.getHouseType());
        this.positionTextView.setText(Html.fromHtml(dto.getAreaStr() + " · " + dto.getSize() + "<font color=#FDC005> · " + dto.getLeaseType() + "</font>"));
        this.monthMoneyTextView.setText(dto.getMonthMoney());

        if (dto.getStatus() == 'b') {
            this.payBtn.setText("去支付");
        } else {
            this.payBtn.setText("退租中");
        }

        if (dto.isPay()) {
            if (dto.getSurplusDay() <= 0) {
                this.tipTextView1.setText("立即支付");
                this.tipTextView1.setTextColor(Color.parseColor("#FF5E5E"));

                this.surplusDayTextView.setVisibility(View.GONE);
                this.tipTextView2.setVisibility(View.GONE);

            } else {
                this.tipTextView1.setText("最晚交租期限还剩");
                this.surplusDayTextView.setText(dto.getSurplusDay() + "");
                this.tipTextView2.setText("天");
            }

        } else {
            if (dto.getMonth() == dto.getTotalMonth()) {
                this.tipTextView1.setText("租房合同");
                this.surplusDayTextView.setText(dto.getSurplusDay() + "");
                this.tipTextView2.setText("天后截止");
            } else {
                this.tipTextView1.setText("距离下次交租日期剩");
                this.surplusDayTextView.setText(dto.getSurplusDay() + "");
                this.tipTextView2.setText("天");
            }
        }

    }

}
