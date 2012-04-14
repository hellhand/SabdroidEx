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

import com.sabdroidex.sickbeard.SickBeardController;
import com.utils.HttpUtil;

public class AsyncImage extends AsyncTask<Object, Void, Void> {

    private static File mExtFolder = Environment.getExternalStorageDirectory();

    /**
     * This method is a background worker and notifies us with a {@link Message} when is has finished
     * 
     * @param params [0] Is the item position in the list, [1] Is the IMDB id of the TV show, [2] Is the name of the TV Show
     * @return
     */
    @Override
    protected Void doInBackground(Object... params) {

        Bitmap bitmap = null;
        Options BgOptions = new Options();
        BgOptions.inPurgeable = true;
        BgOptions.inPreferredConfig = Config.RGB_565;

        String folderPath = mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + params[3] + File.separator;
        folderPath = folderPath.replace(":", "");

        Handler handler = (Handler) params[1];
        String fileName = "";

        if (params[4] == SickBeardController.MESSAGE.SHOW_GETBANNER) {
            fileName = "banner.jpg";
        }
        if (params[4] == SickBeardController.MESSAGE.SHOW_GETPOSTER) {
            fileName = "poster.jpg";
        }

        if (Preferences.isEnabled(Preferences.SICKBEARD_CACHE)) {
            File noMedia = new File(mExtFolder.getAbsolutePath() + File.separator + "SABDroidEx" + File.separator + ".Nomedia");
            try {
                if (Preferences.isEnabled(Preferences.SICKBEARD_NOMEDIA)) {
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
                Log.w("ERROR", " " + e.getLocalizedMessage());
            }

            /**
             * Trying to find Image on Local System
             */
            File folder = new File(folderPath);
            folder.mkdirs();

            if (Preferences.isEnabled(Preferences.SICKBEARD_LOWRES)) {
                BgOptions.inSampleSize = 2;
            }
            try {
                bitmap = BitmapFactory.decodeFile(folderPath + File.separator + fileName, BgOptions);
            }
            catch (Throwable e) {
                Log.w("ERROR", " " + e.getLocalizedMessage());
            }
        }
        /**
         * The bitmap object is null if the BitmapFactory has been unable to decode the file. Hopefully this won't happen often
         */
        if (bitmap == null) {
            /**
             * We get the banner from the server
             */
            String url = "";
            if (params[4] == SickBeardController.MESSAGE.SHOW_GETBANNER) {
                url = SickBeardController.getBannerURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[2]);
            }
            if (params[4] == SickBeardController.MESSAGE.SHOW_GETPOSTER) {
                url = SickBeardController.getPosterURL(SickBeardController.MESSAGE.SHOW_GETPOSTER.toString().toLowerCase(), (Integer) params[2]);
            }
            byte[] data;
            try {
                data = HttpUtil.getInstance().getDataAsByteArray(url);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, BgOptions);

                if (Preferences.isEnabled(Preferences.SICKBEARD_CACHE)) {
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
            catch (Exception e) {
                Log.w("ERROR", " " + e.getLocalizedMessage());
                return null;
            }
        }

        /**
         * Waking up the main Thread
         */
        Message msg = new Message();
        msg.obj = bitmap;
        msg.what = (Integer) params[5];
        handler.sendMessage(msg);

        return null;
    }
}