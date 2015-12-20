package com.housekeeper.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.MessageListAppDto;
import com.housekeeper.activity.tenant.TenantMainActivity;
import com.housekeeper.activity.view.MessageShareDialog;
import com.housekeeper.activity.view.NetErrorDialog;
import com.housekeeper.activity.view.NoZoomControllWebView;
import com.housekeeper.client.ActivityManager;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.AdapterUtil;
import com.housekeeper.utils.JsonUtil;
import com.housekeeper.utils.NetUtil;
import com.wufriends.housekeeper.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

public class ShowShareWebViewActivity extends BaseActivity implements OnClickListener {

    private Button backBtn = null;
    private Button shareBtn = null;

    private TextView titleTextView = null;
    private NoZoomControllWebView webView = null;

    private MessageListAppDto dto = null;

    private boolean hasShare = false; // 是否已经派奖

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_webview);

        String title = this.getIntent().getStringExtra("title");
        String url = this.getIntent().getStringExtra("url");
        dto = (MessageListAppDto) this.getIntent().getSerializableExtra("DTO");

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        shareBtn = (Button) this.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(this);

        // 是否显示分享，默认不显示
        boolean showShareBtn = this.getIntent().getBooleanExtra("SHOW_SHARE", false);
        shareBtn.setVisibility(showShareBtn ? View.VISIBLE : View.GONE);

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        webView = (NoZoomControllWebView) this.findViewById(R.id.webview);
        WebSettings setting = webView.getSettings();

        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);

        setting.setSupportZoom(true);
        setting.setLoadsImagesAutomatically(true);

        setting.setBuiltInZoomControls(true);

        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);

        webView.loadUrl(url);

        new ShowShareTask().execute();
    }

    public void onResume() {
        super.onResume();

        if (!NetUtil.isNetworkAvailable(this)) {
            NetErrorDialog.getInstance().show(this);
        }

        if (hasShare) {
            showSuccessTip();

            hasShare = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.backAction();
                break;

            case R.id.shareBtn: {
                MessageShareDialog dialog = new MessageShareDialog(this, dto);
                dialog.setOnStartShareListener(new MessageShareDialog.OnStartShareListener() {
                    @Override
                    public void startShare() {
                        requestShareMessage();
                    }
                });
                dialog.show();
            }
            break;
        }
    }

    public void onBackPressed() {
        this.backAction();
    }

    private void backAction() {
        // 为推送准备
        if (ActivityManager.getInstance().getAllActivity().size() == 1) {
            Intent intent = new Intent(this, TenantMainActivity.class);
            this.startActivity(intent);
            this.finish();

        } else {
            this.finish();
        }
    }

    // 分享系统消息成功后回调
    private void requestShareMessage() {
        try {
            HashMap<String, String> tmepMap = JsonUtil.jsonToMap(dto.getFunctionData());
            int type = Integer.parseInt(tmepMap.get("type"));
            int shared = Integer.parseInt(tmepMap.get("shared"));

            if (shared == 1 || type == 0) // 已分享或是不送内容
                return;

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", dto.getId() + "");

            JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGE_SHARE, map, false, new Response.Listener<String>() {

                @Override
                public void onResponse(String jsonObject) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                        AppMessageDto<String> dto = objectMapper.readValue(jsonObject, javaType);
                        if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                            hasShare = true;

                        } else {
                            Toast.makeText(ShowShareWebViewActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            this.addToRequestQueue(request, "正在派奖请稍候");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccessTip() {
        HashMap<String, String> map = JsonUtil.jsonToMap(dto.getFunctionData());

        String text = "";

        try {
            int type = Integer.parseInt(map.get("type"));
            if (type == 1) {
                text = map.get("value") + "积分已到账，请到我的奖励中查看";

                Toast.makeText(ShowShareWebViewActivity.this, text, Toast.LENGTH_LONG).show();

            } else if (type == 2) {
                text = map.get("value") + "元现金已到账，请到好友红包中领取";

                Toast.makeText(ShowShareWebViewActivity.this, text, Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // android.view.WindowManager$BadTokenException: Unable to add window -- token null  is not valid; is your activity running?
    // PopupWindow必须在某个事件中显示或者是开启一个新线程去调用，不能直在oncreate方法中显示一个PopupWindow，否则永远会有以上错误
    class ShowShareTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            showShareTip();
        }
    }

    private void showShareTip() {
        try {
            HashMap<String, String> map = JsonUtil.jsonToMap(dto.getFunctionData());
            int type = Integer.parseInt(map.get("type"));
            int shared = Integer.parseInt(map.get("shared"));

            if (shared == 1) { // 已分享

            } else {
                String text = "";

                if (type == 1) {
                    text = "分享就送" + map.get("value") + "积分";
                    showShareTipDialog(text, 60);
                } else if (type == 2) {
                    text = "分享就送现金" + map.get("value") + "元";
                    showShareTipDialog(text, 40);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 弹出分享送红包提醒
    private PopupWindow tipPopup = null;
    private TextView tipTextView = null;

    private void showShareTipDialog(String text, int xOff) {
        try {
            if (null == tipPopup) {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.layout_share_popup_tip, null);
                tipTextView = (TextView) layout.findViewById(R.id.tipTextView);

                tipPopup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tipPopup.setFocusable(false);
                tipPopup.setBackgroundDrawable(new BitmapDrawable());
                tipPopup.setOutsideTouchable(false);
            }

            tipTextView.setText(text);

            tipPopup.showAsDropDown(titleTextView, AdapterUtil.dip2px(this, xOff), 30);


            handler.postDelayed(runnable, 5000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        public void run() {
            tipPopup.dismiss();
            handler.removeCallbacks(this);
        }
    };

}
