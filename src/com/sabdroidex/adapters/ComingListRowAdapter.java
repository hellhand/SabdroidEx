package com.sabdroidex.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.sickbeard.FutureEpisode;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class ComingListRowAdapter extends BaseAdapter {
    
    private final Context mContext;
    private final LayoutInflater mInflater;
    private FuturePeriod futurePeriod;
    
    public ComingListRowAdapter(Context context, FuturePeriod futurePeriod) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.futurePeriod = futurePeriod;
    }
    
    public void setDataSet(FuturePeriod futurePeriod) {
        this.futurePeriod = futurePeriod;
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
        if (getItemViewType(position) == 0) {
            ListSeparatorHolder listSeparatorHolder = (ListSeparatorHolder) getItem(position);
            
            convertView.setPadding(0, 0, 0, 0);
            comingItem.banner.setVisibility(View.GONE);
            comingItem.next_.setVisibility(View.GONE);
            comingItem.next.setVisibility(View.GONE);
            comingItem.airs_.setVisibility(View.GONE);
            comingItem.airs.setVisibility(View.GONE);
            
            comingItem.title.setTextColor(Color.BLACK);
            comingItem.title.setBackgroundColor(Color.rgb(156, 181, 207));
            comingItem.title.setGravity(Gravity.CENTER);
            comingItem.title.setText(mContext.getString(listSeparatorHolder.getSeparator()));
        }
        else {
            FutureEpisode futureEpisode = (FutureEpisode) getItem(position);
            
            convertView.setPadding(10, 4, 10, 0);
            comingItem.banner.setVisibility(View.VISIBLE);
            comingItem.next_.setVisibility(View.VISIBLE);
            comingItem.next.setVisibility(View.VISIBLE);
            comingItem.airs_.setVisibility(View.VISIBLE);
            comingItem.airs.setVisibility(View.VISIBLE);
            
            String imageKey = ImageType.SHOW_BANNER.name() + futureEpisode.getTvdbId();
            ImageUtils.getImageWorker().loadImage(comingItem.banner, ImageType.SHOW_BANNER, imageKey,
                    futureEpisode.getTvdbId(), futureEpisode.getShowName());
            comingItem.title.setTextColor(Color.WHITE);
            comingItem.title.setBackgroundColor(Color.rgb(128, 128, 128));
            comingItem.title.setGravity(Gravity.LEFT);
            comingItem.title.setText(futureEpisode.getShowName());
            
            String nextDescriptor = String.format("%02dx%02d", futureEpisode.getEpisode(), futureEpisode.getSeason());
            String nextDetails = String.format("%sx%s", futureEpisode.getEpName(), futureEpisode.getAirDate());
            String next = nextDescriptor + " - " + nextDetails;
            String airs = String.format("%s %s [%s]", futureEpisode.getAirs(), futureEpisode.getNetwork(),
                    futureEpisode.getQuality());
            
            comingItem.next.setText(next);
            comingItem.airs.setText(airs);
        }
        convertView.setId(position);
        convertView.setTag(comingItem);
        return (convertView);
    }
    
    @Override
    public int getCount() {
        return futurePeriod.getCount();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public Object getItem(int position) {
        return futurePeriod.getElements().get(position);
    }
    
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (futurePeriod.getElements().get(position) instanceof ListSeparatorHolder) {
            return 0;
        }
        return 1;
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
