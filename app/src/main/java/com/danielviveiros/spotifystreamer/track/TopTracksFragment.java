package com.danielviveiros.spotifystreamer.track;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private Bundle mSavedInstanceState;

    /** Top tracks list */
    private List<Track> mTrackList;

    /** Adapter and ListView*/
    private TopTracksAdapter mTopTracksAdapter;
    private ListView mTopTracksListView;

    /**
     * Progress dialog
     */
    private ProgressDialog mProgressDialog;

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getActivity().getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        if (mSelectedArtistId != null) {
            Log.v( LOG_TAG, "Adding " + mSelectedArtistId + " as artist Id to prefs");
            editor.putString("mSelectedArtistId", mSelectedArtistId);
        }
        if (mSelectedArtistName != null) {
            Log.v(LOG_TAG, "Adding " + mSelectedArtistName + " as artist name to prefs");
            editor.putString("mSelectedArtistName", mSelectedArtistName);
        }
        editor.commit();


        Log.v(LOG_TAG, "StreamerArtist id = " + mSelectedArtistId +
                ", artist name = " + mSelectedArtistName);

        //prepare the list view and adapter
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), null, 0);
        mTopTracksListView = (ListView) rootView.findViewById(R.id.listview_toptracks);
        mTopTracksListView.setAdapter(mTopTracksAdapter);
        //set the click listener to the list view
        mTopTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor data = (Cursor) adapterView.getItemAtPosition(i);
                if (data != null) {
                    String albumName = data.getString(TrackRepository.COL_INDEX_ALBUM_NAME);
                    String albumImageUrl = data.getString(TrackRepository.COL_INDEX_FULL_ALBUM_IMAGE_URL);
                    String trackName = data.getString(TrackRepository.COL_INDEX_NAME);
                    String urlPreview = data.getString(TrackRepository.COL_INDEX_URL_PREVIEW);
                    String artistName = data.getString(TrackRepository.COL_INDEX_ARTIST_NAME);

                    Intent playerlIntent = new Intent(getActivity(), PlayerActivity.class);
                    playerlIntent.putExtra(Constants.ARTIST_NAME_KEY, artistName);
                    playerlIntent.putExtra(Constants.ALBUM_NAME_KEY, albumName);
                    playerlIntent.putExtra(Constants.ALBUM_IMAGE_KEY, albumImageUrl);
                    playerlIntent.putExtra(Constants.TRACK_NAME_KEY, trackName);
                    playerlIntent.putExtra(Constants.URL_PREVIEW_KEY, urlPreview);

                    startActivity(playerlIntent);
                }
            }
        });

        //this.updateTopTracks();
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
        mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Fetching tracks ...", true);
        FetchTracksTask tracksTask = new FetchTracksTask(this, getArtistName());
        tracksTask.execute(getArtistId());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY + " = ?";
        String[] selectionArgs = new String[]{getArtistId()};
        String sortOrder = null;
        Uri uri = SpotifyStreamerContract.TrackEntry.buildTrackByArtistUri(getArtistId());
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

    /**
     * Hide progress dialog
     */
    void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    String getArtistId() {
        if (mSelectedArtistId == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                    getActivity().getBaseContext());
            mSelectedArtistId = prefs.getString( "mSelectedArtistId", null );
        }
        return mSelectedArtistId;
    }

    String getArtistName() {
        if (mSelectedArtistName == null)  {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                    getActivity().getBaseContext());
            mSelectedArtistName = prefs.getString( "mSelectedArtistName", null );
        }
        return mSelectedArtistName;
    }
}
