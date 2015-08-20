package com.danielviveiros.spotifystreamer.track;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.danielviveiros.spotifystreamer.media.MediaManager;
import com.danielviveiros.spotifystreamer.util.Constants;


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

    /** Media Player */
    private MediaManager mMediaManager;

    /** Adapter and ListView*/
    private TopTracksAdapter mTopTracksAdapter;
    private ListView mTopTracksListView;

    /** Large Screen? */
    private boolean mIsLargeScreen;

    /**
     * Progress dialog
     */
    private ProgressDialog mProgressDialog;

    public TopTracksFragment() {
        mMediaManager = MediaManager.getInstance();
        mIsLargeScreen = false;
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

        mSelectedArtistId = getArtistId();
        mSelectedArtistName = getArtistName();
        mIsLargeScreen = getIsLargeScreen();
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
                mMediaManager.setPositionInQueue(i);
                showPlayer();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopTracks();
    }

    public void showPlayer() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PlayerActivityFragment newFragment = new PlayerActivityFragment();

        if (mIsLargeScreen) {
            // The device is using a large layout, so show the player as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {

            /*// The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.player_scroll_view, newFragment)
                    .addToBackStack(null).commit();*/

            Intent playerlIntent = new Intent(getActivity(), PlayerActivity.class);
            startActivity(playerlIntent);
        }
    }

    /**
     * Updates the list of top tracks
     */
    void updateTopTracks() {
        if (getArtistId() != null) {
            mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Fetching tracks ...", true);
            FetchTracksTask tracksTask = new FetchTracksTask(this, getArtistName());
            tracksTask.execute(getArtistId());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (getArtistId() == null) {
            return null;
        }

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
        if (getLoaderManager() != null) {
            getLoaderManager().restartLoader(TRACKS_LOADER, null, this);
        }
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

        if (mSelectedArtistId != null) {
            return mSelectedArtistId;
        }

        String artistId = null;

        //check the intent
        Intent intent = getActivity().getIntent();
        if ( (intent != null) && (intent.getStringExtra(Constants.ARTIST_ID_KEY) != null)) {
            artistId = intent.getStringExtra(Constants.ARTIST_ID_KEY);
        } else {
            //check the bundle
            Bundle args = getArguments();
            if ( args != null) {
                artistId = args.getString(Constants.ARTIST_ID_KEY);
            } else {
                artistId = mMediaManager.getArtistId();
            }
        }

        return artistId;
    }

    String getArtistName() {
        if (mSelectedArtistName != null) {
            return mSelectedArtistName;
        }

        String artistName = null;

        //check the intent
        Intent intent = getActivity().getIntent();
        if ( (intent != null) && (intent.getStringExtra(Constants.ARTIST_NAME_KEY) != null)) {
            artistName = intent.getStringExtra(Constants.ARTIST_NAME_KEY);
        } else {
            //check the bundle
            Bundle args = getArguments();
            if (args != null) {
                artistName = args.getString(Constants.ARTIST_NAME_KEY);
            } else {
                artistName = mMediaManager.getArtistName();
            }
        }

        return artistName;
    }

    boolean getIsLargeScreen() {
        boolean isLargeScreen = false;

        //check the intent
        Intent intent = getActivity().getIntent();
        if ( (intent != null) && (intent.getStringExtra(Constants.IS_LARGE_SCREEN_KEY) != null)) {
            isLargeScreen = intent.getBooleanExtra(Constants.IS_LARGE_SCREEN_KEY, false);
        } else {
            //check the bundle
            Bundle args = getArguments();
            if (args != null) {
                isLargeScreen = args.getBoolean(Constants.IS_LARGE_SCREEN_KEY);
            }
        }

        return isLargeScreen;
    }
}
