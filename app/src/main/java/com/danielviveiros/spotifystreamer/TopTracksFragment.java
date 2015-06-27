package com.danielviveiros.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private static final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    /** Access token */
    private String mAccessToken;

    /** Selected artist */
    private String mSelectedArtistId;
    private String mSelectedArtistName;

    /** Top tracks list */
    private List<Track> mTrackList;

    /** Adapter and ListView*/
    private TopTracksViewAdapter mTopTracksViewAdapter;
    private ListView mTopTracksListView;

    public TopTracksFragment() {
        mTrackList = new ArrayList<Track>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.toptracks_fragment, container, false);

        Intent intent = getActivity().getIntent();
        mSelectedArtistId = intent.getStringExtra(Constants.ARTIST_ID_KEY);
        mSelectedArtistName = intent.getStringExtra(Constants.ARTIST_NAME_KEY);
        mAccessToken = intent.getStringExtra(Constants.ACCESS_TOKEN_KEY);
        Log.v(LOG_TAG, "Artist id = " + mSelectedArtistId + ", artist name = " + mSelectedArtistName
                + ", access token = " + mAccessToken);

        //prepare the list view and adapter
        mTopTracksViewAdapter = new TopTracksViewAdapter(getActivity(),
                R.layout.toptracks_listitem, new ArrayList<Track>());
        mTopTracksListView = (ListView) rootView.findViewById(R.id.listview_toptracks);
        mTopTracksListView.setAdapter(mTopTracksViewAdapter);

        this.updateTopTracks();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopTracks();
    }

    /**
     * Defines the track list
     */
    public void setTrackList( List<Track> trackList ) {
        mTrackList = trackList;
    }

    /**
     * Updates the list of top tracks
     */
    private void updateTopTracks() {

        //trigger the artist fetching
        FetchTracksTask tracksTask = new FetchTracksTask(this, mTopTracksViewAdapter,
                mAccessToken);
        tracksTask.execute(mSelectedArtistId);
    }
}
