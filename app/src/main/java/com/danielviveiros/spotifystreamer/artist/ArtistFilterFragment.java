package com.danielviveiros.spotifystreamer.artist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
import com.danielviveiros.spotifystreamer.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;


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
     * UI Components
     */
    @Bind(R.id.listview_artist) ListView mArtistsListView;
    @Bind(R.id.artist_filter) EditText mArtistEditText;

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
        ButterKnife.bind(this, rootView);

        //creates the adapter and sets into the list view
        mArtistAdapter = new ArtistAdapter(getActivity(), null, 0);
        mArtistsListView.setAdapter(mArtistAdapter);

        //if there is a previously selected artist, define it and triggers the update
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getActivity().getBaseContext());
        mArtistFilter = prefs.getString("mArtistFilter", null);
        if (!TextUtils.isEmpty(mArtistFilter)) {
            mArtistEditText.setText(mArtistFilter);
            updateArtistList();
        }

        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Handler for clicks in the artist list
     */
    @OnItemClick(R.id.listview_artist)
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


    /**
     * Handler for the artist filter text field. This method is triggered whenever the
     * action has changed and submitted
     */
    @OnEditorAction(R.id.artist_filter)
    public boolean filterArtist(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            String text = v.getText().toString();
            if (TextUtils.isEmpty(text)) {
                return false;
            }

            //check if the filter has changed
            if ((getArtistFilter() != null) && (text.equals(getArtistFilter()))) {
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
        if ((mProgressDialog != null) && (mProgressDialog.isShowing())){
            mProgressDialog.dismiss();
        }
    }

    /**
     * Updates the list of artists shown
     */
    private void updateArtistList() {
        if (Utilities.isOnline(getActivity())) {
            //trigger the artist fetching
            FetchArtistsTask artistsTask = new FetchArtistsTask(this);
            artistsTask.execute(getArtistFilter());
        } else {
            this.hideProgressDialog();
            if (getView() != null) {
                Snackbar.make(getView(), R.string.artist_filter_error, Snackbar.LENGTH_LONG)
                        .show(); // Don’t forget to show!
            }
        }
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
