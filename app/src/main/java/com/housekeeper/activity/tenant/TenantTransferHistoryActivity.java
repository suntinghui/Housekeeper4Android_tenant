package com.housekeeper.activity.tenant;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.EarningsListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.view.YesterdayEarningsAdapter;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.client.net.ResponseErrorListener;
import com.housekeeper.utils.ActivityUtil;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 12/11/15.
 */
public class TenantTransferHistoryActivity extends BaseActivity implements View.OnClickListener {

    private YesterdayEarningsAdapter adapter = null;
    private ListView listView;
    private List<EarningsListAppDto> mList = new ArrayList<EarningsListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_transfer_history);

        this.initView();

        requesTransferHistory("正在请求数据...");
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("交易记录");

        adapter = new YesterdayEarningsAdapter(this);

        listView = (ListView) this.findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

    private void requesTransferHistory(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("type", "1"); // 1定期 2活期 3物业宝 4全部

        JSONRequest request = new JSONRequest(this, RequestEnum.WITHDRAWAL_LIST, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, EarningsListAppDto.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<List<EarningsListAppDto>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        mList.addAll(dto.getData());
                        adapter.setData(mList);
                        adapter.notifyDataSetChanged();

                        ActivityUtil.setEmptyView(TenantTransferHistoryActivity.this, listView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requesTransferHistory("正在请求数据...");
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }, new ResponseErrorListener(this) {

            @Override
            public void todo() {

            }
        });

        if (!this.addToRequestQueue(request, msg)) {

        }
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
