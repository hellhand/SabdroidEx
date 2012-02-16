package com.sabdroidex.activity.adapters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Environment;
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

    private static File mExtFolder = Environment.getExternalStorageDirectory();

    private Context mContext;
    private LayoutInflater mInflater;
    private ShowsListItem mQueueListItem;
    private ArrayList<Object[]> mItems;
    private Vector<ShowsListItem> mListItems;
    private Vector<AsyncImage> mAsyncImages;

    public SickBeardShowsListRowAdapter(Context context, ArrayList<Object[]> items) {
        super(context, R.layout.list_item, items);
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mListItems = new Vector<ShowsListItem>();
        this.mAsyncImages = new Vector<AsyncImage>();
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
        this.mListItems.setSize(mItems.size());
        this.mAsyncImages.setSize(mItems.size());

        mListItems.set(position, mQueueListItem);

        if (mItems.size() != 0 && mAsyncImages.size() != mItems.size()) {
            this.mAsyncImages.clear();
            this.mAsyncImages.setSize(mItems.size());
        }

        if (mItems.get(position)[6] == null) {
            mQueueListItem.banner.setImageResource(R.drawable.temp_banner);

            if (mAsyncImages.get(position) == null) {
                mAsyncImages.add(position, new AsyncImage());
            }

            if (mAsyncImages.get(position).getStatus() != Status.FINISHED && mAsyncImages.get(position).getStatus() != Status.RUNNING) {
                mAsyncImages.get(position).execute(position, mItems.get(position)[5], mItems.get(position)[0]);
            }
        }
        else {
            mQueueListItem.banner.setImageBitmap((Bitmap) mItems.get(position)[6]);
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
            Bitmap bitmap = (Bitmap) msg.obj;
            mItems.get(msg.what)[6] = bitmap;
            notifyDataSetChanged();
        }
    };

    private class AsyncImage extends AsyncTask<Object, Void, Bitmap> {

        /**
         * 
         * @param params [0] Is the item position in the list, [1] Is the IMDB id of the TV show, [2] Is the name of the TV Show
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object... params) {

            /**
             * Trying to find Image on Local System
             */
            String folderPath = mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + params[2] + File.separator;
            folderPath = folderPath.replace(":", "");
            File folder = new File(folderPath);
            folder.mkdirs();

            Options BgOptions = new Options();
            BgOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(folderPath + File.separator + "banner.jpg", BgOptions);

            /**
             * The bitmap object is null if the BitmapFactory has been unable to decode the file. Hopefully this won't happen often
             */
            if (bitmap == null) {
                /**
                 * We get the banner from the server
                 */
                String url = SickBeardController.getImageURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[1]);
                byte[] data = HttpUtil.getInstance().getDataAsByteArray(url);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                /**
                 * And save it in the cache
                 */
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(folderPath + File.separator + "banner.jpg");
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Waking up the main Thread
             */
            Message msg = new Message();
            msg.obj = bitmap;
            msg.what = (Integer) params[0];
            handler.sendMessage(msg);

            return bitmap;
        }
    }
}
