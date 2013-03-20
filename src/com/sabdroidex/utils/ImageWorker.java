package com.sabdroidex.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.utils.FileUtil;
import com.utils.HttpUtil;

public class ImageWorker {
    
    public static enum ImageType {
        BANNER, POSTER, SEASON_POSTER
    };
    
    private static final String TAG = ImageWorker.class.getCanonicalName();
    
    private BitmapReader mBitmapReader = null;
    private Options bgOptions = null;
    private Bitmap mPosterBitmap = null;
    private Bitmap mBannerBitmap = null;
    private Resources mResources = null;
    
    public ImageWorker(Context context) {
        mResources = context.getResources();
        bgOptions = new Options();
        bgOptions.inPurgeable = true;
        bgOptions.inPreferredConfig = Config.RGB_565;
        mBitmapReader = new BitmapReader(0.05f);
    }
    
    public void setPosterTemp(int resId) {
        mPosterBitmap = BitmapFactory.decodeResource(mResources, resId, bgOptions);
    }
    
    public void setBannerTemp(int resId) {
        mBannerBitmap = BitmapFactory.decodeResource(mResources, resId, bgOptions);
    }
    
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing
     * logic). A memory and disk cache will be used if an {@link ImageCache} has
     * been set using {@link ImageWorker#setImageCache(ImageCache)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an
     * {@link AsyncTask} will be created to asynchronously load the bitmap.
     * 
     * @param data
     *            The URL of the image to download.
     * @param imageView
     *            The ImageView to bind the downloaded image to.
     */
    public void loadImage(ImageView imageView, ImageType imageType, String key, Object... data) {
        if (data == null) {
            return;
        }
        
        Bitmap bitmap = null;
        bitmap = mBitmapReader.getBitmapFromMemCache(key);
        if (bitmap != null) {
            // Bitmap found in memory cache
            imageView.setImageBitmap(bitmap);
        }
        else if (cancelPotentialWork(key, imageView)) {
            BitmapWorkerTask task = null;
            if (imageType == ImageType.BANNER) {
                task = new AsyncShowBanner(imageView, key);
            }
            else if (imageType == ImageType.POSTER) {
                task = new AsyncShowPoster(imageView, key);
            }
            else if (imageType == ImageType.SEASON_POSTER) {
                task = new AsyncSeasonPoster(imageView, key);
            }
            
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(data);
        }
    }
    
    /**
     * Returns true if the current work has been canceled or if there was no
     * work in progress on this image view. Returns false if the work in
     * progress deals with the same data. The work is not stopped in that case.
     */
    public static boolean cancelPotentialWork(Object key, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        
        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.key;
            if (!bitmapData.equals(key)) {
                bitmapWorkerTask.cancel(true);
                Log.d(TAG, "cancelPotentialWork - cancelled work for " + key);
            }
            else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param imageView
     *            Any imageView
     * @return Retrieve the currently active work task (if any) associated with
     *         this imageView. null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
    
    public class AsyncDrawable extends BitmapDrawable {
        
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
        
        public AsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmapWorkerTask.getLoadingBitmap());
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }
        
        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    public abstract class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
        
        private static final int FADE_IN_TIME = 127;
        
        private WeakReference<ImageView> weakViewReference = null;
        private WeakReference<Bitmap> mLoadingBitmap = null;
        public String key = null;
        
        private boolean mFade = false;
        
        protected BitmapWorkerTask(WeakReference<ImageView> weakViewReference, WeakReference<Bitmap> bitmap, String key) {
            this.weakViewReference = weakViewReference;
            this.mLoadingBitmap = bitmap;
            this.key = key;
        }
        
        protected Bitmap getLoadingBitmap() {
            return mLoadingBitmap.get();
        }

        /**
         * TODO: rewrite this jdoc
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            Options bgOptions = new Options();
            Bitmap bitmap = null;
            
            if (Preferences.isEnabled(Preferences.SICKBEARD_LOWRES)) {
                bgOptions.inSampleSize = 2;
            }
            else {
                bgOptions.inSampleSize = 1;
            }
            
            String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + params[1] + File.separator;
            folderPath = folderPath.replace(":", "");
            
            String fileName = getFilename(params);
            
            /**
             * If the cache is enabled we try to read the file from the device
             * (Default is enabled)
             */
            if (!isCancelled() && bitmap == null && Preferences.isEnabled(Preferences.SICKBEARD_CACHE)) {
                Log.i(getClass().getCanonicalName(), "Loading Bitmap for : " + params[1] + " ... trying to open file.");
                bitmap = mBitmapReader.getBitmapFromFile(folderPath, fileName, key);
            }
            
            /**
             * The bitmap object is null if the BitmapFactory has been unable to
             * decode the Bitmap or if the File does not exists.
             */
            if (!isCancelled() && bitmap == null) {
                /**
                 * We get the banner from the server
                 */
                Log.i(getClass().getCanonicalName(), "Bitmap for : " + params[1] + " not found ... trying to download file.");
                String url = getImageURL(params);
                String savePath = folderPath + File.separator + fileName;
                bitmap = mBitmapReader.getBitmapFromWeb(url, savePath, key);
            }
            
            return bitmap;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled()) {
                result = null;
            }
            if (weakViewReference != null && result != null) {
                final ImageView imageView = getAttachedImageView();
                if (imageView != null) {
                    setImage(imageView, result);
                }
            }
            weakViewReference.clear();
        }
        
        @Override
        protected void onCancelled(Bitmap result) {
            result = null;
            weakViewReference.clear();
            super.onCancelled(result);
        }
        
        private void setImage(ImageView imageView, Bitmap bitmap) {
            if (mFade) {
                // Create a transition and set the resulting image to be displayed
                TransitionDrawable td = new TransitionDrawable(new Drawable[] { new BitmapDrawable(mResources, mLoadingBitmap.get()), new BitmapDrawable(mResources, bitmap) });
                imageView.setImageDrawable(td);
                td.startTransition(FADE_IN_TIME);
            }
            else {
                imageView.setImageBitmap(bitmap);
            }
        }
        
        /**
         * Returns the ImageView associated with this task as long as the
         * ImageView's task still points to this task as well. Returns null
         * otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = weakViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            
            if (this == bitmapWorkerTask) {
                return imageView;
            }
            
            return null;
        }
        
        protected abstract String getImageURL(Object... params);
        
        protected abstract String getFilename(Object... params);
        
        protected abstract ImageType getImageType();
    }
    
    public class AsyncShowBanner extends BitmapWorkerTask {
        
        public AsyncShowBanner(ImageView imageView, String key) {
            super(new WeakReference<ImageView>(imageView), new WeakReference<Bitmap>(mBannerBitmap), key);
        }
        
        @Override
        protected String getImageURL(Object... params) {
            return SickBeardController.getImageURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[0]);
        }
        
        @Override
        protected String getFilename(Object... params) {
            return "banner.jpg";
        }
        
        @Override
        protected ImageType getImageType() {
            return ImageType.BANNER;
        }
        
    }
    
    public class AsyncShowPoster extends BitmapWorkerTask {
        
        public AsyncShowPoster(ImageView imageView, String key) {
            super(new WeakReference<ImageView>(imageView), new WeakReference<Bitmap>(mPosterBitmap), key);
        }
        
        @Override
        protected String getImageURL(Object... params) {
            return SickBeardController.getPosterURL(SickBeardController.MESSAGE.SHOW_GETPOSTER.toString().toLowerCase(), (Integer) params[0]);
        }
        
        @Override
        protected String getFilename(Object... params) {
            return "poster.jpg";
        }
        
        @Override
        protected ImageType getImageType() {
            return ImageType.POSTER;
        }
        
    }
    
    public class AsyncSeasonPoster extends BitmapWorkerTask {
        
        public AsyncSeasonPoster(ImageView imageView, String key) {
            super(new WeakReference<ImageView>(imageView), new WeakReference<Bitmap>(mPosterBitmap), key);
        }
        
        @Override
        protected String getImageURL(Object... params) {
            return SickBeardController.getSeasonPosterURL(SickBeardController.MESSAGE.SHOW_SEASONLIST.toString().toLowerCase(), (Integer) params[0], (Integer) params[2]);
        }
        
        @Override
        protected String getFilename(Object... params) {
            return "season-" + (Integer) params[2] + ".jpg";
        }
        
        @Override
        protected ImageType getImageType() {
            return ImageType.SEASON_POSTER;
        }
        
    }
    
    public class BitmapReader {
        
        private LruCache<String, Bitmap> mMemoryCache;
        
        public BitmapReader(float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.05 and 0.8 (inclusive)");
            }
            int memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
            mMemoryCache = new LruCache<String, Bitmap>(memCacheSize) {
                
                /**
                 * Measure item size in kilobytes rather than units which is
                 * more practical for a bitmap cache
                 */
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    final int bitmapSize = getBitmapSize(bitmap) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }
            };
        }
        
        /**
         * Get the size in bytes of a bitmap.
         * 
         * @param bitmap
         * @return size in bytes
         */
        @TargetApi(12)
        public int getBitmapSize(Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
        
        /**
         * Get from memory cache.
         * 
         * @param data
         *            Unique identifier for which item to get
         * @return The bitmap if found in cache, null otherwise
         */
        public Bitmap getBitmapFromMemCache(String data) {
            return mMemoryCache.get(data);
        }
        
        public Bitmap getBitmapFromFile(String folderPath, String fileName, String key) {
            
            Bitmap bitmap = null;
            byte[] data;
            
            /**
             * Trying to find Image on Local System
             */            
            try {
                data = FileUtil.getFileAsByteArray(folderPath + File.separator + fileName);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bgOptions);
                if (bitmap != null) {
                    mMemoryCache.put(key, bitmap);
                }
            }
            catch (Throwable e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
            
            return mMemoryCache.get(key);
        }
        
        public Bitmap getBitmapFromWeb(String url, String savePath, String key) {
            
            Bitmap bitmap = null;
            byte[] data;
            try {
                data = HttpUtil.getInstance().getDataAsByteArray(url);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bgOptions);
                
                /**
                 * If the user enabled cache (Default is enabled)
                 */
                if (Preferences.isEnabled(Preferences.SICKBEARD_CACHE)) {
                    /**
                     * And save it on the device
                     */
                    FileOutputStream fileOutputStream = new FileOutputStream(savePath);
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                
                if (bitmap != null) {
                    mMemoryCache.put(key, bitmap);
                }
            }
            catch (Throwable e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
            return mMemoryCache.get(key);
        }
    }
}
