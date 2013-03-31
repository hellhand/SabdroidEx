package com.sabdroidex.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.Episode;

public class SeasonEpisodeAdapater extends BaseAdapter {
    
    private Context context;
    private LayoutInflater inflater;
    private List<Episode> episodes;
    
    public SeasonEpisodeAdapater(Context context, List<Episode> episodes) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.episodes = episodes;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        EpisodeItem episodeItem;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.season_item, null);
            episodeItem = new EpisodeItem();
        }
        else {
            episodeItem = (EpisodeItem) convertView.getTag();
        }
        
        return null;
    }
    
    @Override
    public int getCount() {
        if (episodes == null) {
            return 0;
        }
        return episodes.size();
    }
    
    @Override
    public Object getItem(int position) {
        return episodes.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
    
    class EpisodeItem {
        TextView episode;
        TextView name;
        TextView airdate;
        TextView quality;
        TextView status;
    }
}
