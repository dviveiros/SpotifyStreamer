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
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * AsyncTask to fetch the top 10 tracks for a specific artist
 * Created by dviveiros on 24/06/15.
 */
public class FetchTracksTask  extends AsyncTask<String, Void, Track[]> {

    /** Log tag */
    private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

    private TopTracksFragment mTopTracksFragment;
    private ArrayAdapter mAdapter;
    private String mAccessToken;

    /**
     * Constructor
     * @param adapter Adapter to be populated after fetching the artist list
     *
     */
    public FetchTracksTask( TopTracksFragment topTracksFragment, ArrayAdapter adapter, String accessToken ) {
        mTopTracksFragment = topTracksFragment;
        mAdapter = adapter;
        mAccessToken = accessToken;
    }

    @Override
    protected void onPostExecute(Track[] tracks) {
        super.onPostExecute(tracks);

        mAdapter.clear();

        if (tracks.length > 0) {
            List<Track> trackList = new ArrayList<Track>(Arrays.asList(tracks));
            for (Track track: trackList) {
                mAdapter.add(track);
            }
            mTopTracksFragment.setTrackList(trackList);
        } else {
            String msgNotFound = mTopTracksFragment.getResources().getString(R.string.top_tracks_not_found);
            Toast noItensToast = Toast.makeText(mTopTracksFragment.getActivity(),
                    msgNotFound, Toast.LENGTH_LONG);
            noItensToast.show();
            mTopTracksFragment.setTrackList(new ArrayList<Track>());
        }
    }

    @Override
    protected Track[] doInBackground(String... params) {

        List<Track> tracksFound = new ArrayList<Track>();

        String artistId = null;
        if (params.length == 1) {
            artistId = params[0];
        } else {
            String message = "Artist ID expected. Found length = " + params.length;
            Log.e(LOG_TAG, message);
            throw new RuntimeException( message );
        }

        //instantiates the spotify API
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();

        Tracks tracks = spotify.getArtistTopTrack(artistId);
        for (Track track: tracks.tracks) {
            tracksFound.add(track);
        }
        Track[] result = tracksFound.toArray(new Track[tracksFound.size()]);

        return result;
    }
}