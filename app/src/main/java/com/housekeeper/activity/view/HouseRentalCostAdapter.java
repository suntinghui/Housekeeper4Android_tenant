package com.housekeeper.activity.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.housekeeper.HousekeeperApplication;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.model.RentContainAppDtoEx;
import com.wufriends.housekeeper.tenant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 9/25/15.
 */
public class HouseRentalCostAdapter extends BaseAdapter {

    private BaseActivity context = null;
    private LayoutInflater layoutInflater = null;
    private List<RentContainAppDtoEx> list = new ArrayList<RentContainAppDtoEx>();
    private boolean editable = false;

    public HouseRentalCostAdapter(BaseActivity context) {
        this.context = context;

        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<RentContainAppDtoEx> list, boolean editable) {
        if (list == null)
            return;

        this.list = list;
        this.editable = editable;

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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.item_rental_cost, parent, false);

            holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
            holder.logoImageView = (ImageView) convertView.findViewById(R.id.logoImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final RentContainAppDtoEx dto = list.get(position);
        holder.titleTextView.setText(dto.getName());

        if (this.editable) {
            if (dto.isSelected()) {
                holder.logoImageView.setBackgroundResource(HousekeeperApplication.getInstance().getResources().getIdentifier(dto.getImg().toLowerCase() + "_selected", "drawable", HousekeeperApplication.getInstance().getPackageName()));
                holder.titleTextView.setTextColor(Color.parseColor("#222222"));
            } else {
                holder.logoImageView.setBackgroundResource(HousekeeperApplication.getInstance().getResources().getIdentifier(dto.getImg().toLowerCase() + "_normal", "drawable", HousekeeperApplication.getInstance().getPackageName()));
                holder.titleTextView.setTextColor(Color.parseColor("#999999"));
            }
        } else {
            holder.logoImageView.setBackgroundResource(HousekeeperApplication.getInstance().getResources().getIdentifier(dto.getImg().toLowerCase() + "_selected", "drawable", HousekeeperApplication.getInstance().getPackageName()));
            holder.titleTextView.setTextColor(Color.parseColor("#222222"));
        }

        return convertView;
    }

    public static final class ViewHolder {
        private LinearLayout contentLayout;
        private ImageView logoImageView;
        private TextView titleTextView;
    }

    // 选中一个 或  取消选中一个
    public void setCheckItem(int id) {
        for (RentContainAppDtoEx dto : list) {
            if (dto.getId() == id) {
                dto.setSelected(!dto.isSelected());
                break;
            }
        }

        this.notifyDataSetChanged();
    }

    // 全选 或 取消全选
    public void setCheckAllItem(boolean checkAll) {

        for (RentContainAppDtoEx dto : list) {
            dto.setSelected(checkAll);
        }

        this.notifyDataSetChanged();
    }

    public String getCheckIds() {
        StringBuffer sb = new StringBuffer();
        for (RentContainAppDtoEx dto : list) {
            if (dto.isSelected()) {
                sb.append(dto.getId() + ",");
            }
        }

        try {
            return sb.deleteCharAt(sb.lastIndexOf(",")).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return sb.toString();
        }
    }

    // 判断是否已经全部选中
    public boolean hasCheckAll() {
        for (RentContainAppDtoEx dto : list) {
            if (dto.isSelected() == false) {
                return false;
            }
        }

        return true;
    }
}
