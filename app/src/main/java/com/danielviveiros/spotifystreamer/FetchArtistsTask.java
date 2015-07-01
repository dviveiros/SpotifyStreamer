package com.danielviveiros.spotifystreamer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Async task to fetch Spotify artist data
 * Created by dviveiros on 23/06/15.
 */
public class FetchArtistsTask extends AsyncTask<String, Void, Artist[]> {

    /** Log tag */
    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private ArtistFilterFragment mArtistFilterFragment;
    private ArrayAdapter mAdapter;
    private String mAccessToken;
    private boolean mErrorState;

    /**
     * Constructor
     * @param adapter Adapter to be populated after fetching the artist list
     *
     */
    public FetchArtistsTask( ArtistFilterFragment artistFilterFragment, ArrayAdapter adapter, String accessToken ) {
        mArtistFilterFragment = artistFilterFragment;
        mAdapter = adapter;
        mAccessToken = accessToken;
        mErrorState = false;
    }

    @Override
    protected void onPostExecute(Artist[] artists) {
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

    @Override
    protected Artist[] doInBackground(String... params) {

        List<Artist> artistsFound = new ArrayList<Artist>();

        String artistFilter;
        if (params.length == 1) {
            artistFilter = params[0];
        } else {
            String message = "Artist filter expected. Found length = " + params.length;
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
                artistsFound.add(artist);
            }
        } catch ( Exception exc ) {
            //handles exception connecting to spotify
            Log.e(LOG_TAG, exc.getMessage(), exc);
            mErrorState = true;

        }
        Artist[] result = artistsFound.toArray(new Artist[artistsFound.size()]);

        return result;
    }
}
