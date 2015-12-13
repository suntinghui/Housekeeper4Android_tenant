package com.housekeeper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.UserAppDto;
import com.housekeeper.activity.gesture.GestureLockSetupActivity;
import com.housekeeper.activity.gesture.GestureLockUtil;
import com.housekeeper.activity.gesture.GestureLockVerifyActivity;
import com.housekeeper.activity.tenant.TenantMeActivityEx;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.wufriends.housekeeper.tenant.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

/**
 * Created by sth on 12/10/15.
 * <p>
 * 活期代扣
 */
public class HQReplacePayActivity extends BaseActivity implements View.OnClickListener {

    private Button toggleGestureLockBtn; // 开启或关闭手势密码

    private boolean open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_hq_replace_pay);

        this.initView();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setOnClickListener(this);

        this.findViewById(R.id.toggleGestureLockLayout).setOnClickListener(this);

        open = this.getIntent().getBooleanExtra("OPEN", false);
        if (open) {
            // 如果已经设置了手势密码
            toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
        } else {
            toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
        }
    }

    private void requestSetOpen() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("open", String.valueOf(open));

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_SET_AUTOPAY, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(HQReplacePayActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(HQReplacePayActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.toggleGestureLockBtn:
            case R.id.toggleGestureLockLayout: {
                open = !open;

                if (open) {
                    // 如果已经设置了手势密码
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
                } else {
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
                }

                requestSetOpen();
            }
            break;
        }
    }
}
