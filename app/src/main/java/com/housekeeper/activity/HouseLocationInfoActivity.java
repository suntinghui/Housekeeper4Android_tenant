package com.housekeeper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ares.house.dto.app.HouseReleaseInfoAppDto;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.wufriends.housekeeper.tenant.R;

/**
 * Created by sth on 12/9/15.
 */
public class HouseLocationInfoActivity extends BaseActivity implements View.OnClickListener {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_house_location_info);

        this.initView();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("房源位置");

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        initOverlay();
    }

    public void initOverlay() {
        HouseReleaseInfoAppDto appDto = (HouseReleaseInfoAppDto) getIntent().getSerializableExtra("DTO");

        // add marker overlay
        LatLng llA = new LatLng(Double.parseDouble(appDto.getLatitude()), Double.parseDouble(appDto.getLongitude()));

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(1).draggable(false);
        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

        MapStatus mMapStatus = new MapStatus.Builder().target(llA).zoom(14).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
        // 回收 bitmap 资源
        bdA.recycle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
