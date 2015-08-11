package com.danielviveiros.spotifystreamer.artist;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerDBHelper;
import com.danielviveiros.spotifystreamer.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Async task to fetch Spotify artist data
 * Created by dviveiros on 23/06/15.
 */
public class FetchArtistsTask extends AsyncTask<String, Void, Void> {

    /** Log tag */
    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private ArtistFilterFragment mArtistFilterFragment;
    private ArrayAdapter mAdapter;
    private String mAccessToken;
    private boolean mErrorState;
    private Context mContext;

    /**
     * Constructor
     */
    public FetchArtistsTask( Context context, String accessToken ) {
        //mArtistFilterFragment = artistFilterFragment;
        //mAdapter = adapter;
        mAccessToken = accessToken;
        mErrorState = false;
        mContext = context;
    }


    /*
    @Override
    protected void onPostExecute() {
        super.onPostExecute(artists);

        if (!mErrorState) {
            mAdapter.clear();
            if (artists.length > 0) {
                List<Artist> artistList = new ArrayList<Artist>(Arrays.asList(artists));
                for (Artist artist : artistList) {
                    mAdapter.add(artist);
                }
                mArtistFilterFragment.setArtistList(artistList);
            } else {
                String msgNotFound = mArtistFilterFragment.getResources().getString(R.string.artist_filter_not_found);
                Toast noItensToast = Toast.makeText(mArtistFilterFragment.getActivity(),
                        msgNotFound, Toast.LENGTH_LONG);
                noItensToast.show();
                mArtistFilterFragment.setArtistList(new ArrayList<Artist>());
            }
        } else {
            Toast toast = Toast.makeText(mArtistFilterFragment.getActivity().getBaseContext(),
                    mArtistFilterFragment.getResources().getText(R.string.artist_filter_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
    */

    @Override
    protected Void doInBackground(String... params) {

        List<ContentValues> artistsFound = new ArrayList<ContentValues>();

        String artistFilter;
        if (params.length == 1) {
            artistFilter = params[0];
        } else {
            String message = "StreamerArtist filter expected. Found length = " + params.length;
            Log.e(LOG_TAG, message);
            throw new RuntimeException( message );
        }

        //instantiates the spotify API
        SpotifyApi api = new SpotifyApi();

        try {
            api.setAccessToken(mAccessToken);
            SpotifyService spotify = api.getService();

            ArtistsPager artistsPager = spotify.searchArtists(artistFilter);
            for (Artist artist : artistsPager.artists.items) {
                ContentValues artistValues = new ContentValues();
                artistValues.put(SpotifyStreamerContract.ArtistEntry.COLUMN_NAME, artist.name);
                artistValues.put(SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL,
                        Utilities.getSmallerImage(artist.images));
                artistsFound.add(artistValues);
            }

            // add to database
            int inserted = 0;
            if ( artistsFound.size() > 0 ) {
                deleteAllRecords();
                ContentValues[] cvArray = artistsFound.toArray(
                        new ContentValues[ artistsFound.size()]);
                inserted = mContext.getContentResolver().bulkInsert(
                        SpotifyStreamerContract.ArtistEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchArtists Complete. " + inserted + " Inserted");

        } catch ( Exception exc ) {
            //handles exception connecting to spotify
            Log.e(LOG_TAG, exc.getMessage(), exc);
            mErrorState = true;

        }


        /*
        Artist[] result = artistsFound.toArray(new Artist[artistsFound.size()]);
        return result;
        */
        return null;
    }

    /**
     * Delete all artists from the database
     */
    private void deleteAllRecords() {
        /*
        mContext.getContentResolver().delete(SpotifyStreamerContract.ArtistEntry.CONTENT_URI,
                null, null);
                */
        SpotifyStreamerDBHelper helper = new SpotifyStreamerDBHelper(mContext);
        helper.getWritableDatabase().delete(SpotifyStreamerContract.ArtistEntry.TABLE_NAME, null, null);
    }
}
