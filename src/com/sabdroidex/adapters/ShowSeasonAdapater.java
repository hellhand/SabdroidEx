package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.Show;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class ShowSeasonAdapater extends BaseAdapter {
    
    private final Context context;
    private final LayoutInflater inflater;
    private Show show;
    
    public ShowSeasonAdapater(Context context, Show show) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.show = show;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        SeasonItem seasonItem;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.season_item, null);
            seasonItem = new SeasonItem();
            seasonItem.seasonName = (TextView) convertView.findViewById(R.id.show_season_name);
            seasonItem.showName = (TextView) convertView.findViewById(R.id.show_name);
            seasonItem.seasonPoster = (ImageView) convertView.findViewById(R.id.show_season_poster);
        }
        else {
            seasonItem = (SeasonItem) convertView.getTag();
        }
        
        String imageKey = ImageType.SEASON_POSTER.name() + show.getTvdbId() + show.getSeasonList().get(position);
        ImageUtils.getImageWorker().loadImage(seasonItem.seasonPoster, ImageType.SEASON_POSTER, imageKey, show.getTvdbId(), show.getShowName(), show.getSeasonList().get(position));
        if (show.getSeasonList().get(position) == 0) {
            seasonItem.seasonName.setText(context.getResources().getString(R.string.show_specials));
        }
        else {
            seasonItem.seasonName.setText(context.getResources().getString(R.string.show_season) + " " + show.getSeasonList().get(position));
        }
        seasonItem.showName.setText(show.getShowName());
        
        convertView.setId(position);
        convertView.setTag(seasonItem);
        
        return convertView;
    }
    
    public void setShow(Show show) {
        this.show = show;
    }
    
    @Override
    public final int getCount() {
        if (show == null) {
            return 0;
        }
        return show.getSeasonList().size();
    }
    
    @Override
    public final Object getItem(int position) {
        return show.getSeasonList().get(position);
    }
    
    @Override
    public final long getItemId(int position) {
        return position;
    }
    
    class SeasonItem {
        
        ImageView seasonPoster;
        TextView seasonName;
        TextView showName;
    }
}