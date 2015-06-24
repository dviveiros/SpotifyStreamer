package com.danielviveiros.spotifystreamer;

import android.content.Context;
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

    private Context mContext;
    private ArrayAdapter mAdapter;
    private String mAccessToken;

    /**
     * Constructor
     * @param adapter Adapter to be populated after fetching the artist list
     *
     */
    public FetchArtistsTask( Context context, ArrayAdapter adapter, String accessToken ) {
        mContext = context;
        mAdapter = adapter;
        mAccessToken = accessToken;
    }

    @Override
    protected void onPostExecute(Artist[] artists) {
        super.onPostExecute(artists);

        mAdapter.clear();

        if (artists.length > 0) {
            List<Artist> artistList = new ArrayList<Artist>(Arrays.asList(artists));
            for (Artist artist : artistList) {
                mAdapter.add(artist);
            }
        } else {
            String msgNotFound = mContext.getResources().getString(R.string.artist_filter_not_found);
            Toast noItensToast = Toast.makeText(mContext, msgNotFound, Toast.LENGTH_LONG);
            noItensToast.show();
        }
    }

    @Override
    protected Artist[] doInBackground(String... params) {

        List<Artist> artistsFound = new ArrayList<Artist>();

        String artistFilter = null;
        if (params.length == 1) {
            artistFilter = params[0];
        } else {
            String message = "Artist filter expected. Found length = " + params.length;
            Log.e(LOG_TAG, message);
            throw new RuntimeException( message );
        }

        //instantiates the spotify API
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();

        ArtistsPager artistsPager = spotify.searchArtists(artistFilter);
        for (Artist artist: artistsPager.artists.items) {
            artistsFound.add( artist );
            Log.v(LOG_TAG, "Artist found: " + artist.name);
        }
        Artist[] result = artistsFound.toArray(new Artist[artistsFound.size()]);

        return result;
    }
}
