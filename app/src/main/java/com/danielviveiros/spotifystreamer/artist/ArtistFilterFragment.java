package com.danielviveiros.spotifystreamer.artist;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Log;
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

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.main.MainActivity;
import com.danielviveiros.spotifystreamer.track.TopTracksActivity;
import com.danielviveiros.spotifystreamer.util.Constants;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFilterFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Log tag
     */
    private static final String LOG_TAG = ArtistFilterFragment.class.getSimpleName();

    /**
     * Loader ID
     */
    private static final int ARTIST_LOADER = 0;

    /**
     * Adapter to deal with the list of artists
     */
    private ArtistAdapter mArtistAdapter;

    /**
     * Reference to MainActivity
     */
    private MainActivity mMainActivity;

    /**
     * List view
     */
    private ListView mArtistsListView;

    /**
     * Filter
     */
    private String mArtistFilter;

    /**
     * List of artists being shown
     */
    private List<Artist> mArtistList;

    /**
     * Progress dialog
     */
    private ProgressDialog mProgressDialog;

    public ArtistFilterFragment() {
        mArtistList = new ArrayList<Artist>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, ">> onActivityCreated(): Initializing loader");
        getLoaderManager().initLoader(ARTIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.artistfilter_fragment, container, false);

        //gets a reference to the main activity
        mMainActivity = (MainActivity) this.getActivity();

        //creates the adapter and sets into the list view
        mArtistAdapter = new ArtistAdapter(getActivity(), null, 0);
        mArtistsListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mArtistsListView.setAdapter(mArtistAdapter);

        //set the click listener to the list view
        mArtistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    String artistKey = cursor.getString(ArtistRepository.COL_INDEX_KEY);
                    String artistName = cursor.getString(ArtistRepository.COL_INDEX_NAME);
                    Intent detailIntent = new Intent(getActivity(), TopTracksActivity.class)
                            .putExtra(Constants.ARTIST_ID_KEY, artistKey)
                            .putExtra(Constants.ARTIST_NAME_KEY, artistName);
                    startActivity(detailIntent);
                }
            }
        });

        //set the listener for the edit text
        EditText artistEditText = (EditText) rootView.findViewById(R.id.artist_filter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getActivity().getBaseContext());
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //shows a "loading" dialog
                    mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Fetching artists ...", true);
                    getActivity().getCurrentFocus();
                    mArtistFilter = v.getText().toString();

                    //saves the value in case the user hits the back button
                    if (!TextUtils.isEmpty(mArtistFilter)) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                                getActivity().getBaseContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("mArtistFilter", mArtistFilter);
                        editor.commit();
                    }

                    //updates the list
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

        //if there is a previously selected artist, define it and triggers the update
        mArtistFilter = prefs.getString("mArtistFilter", null);
        if (!TextUtils.isEmpty(mArtistFilter)) {
            artistEditText.setText(mArtistFilter);
            updateArtistList();
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = SpotifyStreamerContract.ArtistEntry.COLUMN_NAME + " like ?";
        String[] selectionArgs = new String[]{mArtistFilter + "%"};
        String sortOrder = SpotifyStreamerContract.ArtistEntry.COLUMN_NAME + " ASC";
        Uri artistByNameUri = SpotifyStreamerContract.ArtistEntry.buildArtistByName(mArtistFilter);
        return new CursorLoader(getActivity(), artistByNameUri,
                ArtistRepository.FULL_PROJECTION, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArtistAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArtistAdapter.swapCursor(null);
    }

    /**
     * Restarts the loader
     */
    void restartLoader() {
        this.getLoaderManager().restartLoader(ArtistFilterFragment.ARTIST_LOADER, null, this);
    }

    /**
     * Hide progress dialog
     */
    void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Updates the list of artists shown
     */
    private void updateArtistList() {
        //trigger the artist fetching
        FetchArtistsTask artistsTask = new FetchArtistsTask(this);
        artistsTask.execute(getArtistFilter());
    }

    String getArtistFilter() {
        if ( TextUtils.isEmpty(mArtistFilter) ) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                    getActivity().getBaseContext());
            mArtistFilter = prefs.getString("mArtistFilter", null);
        }
        return mArtistFilter;
    }

}
