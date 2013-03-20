package com.sabdroidex.adapters;

import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.data.Show;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class ShowsListRowAdapter extends ArrayAdapter<Show> {
    
    private final Context mContext;
    private final LayoutInflater mInflater;
    private boolean showOverlay = false;
    
    public ShowsListRowAdapter(Context context, List<Show> items) {
        super(context, R.layout.show_item, items);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.showOverlay = ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowsListItem showItem = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.show_item, null);
            showItem = new ShowsListItem();
            showItem.banner = (ImageView) convertView.findViewById(R.id.showBanner);
            showItem.overlay = (ImageView) convertView.findViewById(R.id.showOverlay);
            if (showOverlay) {
                showItem.overlay.setImageResource(R.drawable.list_arrow_selected_holo);
                showItem.overlay.setVisibility(View.INVISIBLE);
            }
        }
        else {
            showItem = (ShowsListItem) convertView.getTag();
        }
        
        if (showOverlay) {
            if (((ListView) parent).getCheckedItemPosition() == position) {
                showItem.overlay.getLayoutParams().height = convertView.getHeight();
                showItem.overlay.setImageResource(R.drawable.list_arrow_selected_holo);
                showItem.overlay.setVisibility(View.VISIBLE);
            }
            else {
                showItem.overlay.setVisibility(View.INVISIBLE);
            }
        }
        
        Show show = getItem(position);
        String imageKey = ImageType.BANNER.name() + show.getTvdbId();
        ImageUtils.getImageWorker().loadImage(showItem.banner, ImageType.BANNER, imageKey, show.getTvdbId(), show.getShowName());
        
        convertView.setId(position);
        convertView.setTag(showItem);
        return (convertView);
    }
    
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    
    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {
        
        ImageView banner;
        ImageView overlay;
    }
}
