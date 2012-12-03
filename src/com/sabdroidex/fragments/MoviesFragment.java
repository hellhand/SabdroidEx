package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;

public class MoviesFragment extends SABDFragment {

	private static final String TAG = "ShowsFragment";
    private FragmentActivity mParent;

    //private static ArrayList<String> rows = new ArrayList<String>();

    //private ListView listView;
    
    /**
     *  Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
        	Object result[];
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_SEARCH.ordinal()) {
                try {
                    result = (Object[]) msg.obj;
                    selectShowPrompt((ArrayList<Object[]>) result[0]);
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            else if (msg.what == CouchPotatoController.MESSAGE.MOVIE_ADD.ordinal()) {
            	if ("Error".equals(msg.obj)){
            		Toast.makeText(mParent,"Failed to add movie\nCheck settings!", Toast.LENGTH_LONG).show();
             	}
            	else if(!"".equals(msg.obj)){
            		Toast.makeText(mParent, "Added: "+msg.obj, Toast.LENGTH_LONG).show();
            	}
            }
        }
    };

    public MoviesFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_movies);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout searchView = (LinearLayout) inflater.inflate(R.layout.list, null);

        //listView = (ListView) searchView.findViewById(R.id.movieList);
        searchView.removeAllViews();
        //listView.setAdapter(new SearchListRowAdapter(mParent, rows));

        return searchView;
    }

    @Override
    public void onFragmentActivated() {

    }
    
    /**
     * Getter for this {@link Fragment}'s message {@link Handler}
     * 
     * @return the message {@link Handler} for this {@link Activity}
     */
    public Handler getMessageHandler() {
        return messageHandler;
    }
    
    
    @SuppressWarnings("deprecation")
	public void addMoviePrompt(){
    	
        if (!Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            mParent.showDialog(R.id.dialog_setup_prompt);
            return;
        }
        
        AlertDialog.Builder alert = new AlertDialog.Builder(mParent);

        alert.setTitle(R.string.add_movie_dialog_title);
        alert.setMessage(R.string.add_movie_dialog_message);

        final EditText input = new EditText(mParent);
        alert.setView(input);
        
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
            	System.out.println("Clicked button!");
                String value = input.getText().toString();
                Toast.makeText(mParent, mParent.getText(R.string.add_show_background_search), Toast.LENGTH_LONG).show();
                CouchPotatoController.searchMovie(getMessageHandler(), value);
            }
        });
        
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    @Override
    protected void clearAdapter() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Displays the propositions dialog with the resulting movie titles found after a user search to add a movie to CouchPotato.
     * 
     * @param result
     *            The result of the search query
     */
    private void selectShowPrompt(final ArrayList<Object[]> result) {

        AlertDialog.Builder alert = new AlertDialog.Builder(mParent);

        ArrayList<String> movies = new ArrayList<String>();
        for (Object[] show : result) {
            movies.add(show[0] + "");
        }

        if (movies.size() > 0) {
            alert.setTitle(R.string.add_show_selection_dialog_title);
        }
        else {
            alert.setTitle(R.string.add_show_not_found);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mParent, android.R.layout.simple_list_item_1, movies);
        alert.setAdapter(adapter, new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Object[] selected = result.get(which);
                CouchPotatoController.addMovie(messageHandler, Preferences.get(Preferences.COUCHPOTATO_PROFILE),
                		((String) selected[1]), ((String) selected[0]));
                dialog.dismiss();
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();
            }
        });

        alert.show();
    }
}
