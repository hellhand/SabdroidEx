package com.sabdroidex.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;


public class RawReader {

    /**
     * This method returns the content of a row file as text.
     * 
     * @param context
     *            The application context
     * @param ressourceID
     *            The raw resource id to read
     * @return rawText The text contained in the raw file
     */
    public static String readTextRaw(Context context, int ressourceID) {
        
        String rawText = "";
        
        try {
            InputStream is = context.getResources().openRawResource(ressourceID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    
            String line = "";
            while ((line = reader.readLine()) != null) {
                rawText += line;
                rawText += "\n";
                line = null;
            }
            reader.close();
            is.close();
        }
        catch (Exception e) {
            Log.wtf("RAW ERROR", e.getMessage(), e);
        }
        
        return rawText;
    }
}
