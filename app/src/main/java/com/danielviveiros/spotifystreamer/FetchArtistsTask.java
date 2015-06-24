package com.danielviveiros.spotifystreamer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
public class FetchArtistsTask extends AsyncTask<String, Void, String[]> {

    /** Log tag */
    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private ArrayAdapter mAdapter;
    private String mAccessToken;

    /**
     * Constructor
     * @param adapter Adapter to be populated after fetching the artist list
     *
     */
    public FetchArtistsTask( ArrayAdapter adapter, String accessToken ) {
        mAdapter = adapter;
        mAccessToken = accessToken;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        List<String> artists = new ArrayList<String>(Arrays.asList(strings));

        mAdapter.clear();
        for (String artist: artists) {
            mAdapter.add(artist);
        }
    }

    @Override
    protected String[] doInBackground(String... params) {

        List<String> artistsFound = new ArrayList<String>();

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
            artistsFound.add( artist.name );
            Log.v(LOG_TAG, "Artist found: " + artist.name);
        }
        String[] result = artistsFound.toArray(new String[artistsFound.size()]);

        return result;
    }
}
