package com.danielviveiros.spotifystreamer.track;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.danielviveiros.spotifystreamer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean mErrorState;

    /**
     * Constructor
     * @param adapter Adapter to be populated after fetching the artist list
     *
     */
    public FetchTracksTask( TopTracksFragment topTracksFragment, ArrayAdapter adapter, String accessToken ) {
        mTopTracksFragment = topTracksFragment;
        mAdapter = adapter;
        mAccessToken = accessToken;
        mErrorState = false;
    }

    @Override
    protected void onPostExecute(Track[] tracks) {
        super.onPostExecute(tracks);

        if (!mErrorState) {
            mAdapter.clear();
            if (tracks.length > 0) {
                List<Track> trackList = new ArrayList<Track>(Arrays.asList(tracks));
                for (Track track : trackList) {
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
        } else {
            Toast toast = Toast.makeText(mTopTracksFragment.getActivity().getBaseContext(),
                    mTopTracksFragment.getResources().getText(R.string.top_tracks_filter_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected Track[] doInBackground(String... params) {

        List<Track> tracksFound = new ArrayList<Track>();

        String artistId;
        if (params.length == 1) {
            artistId = params[0];
        } else {
            String message = "StreamerArtist ID expected. Found length = " + params.length;
            Log.e(LOG_TAG, message);
            throw new RuntimeException( message );
        }

        //gets the country
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                mTopTracksFragment.getActivity().getBaseContext());
        String country = prefs.getString(
                mTopTracksFragment.getResources().getString(R.string.pref_country_key),
                mTopTracksFragment.getResources().getString(R.string.pref_country_default));
        Map<String,Object> mapParams = new HashMap<String,Object>();
        if (country != null) {
            mapParams.put("country", country.toUpperCase());
        }

        //instantiates the spotify API
        SpotifyApi api = new SpotifyApi();

        try {
            api.setAccessToken(mAccessToken);
            SpotifyService spotify = api.getService();
            Tracks tracks = spotify.getArtistTopTrack(artistId, mapParams);
            for (Track track : tracks.tracks) {
                tracksFound.add(track);
            }
        } catch ( Exception exc ) {
            Log.e(LOG_TAG, exc.getMessage(), exc);
            mErrorState = true;
        }

        Track[] result = tracksFound.toArray(new Track[tracksFound.size()]);

        return result;
    }
}
