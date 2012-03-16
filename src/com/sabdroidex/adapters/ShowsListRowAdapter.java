package com.sabdroidex.adapters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.sickbeard.SickBeardController;
import com.utils.HttpUtil;

public class ShowsListRowAdapter extends ArrayAdapter<Object[]> {

    private static File mExtFolder = Environment.getExternalStorageDirectory();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private ShowsListItem mQueueListItem;
    private final ArrayList<Object[]> rows;
    private final Vector<Bitmap> mListBanners;
    private final Vector<AsyncImage> mAsyncImages;
    private final Bitmap mEmptyBanner;

    public ShowsListRowAdapter(Context context, ArrayList<Object[]> rows) {
        super(context, R.layout.list_item, rows);
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
            convertView = mInflater.inflate(R.layout.list, null);
            mQueueListItem = new ShowsListItem();
            mQueueListItem.banner = (ImageView) convertView.findViewById(R.id.showBanner);
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
                mAsyncImages.get(position).execute(position, rows.get(position)[5], rows.get(position)[0]);
            }
        }
        else {
            mQueueListItem.banner.setImageBitmap(mListBanners.get(position));
        }
        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        ImageView banner;
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
            else {
                Toast.makeText(getContext(), R.string.no_poster + " : " + rows.get(msg.what)[0], Toast.LENGTH_LONG);
            }
        }
    };

    // TODO: merge with the same function in SickbeardShowsFragment
    private class AsyncImage extends AsyncTask<Object, Void, Void> {

        /**
         * This method is a background worker and notifies us with a {@link Message} when is has finished
         * 
         * @param params [0] Is the item position in the list, [1] Is the IMDB id of the TV show, [2] Is the name of the TV Show
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            /**
             * Trying to find Image on Local System
             */
            String folderPath = mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + params[2] + File.separator;
            folderPath = folderPath.replace(":", "");
            File folder = new File(folderPath);
            folder.mkdirs();

            Options BgOptions = new Options();
            BgOptions.inPurgeable = true;
            BgOptions.inPreferredConfig = Config.RGB_565;
            if (mContext.getResources().getConfiguration().screenLayout >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                BgOptions.inSampleSize = 1;
            }
            else {
                BgOptions.inSampleSize = 2;
            }
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(folderPath + File.separator + "banner.jpg", BgOptions);
            }
            catch (Throwable e) {
                Log.w("ERROR", " " + e.getLocalizedMessage());
            }

            /**
             * The bitmap object is null if the BitmapFactory has been unable to decode the file. Hopefully this won't happen often
             */
            if (bitmap == null) {
                /**
                 * We get the banner from the server
                 */
                String url = SickBeardController.getBannerURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[1]);
                byte[] data = HttpUtil.getInstance().getDataAsByteArray(url);
                try {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, BgOptions);
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                    return null;
                }

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

            return null;
        }
    }

    public void clearBitmaps() {
        if (mListBanners != null) {
            for (Bitmap bitmap : mListBanners) {
                bitmap.recycle();
            }
            mListBanners.clear();
        }
        System.gc();
    }
}
