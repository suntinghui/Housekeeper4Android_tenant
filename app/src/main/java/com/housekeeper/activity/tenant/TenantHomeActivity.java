package com.housekeeper.activity.tenant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.HouseReleaseListAppDto;
import com.ares.house.dto.app.ImageAppDto;
import com.ares.house.dto.app.LinkArticle;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.HousePushIntentService;
import com.housekeeper.activity.MessageListActivity;
import com.housekeeper.activity.view.HouseRecommendLayout;
import com.housekeeper.activity.view.MediaImagePagerAdapter;
import com.housekeeper.client.Constants;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.UMengShareClient;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.ActivityUtil;
import com.housekeeper.utils.AdapterUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import cn.trinea.android.view.autoscrollviewpager.ImagePagerAdapter;

/**
 * Created by sth on 10/23/15.
 */
public class TenantHomeActivity extends BaseActivity implements View.OnClickListener {

    private PullToZoomScrollViewEx scrollView;

    private AutoScrollViewPager viewPager = null;
    private ImagePagerAdapter viewPagerAdapter = null;
    private List<ImageAppDto> imageURLList = new ArrayList<ImageAppDto>();

    private LinearLayout indicatorLayout;
    private ImageView[] indicatorImageViews = null;

    private LinearLayout wholeRentLayout;
    private LinearLayout shareRentLaout;
    private LinearLayout singleRoomLayout;
    private LinearLayout bedLayout;
    private LinearLayout landlordLayout;
    private LinearLayout keeperLayout;

    private AutoScrollViewPager mediaViewPager = null;
    private MediaImagePagerAdapter mediaViewPagerAdapter = null;
    private List<LinkArticle> mediaImageURLList = new ArrayList<LinkArticle>();

    private LinearLayout recommendLayout = null;
    private List<HouseReleaseListAppDto> mList = new ArrayList<HouseReleaseListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_home);

        this.findViewById(R.id.messageBtn).setOnClickListener(this);

        loadViewForCode();

        //requestMediaImage();

        aboutUmeng();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != mediaViewPager) {
            mediaViewPager.startAutoScroll();
        }

        if (null != viewPager) {
            viewPager.startAutoScroll();
        }

        requestRecommendList();

        requestTopImage();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != mediaViewPager) {
            mediaViewPager.stopAutoScroll();
        }

        if (null != viewPager) {
            viewPager.stopAutoScroll();
        }
    }

    private void loadViewForCode() {
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        // 背景图片
        View zoomView = LayoutInflater.from(this).inflate(R.layout.tenant_home_profile, null, false);
        scrollView.setZoomView(zoomView);

        // 下面的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.tenant_home_content, null, false);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        initView();
    }

    private void initView() {

        initViewPager();

        this.wholeRentLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.wholeRentLayout);
        this.wholeRentLayout.setOnClickListener(this);

        this.shareRentLaout = (LinearLayout) scrollView.getRootView().findViewById(R.id.shareRentLayout);
        this.shareRentLaout.setOnClickListener(this);

        this.singleRoomLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.singleRoomLayout);
        this.singleRoomLayout.setOnClickListener(this);

        this.bedLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.bedLayout);
        this.bedLayout.setOnClickListener(this);

        this.keeperLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.keeperLayout);
        this.keeperLayout.setOnClickListener(this);

        this.landlordLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.landlordLayout);
        this.landlordLayout.setOnClickListener(this);

        this.recommendLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.recommendLayout);
    }

    private void initMediaViewPager() {
        // ViewPager
        mediaViewPager = (AutoScrollViewPager) this.findViewById(R.id.mediaViewPager);
        mediaViewPager.setInterval(3000);
        mediaViewPager.setCycle(true);
        mediaViewPager.setAutoScrollDurationFactor(7.0);
        mediaViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mediaViewPager.setStopScrollWhenTouch(true);
        mediaViewPagerAdapter = new MediaImagePagerAdapter(this, mediaImageURLList);

        // viewPagerAdapter.setInfiniteLoop(true);
        mediaViewPager.setAdapter(mediaViewPagerAdapter);
        mediaViewPager.startAutoScroll();
    }

    private void initViewPager() {
        // indicator
        indicatorLayout = (LinearLayout) scrollView.getZoomView().findViewById(R.id.indicatorLayout);
        indicatorLayout.removeAllViews();

        indicatorImageViews = new ImageView[imageURLList.size()];
        for (int i = 0; i < imageURLList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            indicatorImageViews[i] = imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            indicatorLayout.addView(indicatorImageViews[i], layoutParams);
        }

        // http://www.trinea.cn/android/auto-scroll-view-pager/
        // ViewPager
        viewPager = (AutoScrollViewPager) scrollView.getZoomView().findViewById(R.id.viewPager);
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setAutoScrollDurationFactor(7.0);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        viewPager.setStopScrollWhenTouch(false);
        viewPagerAdapter = new ImagePagerAdapter(this, imageURLList);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                int position = index % imageURLList.size();
                for (int i = 0; i < imageURLList.size(); i++) {
                    if (i == position) {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                    } else {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    }

                }
            }

        });
        // viewPagerAdapter.setInfiniteLoop(true);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.startAutoScroll();

        final GestureDetector tapGestureDetector = new GestureDetector(this, new TapGestureListener());
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });

    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {

            return super.onSingleTapConfirmed(event);
        }
    }

    private void requestTopImage() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("type", "USER_INDEX");

        JSONRequest request = new JSONRequest(this, RequestEnum.TOP_IMG, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, ImageAppDto.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<List<ImageAppDto>> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        responseTopImage(dto.getData());

                    } else {
                        Toast.makeText(TenantHomeActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在发送请稍候...");
    }

    private void responseTopImage(List<ImageAppDto> list) {
        imageURLList = list;
        initViewPager();
        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setFocusable(true);
        viewPager.setFocusableInTouchMode(true);
        viewPager.requestFocus();
    }

    // 取得媒体报道图片
    private void requestMediaImage() {
        JSONRequest request = new JSONRequest(this, RequestEnum.LINK_ARTICLE, null, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, LinkArticle.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<LinkArticle>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        mediaImageURLList = dto.getData();

                        initMediaViewPager();

                        mediaViewPagerAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    // 取得媒体报道图片
    private void requestRecommendList() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cityId", "5");

        JSONRequest request = new JSONRequest(this, RequestEnum.HOUSE_RECOMMEND, map, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, HouseReleaseListAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<HouseReleaseListAppDto>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        mList = dto.getData();

                        responseRecommendList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void responseRecommendList() {
        recommendLayout.removeAllViews();

        for (HouseReleaseListAppDto infoDto : mList) {
            HouseRecommendLayout layout = new HouseRecommendLayout(this);
            layout.setData(infoDto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(AdapterUtil.dip2px(this, 15), 0, AdapterUtil.dip2px(this, 15), AdapterUtil.dip2px(this, 20));

            recommendLayout.addView(layout, params);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messageBtn: {
                Intent intent_msg = new Intent(this, MessageListActivity.class);
                startActivity(intent_msg);
            }
            break;

            case R.id.wholeRentLayout: { // 整租
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "OVERALL");
                this.startActivity(intent);
            }
            break;

            case R.id.shareRentLayout: { // 合租
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "COMBINATION");
                this.startActivity(intent);
            }
            break;

            case R.id.singleRoomLayout: { // 单间
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "SEPARATE");
                this.startActivity(intent);
            }
            break;

            case R.id.bedLayout: { // 床位
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "BUNK");
                this.startActivity(intent);
            }
            break;

            case R.id.keeperLayout: {
                Intent intent = new Intent(this, TenantDownloadLandlordActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.landlordLayout: {
                Intent intent = new Intent(this, TenantDownloadLandlordActivity.class);
                this.startActivity(intent);
            }
            break;
        }
    }

    private void aboutUmeng() {
        // UMeng
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.setAutoLocation(true);

        // 推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        mPushAgent.setDebugMode(false);
        mPushAgent.setPushIntentServiceClass(HousePushIntentService.class);

        String deviceToken = UmengRegistrar.getRegistrationId(this);
        Log.e("UMENG", "UMENG DEVICE TOKEN : " + deviceToken);
        SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
        editor.putString(Constants.DEVICETOKEN, deviceToken);
        editor.commit();

        // 解决在通知栏里面显示的始终是最新的那一条的问题，谨慎使用，以免用户看到消息过多卸载应用。
        // 合并
        mPushAgent.setMergeNotificaiton(true);

        mPushAgent.onAppStart();

        // UMeng检查更新
        checkUpdate();

        UMengShareClient.setAPPID(this);

        UMengShareClient.setAPPID(this);
    }

    // 检查更新
    private void checkUpdate() {
        // 因为友盟的更新设置是静态的参数，如果在应用中不止一次调用了检测更新的方法，而每次的设置都不一样，请在每次检测更新的函数之前先恢复默认设置再设置参数，避免在其他地方设置的参数影响到这次更新
        UmengUpdateAgent.setDefault();
        // updateOnlyWifi 布尔值true(默认)只在wifi环境下检测更新，false在所有网络环境中均检测更新。
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        // deltaUpdate 布尔值true(默认)使用增量更新，false使用全量更新。看了FAQ，貌似增量更新会可能有问题，为了保险起见，不使用增量更新
        UmengUpdateAgent.setDeltaUpdate(false);
        UmengUpdateAgent.update(this);
    }
}
