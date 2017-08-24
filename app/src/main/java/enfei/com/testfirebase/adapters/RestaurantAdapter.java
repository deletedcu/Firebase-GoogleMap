package enfei.com.testfirebase.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import enfei.com.testfirebase.R;
import enfei.com.testfirebase.models.Restaurant;
import enfei.com.testfirebase.utils.StringUtils;

/**
 * Created by king on 19/08/2017.
 */

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    private LayoutInflater inflater;
    private List<Restaurant> mList = new ArrayList<>();

    public RestaurantAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<Restaurant> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_restaurant, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Restaurant item = getItem(position);
        holder.tvName.setText(item.name);
        if (StringUtils.isNotEmpty(item.address))
            holder.tvAddress.setText(item.address);
        if (StringUtils.isNotEmpty(item.subTitle))
            holder.tvSubTitle.setText(item.subTitle);

        return convertView;
    }

    private class ViewHolder {
        public TextView tvName;
        public TextView tvAddress;
        public TextView tvSubTitle;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvAddress = (TextView) view.findViewById(R.id.tv_address);
            tvSubTitle = (TextView) view.findViewById(R.id.tv_subTitle);
        }
    }

}
