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
import com.wufriends.housekeeper.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 8/26/15.
 */
public class NotificationAdapter extends BaseAdapter {

    private BaseActivity context = null;
    private LayoutInflater layoutInflater = null;
    private List<MessageListAppDto> list = new ArrayList<MessageListAppDto>();

    public NotificationAdapter(BaseActivity context) {
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

            convertView = layoutInflater.inflate(R.layout.item_notification, parent, false);

            holder.iconImageView = (CustomNetworkImageView) convertView.findViewById(R.id.iconImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.contentTextView = (TextView) convertView.findViewById(R.id.contentTextView);
            holder.queryTextView = (TextView) convertView.findViewById(R.id.queryTextView);
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

        holder.timeTextView.setText(dto.getTime());

        holder.iconImageView.setLocalImageBitmap(R.drawable.icon_notification);
        holder.iconImageView.setErrorImageResId(R.drawable.icon_notification);
        holder.iconImageView.setDefaultImageResId(R.drawable.icon_notification);
        if (!StringUtils.isBlank(dto.getLogo()) && dto.getLogo().startsWith("http")) {
            holder.iconImageView.setImageUrl(dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
        } else {
            holder.iconImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
        }

        return convertView;
    }

    public static final class ViewHolder {
        private CustomNetworkImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView queryTextView;
        private TextView timeTextView;
    }
}
