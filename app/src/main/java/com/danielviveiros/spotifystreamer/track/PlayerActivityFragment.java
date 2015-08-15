package com.danielviveiros.spotifystreamer.track;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    private static final int PLAYER_LOADER = 2;

    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mAlbumImageUrl;
    private String mPreviewUrl;

    /** Buttons */
    private ImageButton mPlayButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;

    private MediaPlayer mMediaPlayer;
    private boolean mIsPlaying = false;

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
        trackTextView.setText(mTrackName);
        Picasso.with(getActivity()).load(mAlbumImageUrl).into(imageView);

        //buttons
        mPlayButton = (ImageButton) view.findViewById( R.id.play_button);
        mPreviousButton = (ImageButton) view.findViewById( R.id.previous_button);
        mNextButton = (ImageButton) view.findViewById( R.id.next_button);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    stopMusic();
                } else {
                    playMusic();
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        if (mIsPlaying) {
            stopMusic();
        }
        super.onPause();
    }

    /**
     * Play the music
     */
    private void playMusic() {
        if (TextUtils.isEmpty( mPreviewUrl )) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(),
                    this.getResources().getText(R.string.music_not_playable),
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                Uri musicUri = Uri.parse(mPreviewUrl);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC );
                mMediaPlayer.setDataSource(getActivity(), musicUri);
                mMediaPlayer.prepare();
            }

            mMediaPlayer.start();
            mIsPlaying = true;
            mPlayButton.setImageResource( R.drawable.ic_pause_black_24dp );
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error playing music: " + mPreviewUrl, e);
        }
    }

    /**
     * Stop the music
     */
    private void stopMusic() {
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mIsPlaying = false;
            mPlayButton.setImageResource( R.drawable.ic_play_arrow_black_24dp );
        }
    }


}
