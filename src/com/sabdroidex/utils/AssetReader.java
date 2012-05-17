package com.sabdroidex.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.content.Context;
import android.util.Log;

public class AssetReader {

    /**
     * This method returns the content of a row file as text.
     * 
     * @param context
     *            The application context
     * @param ressourceID
     *            The raw resource id to read
     * @return rawText The text contained in the raw file
     */
    public static String readTextAsset(Context context, String assetName) {

        String assetText = "";

        try {
            InputStream is = context.getResources().getAssets().open(assetName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                assetText += line;
                assetText += "\n";
                line = null;
            }
            reader.close();
            is.close();
        }
        catch (Exception e) {
            Log.wtf("ASSET ERROR", e.getMessage(), e);
        }

        return assetText;
    }
}
