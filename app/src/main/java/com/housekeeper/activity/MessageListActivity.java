package com.housekeeper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.MessageAppDto;
import com.ares.house.dto.app.MessageListAppDto;
import com.housekeeper.activity.view.MessageAdapter;
import com.housekeeper.activity.view.NotificationAdapter;
import com.housekeeper.client.Constants;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.ActivityUtil;
import com.housekeeper.utils.JsonUtil;
import com.wufriends.housekeeper.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 8/26/15.
 */
public class MessageListActivity extends BaseActivity implements View.OnClickListener {

    private TextView messageTextView; // 系统消息
    private TextView notificationTextView; // 通知

    private ImageView newMessageImageView;
    private ImageView newNotificationImageView;

    private LinearLayout messageLayout;
    private LinearLayout notificationLayout;

    private ListView messageListView;
    private ListView notificationListView;

    private MessageAdapter messageAdapter;
    private NotificationAdapter notificationAdapter;

    private List<MessageListAppDto> messageList = new ArrayList<MessageListAppDto>();
    private List<MessageListAppDto> notificationList = new ArrayList<MessageListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_list);

        initView();

        requestMessageList(true, "正在请求数据...");
        requestMessageList(false, "正在请求数据...");
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("消息列表");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);
        messageTextView.setOnClickListener(this);

        notificationTextView = (TextView) this.findViewById(R.id.notificationTextView);
        notificationTextView.setOnClickListener(this);

        newMessageImageView = (ImageView) this.findViewById(R.id.newMessageImageView);
        newNotificationImageView = (ImageView) this.findViewById(R.id.newNotificationImageView);

        messageLayout = (LinearLayout) this.findViewById(R.id.messageLayout);
        notificationLayout = (LinearLayout) this.findViewById(R.id.notificationLayout);

        messageListView = (ListView) this.findViewById(R.id.messageListView);
        messageAdapter = new MessageAdapter(this);
        messageListView.setAdapter(messageAdapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestMessageRead(messageList.get(position));
            }
        });

        notificationListView = (ListView) this.findViewById(R.id.notificationListView);
        notificationAdapter = new NotificationAdapter(this);
        notificationListView.setAdapter(notificationAdapter);
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestMessageRead(notificationList.get(position));
            }
        });

        messageTextView.setSelected(true);
        notificationTextView.setSelected(false);
        messageLayout.setVisibility(View.VISIBLE);
        notificationLayout.setVisibility(View.GONE);

        LinearLayout emptyLayout = (LinearLayout) this.findViewById(R.id.emptyLayout);
        messageListView.setEmptyView(emptyLayout);
        ImageView noDataImageView = (ImageView) emptyLayout.findViewById(R.id.noDataImageView);
        noDataImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMessageList(true, "正在请求数据...");
            }
        });

        LinearLayout unEmptyLayout = (LinearLayout) this.findViewById(R.id.unEmptyLayout);
        notificationListView.setEmptyView(unEmptyLayout);
        ImageView unNoDataImageView = (ImageView) unEmptyLayout.findViewById(R.id.noDataImageView);
        unNoDataImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMessageList(false, "正在请求数据...");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (messageTextView.isSelected()) {
            this.requestMessageList(true, null);
        } else {
            this.requestMessageList(false, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.messageTextView:
                messageTextView.setSelected(true);
                notificationTextView.setSelected(false);
                messageLayout.setVisibility(View.VISIBLE);
                notificationLayout.setVisibility(View.GONE);
                break;

            case R.id.notificationTextView:
                messageTextView.setSelected(false);
                notificationTextView.setSelected(true);
                messageLayout.setVisibility(View.GONE);
                notificationLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void requestMessageList(final boolean isMessage, String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("type", isMessage ? "1" : "0");// 0通知 1系统消息
        tempMap.put("pageNo", "1");
        tempMap.put("pageSize", "30");

        JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGE_LIST_2, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MessageAppDto.class);
                    AppMessageDto<MessageAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        if (isMessage) {
                            messageList = dto.getData().getData().getList();
                            messageAdapter.setData(messageList);

                            if (dto.getData().getNoReadCount() > 0) {
                                newMessageImageView.setVisibility(View.VISIBLE);
                            } else {
                                newMessageImageView.setVisibility(View.GONE);
                            }
                        } else {
                            notificationList = dto.getData().getData().getList();
                            notificationAdapter.setData(notificationList);

                            if (dto.getData().getNoReadCount() > 0) {
                                newNotificationImageView.setVisibility(View.VISIBLE);
                            } else {
                                newNotificationImageView.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        Toast.makeText(MessageListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, msg);

    }

    private void requestMessageRead(final MessageListAppDto infoDto) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", infoDto.getId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGEREAD, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        jumpAction(infoDto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    // 如果有修改一定要同时修改推送部分～～
    private void jumpAction(MessageListAppDto dto) {
        /**
         * 跳转数据 json格式 a系统消息的时候是http的url地址{"url":"http://www.baidu.com"} b订单 订单标示{"orderId":1} c好友 为空 d还款 还款订单标示{"orderId":1} e审核 为空
         */
        /**
         * 跳转标识  1定投列表 2抢投列表 3转让列表  4打开链接  5好友列表  6我的投资  7 系统提醒（do nothing）  9另一设备登录
         */
        try {
            switch (dto.getFunctionType()) {

                case 0: // 无任何操作

                    break;

                case 1: {

                }
                break;

                case 2: {

                }
                break;

                case 4: {
                    Intent intent = new Intent(this, ShowShareWebViewActivity.class);
                    intent.putExtra("title", "系统消息");
                    intent.putExtra("url", Constants.HOST_IP + "/rpc/" + JsonUtil.jsonToMap(dto.getFunctionData()).get("url"));
                    intent.putExtra("SHOW_SHARE", true);

                    intent.putExtra("DTO", dto);

                    /*
                    intent.putExtra("shareTitle", dto.getTitle());
                    intent.putExtra("shareContent", dto.getContent());
                    intent.putExtra("shareURL", Constants.HOST_IP + "/share/message/" + dto.getId());
                    intent.putExtra("shareData", dto.getFunctionData());
                    intent.putExtra("shareId", dto.getId() + "");
                    */

                    this.startActivityForResult(intent, 0);
                }
                break;

                case 5: {

                }
                break;

                case 6: {

                }
                break;

                case 7: { // 通知
                    Intent intent = new Intent(this, ShowNotificationActivity.class);
                    intent.putExtra("DTO", dto);
                    this.startActivityForResult(intent, 0);
                }
                break;

                case 9: { // 注销登录
                    Toast.makeText(this, "已注销登录", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                    editor.putString(Constants.Base_Token, "");
                    editor.commit();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("FROM", LoginActivity.FROM_TOKEN_EXPIRED);
                    this.startActivity(intent);

                }

                default:
                    Toast.makeText(this, "没有找到相应的类型", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
