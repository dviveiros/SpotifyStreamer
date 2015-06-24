package com.danielviveiros.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFilterFragment extends Fragment {

    /** Log tag */
    private static final String LOG_TAG = ArtistFilterFragment.class.getName();

    /** Adapter to deal with the list of artists */
    private ArtistListViewAdapter mArtistListAdapter;

    /** Reference to MainActivity */
    private MainActivity mMainActivity;

    /** List view */
    private ListView mForecastListView;

    /** Filter */
    private String mArtistFilter;

    /** List of artists being shown */
    private List<Artist> mArtistList;

    public ArtistFilterFragment() {
        mArtistList = new ArrayList<Artist>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.artistfilter_fragment, container, false);

        //gets spotify access token
        mMainActivity = (MainActivity) this.getActivity();

        //prepare the list view and adapter
        mArtistListAdapter = new ArtistListViewAdapter(getActivity(),
                R.layout.artistfilter_listitem, new ArrayList<Artist>());
        mForecastListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mForecastListView.setAdapter(mArtistListAdapter);

        //set the click listener to the list view
        mForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mArtistList != null) {
                    Artist artist = mArtistList.get(i);
                    Intent detailIntent = new Intent(getActivity(), TopTracksActivity.class)
                            .putExtra("selectedArtistId", artist.id)
                            .putExtra("selectedArtistName", artist.name)
                            .putExtra("accessToken", mMainActivity.getSpotifyAccessToken());
                    startActivity(detailIntent);
                }
            }
        });

        //set the listener for the edit text
        EditText artistEditText = (EditText) rootView.findViewById(R.id.artist_filter);
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getActivity().getCurrentFocus();
                    mArtistFilter = v.getText().toString();
                    updateArtistList();

                    //hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    /**
     * Redefines the artist list
     */
    protected void setArtistList( List<Artist> artistList ) {
        this.mArtistList = artistList;
    }

    /**
     * Updates the list of artists shown
     */
    private void updateArtistList() {

        //trigger the artist fetching
        FetchArtistsTask artistsTask = new FetchArtistsTask(this, mArtistListAdapter,
                mMainActivity.getSpotifyAccessToken());
        artistsTask.execute(mArtistFilter);
    }
}
