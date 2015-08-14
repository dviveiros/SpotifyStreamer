package com.danielviveiros.spotifystreamer.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final int PLAYER_LOADER = 2;

    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mAlbumImageUrl;
    private String mPreviewUrl;

    public PlayerActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_fragment, container, false);

        Intent intent = getActivity().getIntent();
        mArtistName = intent.getStringExtra(Constants.ARTIST_NAME_KEY );
        mTrackName = intent.getStringExtra(Constants.TRACK_NAME_KEY );
        mAlbumName = intent.getStringExtra(Constants.ALBUM_NAME_KEY );
        mAlbumImageUrl = intent.getStringExtra(Constants.ALBUM_IMAGE_KEY );
        mPreviewUrl = intent.getStringExtra(Constants.URL_PREVIEW_KEY );

        TextView artistTextView = (TextView) view.findViewById( R.id.artist_textview );
        TextView albumTextView = (TextView) view.findViewById(R.id.album_textview);
        TextView trackTextView = (TextView) view.findViewById(R.id.track_textview);
        ImageView imageView = (ImageView) view.findViewById( R.id.album_artwork );

        artistTextView.setText( mArtistName );
        albumTextView.setText( mAlbumName );
        trackTextView.setText( mTrackName );
        Picasso.with(getActivity()).load(mAlbumImageUrl).into(imageView);

        return view;
    }


}
