package com.sabdroidex.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.Show;
import com.sabdroidex.utils.AsyncImage;
import com.sabdroidex.utils.AsyncSeasonPoster;

public class ShowSeasonAdapater extends BaseAdapter {
    
    private final Context context;
    private final LayoutInflater inflater;
    private final Vector<Bitmap> mListPosters;
    private final Vector<AsyncImage> mAsyncImages;
    private final List<Integer> items;
    private final Bitmap mEmptyBanner;
    private Show show;
    
    public ShowSeasonAdapater(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.items = new ArrayList<Integer>();
        this.mListPosters = new Vector<Bitmap>();
        this.mAsyncImages = new Vector<AsyncImage>();
        this.mEmptyBanner = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_poster);
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
        
        this.mAsyncImages.setSize(items.size());
        this.mListPosters.setSize(items.size());
        
        if (items.size() != 0 && mAsyncImages.size() != items.size()) {
            this.mAsyncImages.clear();
            this.mAsyncImages.setSize(items.size());
        }
        
        if (mListPosters.get(position) == null) {
            seasonItem.seasonPoster.setImageBitmap(mEmptyBanner);
            
            if (mAsyncImages.get(position) == null) {
                mAsyncImages.add(position, new AsyncSeasonPoster());
            }
            
            if (mAsyncImages.get(position).getStatus() != Status.FINISHED && mAsyncImages.get(position).getStatus() != Status.RUNNING) {
                mAsyncImages.get(position).execute(imageHandler, position, show.getTvdvId(), show.getShowName(), show.getSeasonList().get(position));
            }
        }
        else {
            seasonItem.seasonPoster.setImageBitmap(mListPosters.get(position));
        }
        
        Integer season = show.getSeasonList().get(position);
        if (season == 0) {
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
    
    /**
     * Handler used to notify this Fragment that an image has been downloaded
     * and that it should be refreshed to display it.
     */
    private final Handler imageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                mListPosters.set(msg.what, bitmap);
                notifyDataSetChanged();
            }
        }
    };
    
    public void setShow(Show show) {
        this.show = show;
        this.items.clear();
        this.items.addAll(show.getSeasonList());
    }
    
    @Override
    public final int getCount() {
        return items.size();
    }
    
    @Override
    public final Object getItem(int position) {
        return items.get(position);
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