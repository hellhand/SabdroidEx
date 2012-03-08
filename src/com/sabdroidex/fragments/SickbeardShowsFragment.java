package com.sabdroidex.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.SickBeardShowsListRowAdapter;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.HttpUtil;

public class SickbeardShowsFragment extends SABDFragment implements OnItemLongClickListener {

    private static File mExtFolder = Environment.getExternalStorageDirectory();
    private static ArrayList<Object[]> rows;
    private static Bitmap mEmptyPoster;
    private ListView mListView;
    private ScrollView mShowView;
    private AsyncImage mAsyncImage;
    private SickBeardShowsListRowAdapter mSickBeardShowsListRowAdapter;

    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SickBeardController.MESSAGE_UPDATE:

                    Object result[] = (Object[]) msg.obj;
                    // Updating rows
                    rows.clear();
                    rows.addAll((ArrayList<Object[]>) result[1]);

                    /**
                     * This might happens if a rotation occurs
                     */
                    if (mListView != null || getAdapter(mListView) != null) {
                        ArrayAdapter<Object[]> adapter = getAdapter(mListView);
                        adapter.notifyDataSetChanged();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    private FragmentActivity mParent;

    public SickbeardShowsFragment() {
    }

    public SickbeardShowsFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
        if (mEmptyPoster == null) {
            mEmptyPoster = BitmapFactory.decodeResource(mParent.getResources(), R.drawable.temp_poster);
        }
    }

    public SickbeardShowsFragment(SABDroidEx sabDroidEx, ArrayList<Object[]> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
    }

    @SuppressWarnings("unchecked")
    ArrayList<Object[]> extracted(Object[] data, int position) {
        return data == null ? null : (ArrayList<Object[]>) data[position];
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Object[]> getAdapter(ListView listView) {
        return listView == null ? null : (ArrayAdapter<Object[]>) listView.getAdapter();
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_shows);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshShows() {
        // First run setup
        if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
            return;
        }
        SickBeardController.refreshShows(messageHandler);
    }

    @Override
    public void onAttach(Activity activity) {
        mParent = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = mParent.getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        LinearLayout showView = (LinearLayout) inflater.inflate(R.layout.list, null);

        mListView = (ListView) showView.findViewById(R.id.queueList);
        showView.removeAllViews();

        mSickBeardShowsListRowAdapter = new SickBeardShowsListRowAdapter(mParent, rows);
        mListView.setAdapter(mSickBeardShowsListRowAdapter);
        mListView.setOnItemLongClickListener(this);

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 2) != null) {
            rows = extracted(data, 2);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(mListView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshShows();
        }

        return mListView;
    }

    @Override
    protected void finalize() throws Throwable {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.finalize();
    }

    @Override
    public void onDestroyView() {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        mSickBeardShowsListRowAdapter.clearBitmaps();
        super.onPause();
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshShows();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog dialog = null;
        OnClickListener onClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mAsyncImage != null && mAsyncImage.getStatus() == (AsyncTask.Status.RUNNING)) {
                    mAsyncImage.cancel(true);
                }
                mAsyncImage = null;
                dialog.dismiss();
            }
        };
        LayoutInflater inflater = LayoutInflater.from(mParent);
        mShowView = (ScrollView) inflater.inflate(R.layout.show_status, null);

        ImageView showPoster = (ImageView) mShowView.findViewById(R.id.showPoster);
        showPoster.setImageBitmap(mEmptyPoster);

        TextView showName = (TextView) mShowView.findViewById(R.id.show_name);
        showName.setText((CharSequence) rows.get(position)[0]);

        GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        gradientDrawable.setColor(Color.TRANSPARENT);

        TextView showStatus = (TextView) mShowView.findViewById(R.id.show_status);
        showStatus.setText((CharSequence) rows.get(position)[1]);
        if ("Ended".equals(rows.get(position)[1])) {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.rgb(255, 50, 50));
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.WHITE);
        }
        else {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.TRANSPARENT);
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.BLACK);
        }

        TextView showQuality = (TextView) mShowView.findViewById(R.id.show_quality);
        showQuality.setText((CharSequence) rows.get(position)[2]);
        showQuality.setTextColor(Color.WHITE);
        gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        if ("Any".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 68, 68));
        }
        if ("SD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(153, 68, 68));
        }
        if ("HD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 153, 68));
        }
        showQuality.setBackgroundDrawable(gradientDrawable);

        TextView showNextEpisode = (TextView) mShowView.findViewById(R.id.show_next_episode);
        showNextEpisode.setText((CharSequence) rows.get(position)[3]);

        TextView showNetwork = (TextView) mShowView.findViewById(R.id.show_network);
        showNetwork.setText((CharSequence) rows.get(position)[4]);

        TextView showLanguage = (TextView) mShowView.findViewById(R.id.show_language);
        showLanguage.setText((CharSequence) rows.get(position)[6]);

        if (mAsyncImage != null && mAsyncImage.getStatus() == (AsyncTask.Status.RUNNING)) {
            mAsyncImage.cancel(true);
        }
        mAsyncImage = new AsyncImage();
        mAsyncImage.execute(0, rows.get(position)[5], rows.get(position)[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
        builder.setNegativeButton(R.string.close, onClickListener);
        builder.setView(mShowView);
        dialog = builder.create();
        dialog.show();
        return true;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                ImageView showPoster = (ImageView) mShowView.findViewById(R.id.showPoster);
                showPoster.setImageBitmap(bitmap);
                showPoster.invalidate();
            }
            else {
                Toast.makeText(mParent, R.string.no_poster, Toast.LENGTH_LONG);
            }
        }
    };

    // TODO: merge wit the same function in SickBeardShowsListRowAdapter
    private class AsyncImage extends AsyncTask<Object, Void, Bitmap> {

        /**
         * 
         * @param params [1] Is the IMDB id of the TV show, [2] Is the name of the TV Show
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

            BitmapFactory.Options BgOptions = new BitmapFactory.Options();
            BgOptions.inPurgeable = true;
            BgOptions.inPreferredConfig = Config.RGB_565;
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(folderPath + File.separator + "poster.jpg", BgOptions);
            }
            catch (Exception e) {
                Log.w("ERROR", " " + e.getLocalizedMessage());
            }

            /**
             * The bitmap object is null if the BitmapFactory has been unable to decode the file. Hopefully this won't happen often
             */
            if (bitmap == null) {

                try {
                    /**
                     * We get the banner from the server
                     */
                    String url = SickBeardController.getPosterURL(SickBeardController.MESSAGE.SHOW_GETPOSTER.toString().toLowerCase(), (Integer) params[1]);
                    byte[] data = HttpUtil.getInstance().getDataAsByteArray(url);
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    /**
                     * And save it in the cache
                     */
                    FileOutputStream fileOutputStream;
                    fileOutputStream = new FileOutputStream(folderPath + File.separator + "poster.jpg");
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (Exception e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
            }

            /**
             * Waking up the main Thread
             */
            Message msg = new Message();
            msg.obj = bitmap;
            handler.sendMessage(msg);

            return bitmap;
        }
    }
}
