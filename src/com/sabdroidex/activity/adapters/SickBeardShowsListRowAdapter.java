package com.sabdroidex.activity.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sabdroidex.R;
import com.sabdroidex.sickbeard.SickBeardController;
import com.utils.HttpUtil;

public class SickBeardShowsListRowAdapter extends ArrayAdapter<Object[]> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ShowsListItem mQueueListItem;
    private ArrayList<Object[]> mItems;
    private ArrayList<Bitmap> banners;
    private AsyncImageView asyncImageView;

    public SickBeardShowsListRowAdapter(Context context, ArrayList<Object[]> items) {
        super(context, R.layout.list_item, items);
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.banners = new ArrayList<Bitmap>();

        for (Object[] o : items) {
            asyncImageView = new AsyncImageView();
            asyncImageView.execute(o);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_show, null);
            mQueueListItem = new ShowsListItem();
            mQueueListItem.banner = (ImageView) convertView.findViewById(R.id.showBanner);
        }
        else {
            mQueueListItem = (ShowsListItem) convertView.getTag();
        }

        if (banners.size() < 1 + position) {
            mQueueListItem.banner.setImageResource(R.drawable.temp_banner);
        }
        else {
            mQueueListItem.banner.setImageBitmap(banners.get(position));
        }
        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    class ShowsListItem {

        ImageView banner;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Object[] params = (Object[]) ((Object[]) msg.obj)[0];
            Bitmap bitmap = (Bitmap) ((Object[]) msg.obj)[1];
            int position = mItems.indexOf(params);
            banners.add(position, bitmap);
        }
    };

    private class AsyncImageView extends AsyncTask<Object, Void, Bitmap> {

        public AsyncImageView() {
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            String url = SickBeardController.getImageURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[5]);
            byte[] data = HttpUtil.getInstance().getDataAsByteArray(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            Message msg = new Message();
            msg.obj = new Object[] { params, bitmap };
            handler.sendMessage(msg);
            return bitmap;
        }
    }
}
