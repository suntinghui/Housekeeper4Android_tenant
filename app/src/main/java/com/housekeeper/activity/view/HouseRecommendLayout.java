package com.housekeeper.activity.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.house.dto.app.HouseReleaseListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.keeper.KeeperHouseInfoPublishActivity;
import com.housekeeper.activity.keeper.KeeperReserveListActivity;
import com.housekeeper.client.Constants;
import com.housekeeper.client.net.ImageCacheManager;
import com.wufriends.housekeeper.tenant.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 10/29/15.
 * <p/>
 * 推荐房源
 */
public class HouseRecommendLayout extends LinearLayout {

    private BaseActivity context = null;

    private LinearLayout rootLayout;
    private CustomNetworkImageView headImageView;
    private TextView communityTextView;
    private TextView monthMoneyTextView;
    private TagFlowLayout flowlayout;

    public HouseRecommendLayout(BaseActivity context) {
        super(context);

        this.initView(context);
    }

    public HouseRecommendLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_house_recommend, this);

        this.rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
        this.headImageView = (CustomNetworkImageView) this.findViewById(R.id.headImageView);
        this.communityTextView = (TextView) this.findViewById(R.id.communityTextView);
        this.monthMoneyTextView = (TextView) this.findViewById(R.id.monthMoneyTextView);
        this.flowlayout = (TagFlowLayout) this.findViewById(R.id.flowlayout);
    }

    public void setData(final HouseReleaseListAppDto infoDto) {
        this.headImageView.setImageUrl(Constants.HOST_IP + infoDto.getIndexImgUrl(), ImageCacheManager.getInstance().getImageLoader());

        this.communityTextView.setText(infoDto.getAreaStr() + "  " + infoDto.getCommunity() + "   " + infoDto.getHouseType());
        this.monthMoneyTextView.setText(infoDto.getMonthMoney());
        this.flowlayout.setEnabled(false);
        this.flowlayout.setAdapter(new TagAdapter<String>(infoDto.getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tag_layout, parent, false);
                tv.setText(s);
                tv.setTextSize(11);
                return tv;
            }
        });

        this.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, KeeperHouseInfoPublishActivity.class);
                intent.putExtra("houseId", infoDto.getHouseId() + "");
                context.startActivity(intent);
            }
        });

    }

}
