package com.sabdroidex.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.sickbeard.Episode;
import com.sabdroidex.data.sickbeard.Season;

public class SeasonEpisodeAdapater extends BaseAdapter {
    
    private LayoutInflater inflater;
    private Season season;
    
    public enum COLOR {
        DOWNLOADED (Color.rgb(100, 200, 100)), //GREEN
        SNATCHED (Color.rgb(225, 150, 225)), //PINK
        SKIPPED (Color.rgb(75, 75, 250)), //BLUE
        WANTED (Color.rgb(250, 50, 50)), //RED
        LOW_QUALITY (Color.rgb(225, 225, 50)), //YELLOW
        UNAIRED (Color.rgb(75, 75, 75)); //BLACKISH
        
        private int color;
        
        private COLOR(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
    
    public SeasonEpisodeAdapater(Context context, Season season) {
        this.inflater = LayoutInflater.from(context);
        this.season = season;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        EpisodeItem episodeItem;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.episode_item, null);
            episodeItem = new EpisodeItem();
            episodeItem.episode = (TextView) convertView.findViewById(R.id.episode);
            episodeItem.name = (TextView) convertView.findViewById(R.id.episode_name);
            episodeItem.airdate = (TextView) convertView.findViewById(R.id.episode_airdate);
            episodeItem.quality = (TextView) convertView.findViewById(R.id.episode_quality);
            episodeItem.status = (TextView) convertView.findViewById(R.id.episode_status);
        }
        else {
            episodeItem = (EpisodeItem) convertView.getTag();
        }
        
        Episode episode = (Episode) getItem(position);
        episodeItem.episode.setText(episode.getEpisode().toString());
        episodeItem.name.setText(episode.getName());
        episodeItem.airdate.setText(episode.getAirDate());
        episodeItem.quality.setText(episode.getQuality());
        episodeItem.status.setText(episode.getStatus());
        
        for (COLOR color : COLOR.values()) {
            if (episode.getStatus().toLowerCase().equals(color.name().toLowerCase().replace('_', ' '))) {
                episodeItem.status.setTextColor(color.getColor());
            }
        }
        
        convertView.setTag(episodeItem);
        
        return convertView;
    }
    
    @Override
    public int getCount() {
        if (season == null) {
            return 0;
        }
        return season.getEpisodes().size();
    }
    
    @Override
    public Object getItem(int position) {
        return season.getEpisodes().get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setSeason(Season season) {
        this.season = season;
    }
    
    class EpisodeItem {
        
        TextView episode;
        TextView name;
        TextView airdate;
        TextView quality;
        TextView status;
    }
}
