package com.housekeeper.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ares.house.dto.app.HouseReleaseInfoAppDto;
import com.ares.house.dto.app.MessageListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.client.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.wufriends.housekeeper.R;

public class HouseShareDialog extends Dialog implements View.OnClickListener {

    private BaseActivity context = null;

    private TextView timelineTextView = null;
    private TextView QZoneTextView = null;

    private HouseReleaseInfoAppDto dto = null;

    private OnStartShareListener listener = null;

    public HouseShareDialog(Context context, HouseReleaseInfoAppDto dto) {
        this(context, R.style.ProgressHUD);

        this.dto = dto;
    }

    public HouseShareDialog(Context context, int theme) {
        super(context, theme);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (BaseActivity) context;

        this.setContentView(R.layout.layout_message_share);

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        lp.width = (int) (this.context.getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        this.getWindow().setAttributes(lp);

        timelineTextView = (TextView) this.findViewById(R.id.timelineTextView);
        timelineTextView.setOnClickListener(this);

        QZoneTextView = (TextView) this.findViewById(R.id.QZoneTextView);
        QZoneTextView.setOnClickListener(this);
    }

    public void setOnStartShareListener(OnStartShareListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timelineTextView:
                shareTimeline();
                break;

            case R.id.QZoneTextView:
                shareQZone();
                break;

        }

        this.dismiss();
    }

    private String getShareContent() {
        return "我在【我有房】租房APP找到一套好房子，" + this.dto.getAreaStr() + this.dto.getHouseType() + "，" + this.dto.getMonthMoney() + "元/月，一起来看看吧！";
    }

    private void shareTimeline() {
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(getShareContent());
        circleMedia.setTitle(getShareContent());
        circleMedia.setTargetUrl(Constants.HOST_IP + "/share/house/" + dto.getHouseId() + ".html");
        if (dto.getTopImages().size() == 0) {
            circleMedia.setShareImage(new UMImage(this.context, R.drawable.share_money_1000_wechat));
        } else {
            circleMedia.setShareImage(new UMImage(this.context, Constants.HOST_IP + dto.getTopImages().get(0).getUrl()));
        }
        mController.setShareMedia(circleMedia);
        mController.postShare(this.context, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
            @Override
            public void onStart() {
                if (listener != null) {
                    listener.startShare();
                }
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {
            }
        });
    }

    private void shareQZone() {
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(getShareContent());
        qzone.setTitle(getShareContent());
        qzone.setTargetUrl(Constants.HOST_IP + "/share/house/" + dto.getHouseId() + ".html");
        if (dto.getTopImages().size() == 0) {
            qzone.setShareImage(new UMImage(this.context, R.drawable.share_money_1000_wechat));
        } else {
            qzone.setShareImage(new UMImage(this.context, Constants.HOST_IP + dto.getTopImages().get(0).getUrl()));
        }

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(qzone);

        mController.postShare(context, SHARE_MEDIA.QZONE, new SnsPostListener() {
            @Override
            public void onStart() {
                if (listener != null) {
                    listener.startShare();
                }
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
            }

        });
    }

    public interface OnStartShareListener {
        public void startShare();
    }
}
