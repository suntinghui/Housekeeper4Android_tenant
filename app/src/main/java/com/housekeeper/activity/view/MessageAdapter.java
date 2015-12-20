package com.housekeeper.activity.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ares.house.dto.app.MessageListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.client.Constants;
import com.housekeeper.client.net.ImageCacheManager;
import com.housekeeper.utils.JsonUtil;
import com.wufriends.housekeeper.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 8/26/15.
 */
public class MessageAdapter extends BaseAdapter {

    private BaseActivity context = null;
    private LayoutInflater layoutInflater = null;
    private List<MessageListAppDto> list = new ArrayList<MessageListAppDto>();

    public MessageAdapter(BaseActivity context) {
        this.context = context;

        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<MessageListAppDto> list) {
        if (list == null)
            return;

        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.item_message, parent, false);

            holder.iconImageView = (CustomNetworkImageView) convertView.findViewById(R.id.iconImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.contentTextView);
            holder.picImageView = (CustomNetworkImageView) convertView.findViewById(R.id.picImageView);
            holder.queryTextView = (TextView) convertView.findViewById(R.id.queryTextView);
            holder.shareRewardTextView = (TextView) convertView.findViewById(R.id.shareRewardTextView);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MessageListAppDto dto = list.get(position);

        holder.titleTextView.setText(dto.getTitle());
        holder.contentTextView.setText(dto.getContent());

        if (dto.isRead()) {
            holder.queryTextView.setText("已读");
            holder.queryTextView.setTextColor(Color.parseColor("#aaaaaa"));

        } else {
            holder.queryTextView.setText("点击查看");
            holder.queryTextView.setTextColor(context.getResources().getColor(R.color.blueme));
        }

        try {
            HashMap<String, String> tempMap = JsonUtil.jsonToMap(dto.getFunctionData());
            int type = Integer.parseInt(tempMap.get("type"));
            int shared = Integer.parseInt(tempMap.get("shared"));

            if (shared == 0 && type != 0) {
                if (type == 1) {
                    holder.shareRewardTextView.setText(tempMap.get("value") + "积分");
                } else if (type == 2) {
                    holder.shareRewardTextView.setText(tempMap.get("value") + "元");
                }

                holder.shareRewardTextView.setVisibility(View.VISIBLE);
            } else {
                holder.shareRewardTextView.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            holder.shareRewardTextView.setVisibility(View.GONE);
        }

        holder.timeTextView.setText(dto.getTime());

        holder.iconImageView.setLocalImageBitmap(R.drawable.icon_message);
        holder.iconImageView.setErrorImageResId(R.drawable.icon_message);
        holder.iconImageView.setDefaultImageResId(R.drawable.icon_message);
        if (!StringUtils.isBlank(dto.getLogo()) && dto.getLogo().startsWith("http")) {
            holder.iconImageView.setImageUrl(dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
        } else {
            holder.iconImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
        }

        holder.picImageView.setLocalImageBitmap(R.drawable.message_placeholder);
        holder.picImageView.setErrorImageResId(R.drawable.message_placeholder);
        holder.picImageView.setDefaultImageResId(R.drawable.message_placeholder);
        if (!StringUtils.isBlank(dto.getImg()) && dto.getImg().startsWith("http")) {
            holder.picImageView.setImageUrl(dto.getImg(), ImageCacheManager.getInstance().getImageLoader());
        } else {
            holder.picImageView.setImageUrl(Constants.HOST_IP + dto.getImg(), ImageCacheManager.getInstance().getImageLoader());
        }

        return convertView;
    }

    public static final class ViewHolder {
        private CustomNetworkImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private CustomNetworkImageView picImageView;
        private TextView queryTextView;
        private TextView shareRewardTextView;
        private TextView timeTextView;
    }
}
