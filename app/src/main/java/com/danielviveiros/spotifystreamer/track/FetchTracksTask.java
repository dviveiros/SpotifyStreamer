package com.danielviveiros.spotifystreamer.track;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.util.Utilities;

import java.util.ArrayList;
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
public class FetchTracksTask  extends AsyncTask<String, Void, Void> {

    /** Log tag */
    private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

    private TopTracksFragment mTopTracksFragment;
    private boolean mErrorState;
    private boolean mNotFound;
    private TrackRepository trackRepository;
    private String mArtistName;

    /**
     * Constructor
     *
     */
    public FetchTracksTask( TopTracksFragment topTracksFragment, String artistName ) {
        mTopTracksFragment = topTracksFragment;
        mErrorState = false;
        mNotFound = false;
        trackRepository = trackRepository.getInstance( topTracksFragment.getActivity() );
        mArtistName = artistName;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        mTopTracksFragment.hideProgressDialog();

        if (mErrorState) {
            Toast toast = Toast.makeText(mTopTracksFragment.getActivity().getBaseContext(),
                    mTopTracksFragment.getResources().getText(R.string.top_tracks_filter_error),
                    Toast.LENGTH_LONG);
            toast.show();
        } else if (mNotFound) {
            String msgNotFound = mTopTracksFragment.getResources().getString(R.string.top_tracks_not_found);
            Toast noItensToast = Toast.makeText(mTopTracksFragment.getActivity(),
                    msgNotFound, Toast.LENGTH_LONG);
            noItensToast.show();
        } else {
            mTopTracksFragment.restartLoader();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        List<StreamerTrack> tracksFound = new ArrayList<StreamerTrack>();

        String artistId;
        if (params.length == 1) {
            artistId = params[0];
            if (artistId == null) {
                Log.w(LOG_TAG, "Artist ID = null, this should not happen. Returning...");
                return null;
            }
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
            SpotifyService spotify = api.getService();
            Tracks tracks = spotify.getArtistTopTrack(artistId, mapParams);
            for (Track track : tracks.tracks) {
                StreamerTrack streamerTrack = new StreamerTrack(
                        artistId,
                        track.id,
                        track.name,
                        track.album.name,
                        Utilities.getSmallerImage(track.album.images),
                        Utilities.getLargerImage(track.album.images),
                        track.duration_ms,
                        track.preview_url,
                        mArtistName);
                tracksFound.add(streamerTrack);
            }

            // add to database
            int inserted = 0;
            if ( tracksFound.size() > 0 ) {
                inserted = trackRepository.bulkInsert(tracksFound);
            }

            Log.d(LOG_TAG, "FetchTracks Complete. " + inserted + " Inserted");
        } catch ( Exception exc ) {
            Log.e(LOG_TAG, exc.getMessage(), exc);
            mErrorState = true;
        }

        return null;
    }
}
