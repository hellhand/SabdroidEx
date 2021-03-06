package com.sabdroidex.utils;

import android.content.Context;
import android.util.Log;

import com.sabdroidex.R;

import java.io.File;
import java.io.IOException;


public class ImageUtils {
 
    private static ImageWorker imageWorker = null;
    
    public static synchronized void initImageWorker(Context context) {
        if (imageWorker == null) {
            imageWorker = new ImageWorker(context);
            imageWorker.setSickbeardPosterTemp(R.drawable.temp_poster);
            imageWorker.setSickbeardBannerTemp(R.drawable.temp_banner);
            imageWorker.setCouchPosterBitmap(R.drawable.couch_temp_poster);
            imageWorker.setCouchBannerBitmap(R.drawable.couch_temp_banner);
        }
    }
    
    public static ImageWorker getImageWorker() {
        return imageWorker;
    }
    
    public static class NoMediaChecker {
        
        private static final String TAG = NoMediaChecker.class.getCanonicalName();
        
        public static void check(String folder) {
            File noMedia = new File(folder + File.separator + "SABDroidEx" + File.separator + ".Nomedia");
            try {
                if (Preferences.isEnabled(Preferences.DATA_NO_MEDIA)) {
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
        }
    }
}
