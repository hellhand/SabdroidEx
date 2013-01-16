package com.sabdroidex.adapters;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.utils.AsyncImage;

public class ShowsListRowAdapter extends ArrayAdapter<Object[]> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Object[]> rows;
    private final Vector<Bitmap> mListBanners;
    private final Vector<AsyncImage> mAsyncImages;
    private final Bitmap mEmptyBanner;
    private ShowsListItem mQueueListItem;
    private int mSelectedIndex;

    public ShowsListRowAdapter(Context context, ArrayList<Object[]> rows) {
        super(context, R.layout.show_item, rows);
        this.mContext = context;
        this.rows = rows;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mListBanners = new Vector<Bitmap>();
        this.mAsyncImages = new Vector<AsyncImage>();
        this.mEmptyBanner = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.temp_banner);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.show_item, null);
            mQueueListItem = new ShowsListItem();
            mQueueListItem.banner = (ImageView) convertView.findViewById(R.id.showBanner);
            mQueueListItem.overlay = (ImageView) convertView.findViewById(R.id.showOverlay);
            if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                    && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mQueueListItem.overlay.setImageResource(R.drawable.list_arrow_selected_holo);
            }
        }
        else {
            mQueueListItem = (ShowsListItem) convertView.getTag();
        }

        this.mListBanners.setSize(rows.size());
        this.mAsyncImages.setSize(rows.size());

        if (rows.size() != 0 && mAsyncImages.size() != rows.size()) {
            this.mAsyncImages.clear();
            this.mAsyncImages.setSize(rows.size());
        }

        if (mListBanners.get(position) == null) {
            mQueueListItem.banner.setImageBitmap(mEmptyBanner);

            if (mAsyncImages.get(position) == null) {
                mAsyncImages.add(position, new AsyncImage());
            }

            if (mAsyncImages.get(position).getStatus() != Status.FINISHED && mAsyncImages.get(position).getStatus() != Status.RUNNING) {
                mAsyncImages.get(position).execute(handler, position, rows.get(position)[5], rows.get(position)[0], SickBeardController.MESSAGE.SHOW_GETBANNER);
            }
        }
        else {
            mQueueListItem.banner.setImageBitmap(mListBanners.get(position));
        }

        if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (position == mSelectedIndex) {
                mQueueListItem.overlay.getLayoutParams().height = convertView.getHeight();
                mQueueListItem.overlay.setVisibility(View.VISIBLE);
            }
            else {
                mQueueListItem.overlay.setVisibility(View.GONE);
            }
        }

        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    public void setSelectedItem(int position) {
        mSelectedIndex = position;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        ImageView banner;
        ImageView overlay;
    }

    /**
     * This handler will receive the messages from the background worker
     */
    private final Handler handler = new Handler() {
    	
        /**
         * This method will handle the messages sent to this handler by the background worker
         */
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null && mListBanners != null && mListBanners.size() > msg.what) {
                mListBanners.set(msg.what, bitmap);
                notifyDataSetChanged();
            }
        }
    };

    /**
     * Clearing the {@link Bitmap} list
     */
    public void clearBitmaps() {
        if (mListBanners != null) {
            mListBanners.clear();
        }
        System.gc();
    }
}
