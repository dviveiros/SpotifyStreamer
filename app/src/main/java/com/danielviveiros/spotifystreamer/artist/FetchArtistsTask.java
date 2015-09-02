package com.danielviveiros.spotifystreamer.artist;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.danielviveiros.spotifystreamer.R;
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

    /** Artist DAO */
    private ArtistRepository artistRepository;

    private ArtistFilterFragment mArtistFilterFragment;
    private boolean mErrorState;
    private boolean mNotFound;

    /**
     * Constructor
     */
    public FetchArtistsTask( ArtistFilterFragment artistFilterFragment ) {
        mArtistFilterFragment = artistFilterFragment;
        mErrorState = false;
        mNotFound = false;
        artistRepository = ArtistRepository.getInstance(artistFilterFragment.getActivity());
    }

    @Override
    protected void onPostExecute( Void v ) {
        super.onPostExecute(v);

        mArtistFilterFragment.hideProgressDialog();
        if (mErrorState) {
            Snackbar.make(mArtistFilterFragment.getView(), R.string.artist_filter_error, Snackbar.LENGTH_LONG)
                    .show();
        } else if (mNotFound) {
            Snackbar.make(mArtistFilterFragment.getView(), R.string.artist_filter_not_found, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            mArtistFilterFragment.restartLoader();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        mErrorState = false;
        mNotFound = false;

        List<StreamerArtist> artistsFound = new ArrayList<>();

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
            SpotifyService spotify = api.getService();

            ArtistsPager artistsPager = spotify.searchArtists(artistFilter);
            if (artistsPager.artists.items.size() == 0) {
                mNotFound = true;
            }
            for (Artist artist : artistsPager.artists.items) {
                StreamerArtist streamerArtist = new StreamerArtist(
                        artist.id,
                        artist.name,
                        Utilities.getSmallerImage(artist.images));
                artistsFound.add(streamerArtist);
            }

            // add to database
            int inserted = 0;
            if ( artistsFound.size() > 0 ) {
                //artistRepository.deleteAll();
                inserted = artistRepository.bulkInsert(artistsFound);
            }

            Log.d(LOG_TAG, "FetchArtists Complete. " + inserted + " Inserted");
        } catch ( Exception exc ) {
            //handles exception connecting to spotify
            Log.e(LOG_TAG, exc.getMessage(), exc);
            mErrorState = true;
        }

        return null;
    }
}
