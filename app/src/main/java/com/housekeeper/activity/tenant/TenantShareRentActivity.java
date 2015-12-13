package com.housekeeper.activity.tenant;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.housekeeper.activity.BaseActivity;
import com.wufriends.housekeeper.tenant.R;

/**
 * Created by sth on 10/23/15.
 */
public class TenantShareRentActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_sharerent);

        this.initView();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        ((TextView)this.findViewById(R.id.titleTextView)).setText("合租");

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
