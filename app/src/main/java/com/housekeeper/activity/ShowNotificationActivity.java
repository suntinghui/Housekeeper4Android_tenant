package com.housekeeper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ares.house.dto.app.MessageListAppDto;
import com.wufriends.housekeeper.R;


/**
 * Created by sth on 9/6/15.
 */
public class ShowNotificationActivity extends BaseActivity implements View.OnClickListener {

    private TextView contentTextView = null;
    private TextView dateTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_notification);

        initView();
    }

    private void initView(){
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("通知");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        contentTextView = (TextView) this.findViewById(R.id.contentTextView);
        dateTextView = (TextView) this.findViewById(R.id.dateTextView);

        MessageListAppDto dto = (MessageListAppDto) this.getIntent().getSerializableExtra("DTO");
        contentTextView.setText(dto.getContent());
        dateTextView.setText(dto.getTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
