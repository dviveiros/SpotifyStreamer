package com.danielviveiros.spotifystreamer.artist;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.danielviveiros.spotifystreamer.media.MediaManager;

import java.util.ArrayList;
import java.util.List;


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
     * List view
     */
    private ListView mArtistsListView;

    /**
     * Filter
     */
    private String mArtistFilter;

    /**
     * List of callbacks
     */
    private List<Callback> mListCallbacks;

    /**
     * Progress dialog
     */
    private ProgressDialog mProgressDialog;

    /**
     * Media manager
     */
    private MediaManager mMediaManager;

    public ArtistFilterFragment() {
        mListCallbacks = new ArrayList<>();
        mMediaManager = MediaManager.getInstance();

    }

    /**
     * Register a callback
     */
    public void addCallback( Callback callback ) {
        mListCallbacks.add(callback);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ARTIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.artistfilter_fragment, container, false);

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
                    mMediaManager.setArtistId( artistKey );
                    mMediaManager.setArtistName( artistName );
                    for ( Callback callback: mListCallbacks ) {
                        callback.onItemSelected( artistKey, artistName );
                    }
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

                    String text = v.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        return false;
                    }

                    //check if the filter has changed
                    if ( (getArtistFilter() != null) && (text.equals(getArtistFilter())) ) {
                        return false;
                    }

                    //shows a "loading" dialog
                    mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Fetching artists ...", true);
                    getActivity().getCurrentFocus();


                    //saves the value in case the user hits the back button
                    mArtistFilter = text;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                            getActivity().getBaseContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("mArtistFilter", mArtistFilter);
                    editor.commit();

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

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {

        /**
         * When an item has been selected.
         */
        public void onItemSelected(String artistKey, String artistName);

    }

}
