package com.sabdroidex.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class ComingListRowAdapter extends ArrayAdapter<Object[]> {

    private final Context mContext;
    private final LayoutInflater mInflater;

    public ComingListRowAdapter(Context context, ArrayList<Object[]> rows) {
        super(context, R.layout.coming_item, rows);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowsListItem comingItem;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.coming_item, null);
            comingItem = new ShowsListItem();
            comingItem.title = (TextView) convertView.findViewById(R.id.coming_show_name);
            comingItem.banner = (ImageView) convertView.findViewById(R.id.coming_show_banner);
            comingItem.next_ = (TextView) convertView.findViewById(R.id.coming_next_episode_);
            comingItem.next = (TextView) convertView.findViewById(R.id.coming_next_episode);
            comingItem.airs_ = (TextView) convertView.findViewById(R.id.coming_airs_);
            comingItem.airs = (TextView) convertView.findViewById(R.id.coming_airs);
        }
        else {
            comingItem = (ShowsListItem) convertView.getTag();
        }

        /**
         * If the size is 1, this means this is a time descriptor
         */
        if (getItem(position).length == 1) {
            convertView.setPadding(0, 0, 0, 0);
            comingItem.banner.setVisibility(View.GONE);
            comingItem.next_.setVisibility(View.GONE);
            comingItem.next.setVisibility(View.GONE);
            comingItem.airs_.setVisibility(View.GONE);
            comingItem.airs.setVisibility(View.GONE);

            comingItem.title.setTextColor(Color.BLACK);
            comingItem.title.setBackgroundColor(Color.rgb(156, 181, 207));
            comingItem.title.setGravity(Gravity.CENTER);
            comingItem.title.setText(getItem(position)[0] + " ");
        }
        else {
            convertView.setPadding(10, 4, 10, 0);
            comingItem.banner.setVisibility(View.VISIBLE);
            comingItem.next_.setVisibility(View.VISIBLE);
            comingItem.next.setVisibility(View.VISIBLE);
            comingItem.airs_.setVisibility(View.VISIBLE);
            comingItem.airs.setVisibility(View.VISIBLE);

            String imageKey = ImageType.BANNER.name() + getItem(position)[1].toString();
            ImageUtils.getImageWorker().loadImage(comingItem.banner, ImageType.BANNER, imageKey, getItem(position)[1], getItem(position)[2]);            
            comingItem.title.setTextColor(Color.WHITE);
            comingItem.title.setBackgroundColor(Color.rgb(128, 128, 128));
            comingItem.title.setGravity(Gravity.LEFT);
            comingItem.title.setText(getItem(position)[2] + " ");

            String next = getItem(position)[3] + "x" + getItem(position)[4] + " - " + getItem(position)[5] + " (" + getItem(position)[6] + ")";
            String airs = getItem(position)[7] + " " + getItem(position)[8] + " [" + getItem(position)[9] + "]";

            comingItem.next.setText(next);
            comingItem.airs.setText(airs);
        }
        convertView.setId(position);
        convertView.setTag(comingItem);
        return (convertView);
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        TextView title;
        ImageView banner;
        TextView next_;
        TextView next;
        TextView airs_;
        TextView airs;
    }
}
