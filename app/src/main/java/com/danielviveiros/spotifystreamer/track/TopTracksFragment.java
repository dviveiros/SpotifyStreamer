package com.danielviveiros.spotifystreamer.track;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.util.Constants;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    /**
     * Loader ID
     */
    private static final int TRACKS_LOADER = 1;

    /** Selected artist */
    private String mSelectedArtistId;
    private String mSelectedArtistName;

    /** Top tracks list */
    private List<Track> mTrackList;

    /** Adapter and ListView*/
    private TopTracksAdapter mTopTracksAdapter;
    private ListView mTopTracksListView;

    public TopTracksFragment() {
        mTrackList = new ArrayList<Track>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRACKS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.toptracks_fragment, container, false);

        Intent intent = getActivity().getIntent();
        mSelectedArtistId = intent.getStringExtra(Constants.ARTIST_ID_KEY);
        mSelectedArtistName = intent.getStringExtra(Constants.ARTIST_NAME_KEY);
        Log.v(LOG_TAG, "StreamerArtist id = " + mSelectedArtistId +
                ", artist name = " + mSelectedArtistName);

        //prepare the list view and adapter
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), null, 0);
        mTopTracksListView = (ListView) rootView.findViewById(R.id.listview_toptracks);
        mTopTracksListView.setAdapter(mTopTracksAdapter);

        this.updateTopTracks();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopTracks();
    }

    /**
     * Updates the list of top tracks
     */
    private void updateTopTracks() {
        FetchTracksTask tracksTask = new FetchTracksTask(this);
        tracksTask.execute(mSelectedArtistId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY + " = ?";
        String[] selectionArgs = new String[]{mSelectedArtistId};
        String sortOrder = null;
        Uri uri = SpotifyStreamerContract.TrackEntry.buildTrackByArtistUri(mSelectedArtistId);
        return new CursorLoader(getActivity(), uri,
                TrackRepository.FULL_PROJECTION, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTopTracksAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTopTracksAdapter.swapCursor(null);
    }

    void restartLoader() {
        getLoaderManager().restartLoader(TRACKS_LOADER, null, this);
    }
}
