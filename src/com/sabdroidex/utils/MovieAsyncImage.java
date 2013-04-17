package com.sabdroidex.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.utils.HttpUtil;

public class MovieAsyncImage extends AsyncTask<Object, Void, Void> {

    private static final String TAG = MovieAsyncImage.class.getCanonicalName();
    
    private static File mExtFolder = Environment.getExternalStorageDirectory();
    
    /**
     * This method is a background worker and notifies us with a {@link Message} when is has finished
     * 
     * @param params [0] Is the item position in the list, [1] is the ImageHandler, [2] is the Poster URL, [3] Is the title of the movie
     * @return
     */
    @Override
    protected Void doInBackground(Object... params) {

        Bitmap bitmap = null;
        Options BgOptions = new Options();
        BgOptions.inPurgeable = true;
        BgOptions.inPreferredConfig = Config.RGB_565;
        if (Preferences.isEnabled(Preferences.COUCHPOTATO_LOWRES)) {
            BgOptions.inSampleSize = 2;
        }
        
        String folderPath = mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + params[3] + File.separator;
        folderPath = folderPath.replace(":", "");

        Handler handler = (Handler) params[1];
        String fileName = "poster.jpg";

        if (Preferences.isEnabled(Preferences.COUCHPOTATO_CACHE)) {
            
            Log.i(TAG, "Loading Bitmap for : " + params[3] + " ... trying to open file.");
            
            File noMedia = new File(mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + ".Nomedia");
            try {
                if (Preferences.isEnabled(Preferences.COUCHPOTATO_NOMEDIA)) {
                    if (!noMedia.exists()) {
                        noMedia.createNewFile();
                    }
                }
                else {
                    if (noMedia.exists()) {
                        noMedia.delete();
                    }
                }
            }
            catch (IOException e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }

            /**
             * Trying to find Image on Local System
             */
            File folder = new File(folderPath);
            folder.mkdirs();

            try {
                bitmap = BitmapFactory.decodeFile(folderPath + File.separator + fileName, BgOptions);
            }
            catch (Throwable e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
        }
        
        /**
         * The bitmap object is null if the BitmapFactory has been unable to decode the file. Hopefully this won't happen often
         */
        if (bitmap == null) {
            
            Log.i(TAG, "Bitmap for : " + params[3] + " not found ... trying to download file.");
            
            /**
             * We get the banner from the server
             */
            String url = (String) params[2];
            
            byte[] data;
            try {
                data = HttpUtil.getInstance().getDataAsByteArray(url);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, BgOptions);

                if (Preferences.isEnabled(Preferences.COUCHPOTATO_CACHE)) {
                    /**
                     * And save it in the cache
                     */
                    FileOutputStream fileOutputStream;
                    fileOutputStream = new FileOutputStream(folderPath + File.separator + fileName);
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            catch (Throwable e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
                return null;
            }
        }

        /**
         * Waking up the main Thread
         */
        Message msg = new Message();
        msg.obj = bitmap;
        handler.sendMessage(msg);

        return null;
    }
}