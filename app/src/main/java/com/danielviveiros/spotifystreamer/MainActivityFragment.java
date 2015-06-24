package com.danielviveiros.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    /** Log tag */
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    /** Adapter to deal with the list of artists */
    private ArtistListViewAdapter mArtistListAdapter;

    /** List view */
    private ListView mForecastListView;

    /** Filter */
    private String mArtistFilter;

    /** Spotify access token */
    private String mAccessToken;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //prepare the list view and adapter
        mArtistListAdapter = new ArtistListViewAdapter(getActivity(),
                R.layout.artist_listitem, new ArrayList<Artist>());
        mForecastListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mForecastListView.setAdapter(mArtistListAdapter);

        //set the listener for the edit text
        EditText artistEditText = (EditText) rootView.findViewById(R.id.artist_filter);
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mArtistFilter = v.getText().toString();
                    updateArtistList();
                    return true;
                }
                return false;
            }
        });

        //gets spotify access token
        MainActivity mainActivity = (MainActivity) this.getActivity();
        mAccessToken = mainActivity.getSpotifyAccessToken();

        return rootView;
    }

    /**
     * Updates the list of artists shown
     */
    private void updateArtistList() {

        //hides the keyboard
        InputMethodManager imm = (InputMethodManager)
                this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        //trigger the artist fetching
        FetchArtistsTask artistsTask = new FetchArtistsTask(mArtistListAdapter, mAccessToken);
        artistsTask.execute(mArtistFilter);
    }
}
