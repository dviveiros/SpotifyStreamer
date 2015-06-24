package com.danielviveiros.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private static final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    /** Access token */
    private String mAccessToken;

    /** Selected artist */
    private String mSelectedArtistId;
    private String mSelectedArtistName;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.toptracks_fragment, container, false);

        Intent intent = getActivity().getIntent();
        mSelectedArtistId = intent.getStringExtra("selectedArtistId");
        mSelectedArtistName = intent.getStringExtra("selectedArtistName");
        mAccessToken = intent.getStringExtra("accessToken");
        Log.v(LOG_TAG, "Artist id = " + mSelectedArtistId + ", artist name = " + mSelectedArtistName
                + ", access token = " + mAccessToken);


        return rootView;
    }
}
