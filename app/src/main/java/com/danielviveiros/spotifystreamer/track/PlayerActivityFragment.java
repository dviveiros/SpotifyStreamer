package com.danielviveiros.spotifystreamer.track;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.media.MediaCallback;
import com.danielviveiros.spotifystreamer.media.StreamerMediaPlayer;
import com.danielviveiros.spotifystreamer.util.Constants;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment
        implements MediaCallback, SeekBar.OnSeekBarChangeListener {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    private static final DecimalFormat mFormat = new DecimalFormat( "## ");

    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mAlbumImageUrl;
    private String mPreviewUrl;

    /** Buttons */
    private ImageButton mPlayButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;

    /** Seek bar */
    private SeekBar mSeekBar;
    private TextView mTotalLengthTextView;
    private Handler mHandler = new Handler();

    private StreamerMediaPlayer mMediaPlayer = StreamerMediaPlayer.getInstance();
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

        //seekbar
        mSeekBar = (SeekBar) view.findViewById( R.id.seekbar_player );
        mSeekBar.setOnSeekBarChangeListener(this);
        mTotalLengthTextView = (TextView) view.findViewById( R.id.end_song_textview );
        setTotalLength();

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
                if (mMediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });


        mMediaPlayer.addListener( "PlayerActivityFragment", this );
        mMediaPlayer.loadMusic(mPreviewUrl, getActivity());
        if (mMediaPlayer.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.ic_pause_black_24dp);
        }



        return view;
    }

    @Override
    public void onMediaStart() {
        mPlayButton.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    @Override
    public void onMediaPause() {
        mPlayButton.setImageResource( R.drawable.ic_play_arrow_black_24dp );
    }

    @Override
    public void onMediaStop() {
        mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    @Override
    public void onMediaCompletion() {
        mPlayButton.setImageResource( R.drawable.ic_play_arrow_black_24dp );
        mSeekBar.setProgress(0);
        mMediaPlayer.goToPosition(0);
    }

    @Override
    public void onMediaManagerPrepared() {
        setTotalLength();
        int durationInSeconds = mMediaPlayer.getDurationInSeconds();
        mSeekBar.setMax(durationInSeconds);
        //Make sure you update Seekbar on UI thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = mMediaPlayer.getCurrentPositionInSeconds();
                mSeekBar.setProgress(currentPosition);
                mHandler.postDelayed(this, 1000);
            }
        });
        playMusic();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mMediaPlayer.goToPosition(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Play the music
     */
    private void playMusic() {
        try {
            mMediaPlayer.play();
        } catch ( Exception exc ) {
            Log.e(LOG_TAG, "Error playing music: " + mPreviewUrl, exc);
            Toast toast = Toast.makeText(getActivity().getBaseContext(),
                    this.getResources().getText(R.string.error_playing_music),
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }

    /**
     * Pause the music
     */
    private void pauseMusic() {
        mMediaPlayer.pause();
    }

    /**
     * Stop the music
     */
    private void stopMusic() {
        mMediaPlayer.stop();
    }

    private void setTotalLength() {

        String strTotalLength = "00:00";
        int durationInSeconds = mMediaPlayer.getDurationInSeconds();

        if (durationInSeconds > 0) {
            double minutes = Math.floor(durationInSeconds / 60);
            double seconds = Math.floor(durationInSeconds % 60);

            strTotalLength = mFormat.format( minutes ) + ":" + mFormat.format( seconds );
        }

        mTotalLengthTextView.setText( strTotalLength );
    }

}
