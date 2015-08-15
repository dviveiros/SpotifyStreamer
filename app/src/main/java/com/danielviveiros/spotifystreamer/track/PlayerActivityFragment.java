package com.danielviveiros.spotifystreamer.track;

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

    /** UI components */
    private TextView mArtistTextView;
    private TextView mAlbumTextView;
    private TextView mTrackTextView;
    private ImageView mImageView;

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

        //UI components
        mArtistTextView = (TextView) view.findViewById( R.id.artist_textview );
        mAlbumTextView = (TextView) view.findViewById(R.id.album_textview);
        mTrackTextView = (TextView) view.findViewById(R.id.track_textview);
        mImageView = (ImageView) view.findViewById( R.id.album_artwork );
        mSeekBar = (SeekBar) view.findViewById( R.id.seekbar_player );
        mPlayButton = (ImageButton) view.findViewById( R.id.play_button);
        mPreviousButton = (ImageButton) view.findViewById( R.id.previous_button);
        mNextButton = (ImageButton) view.findViewById( R.id.next_button);

        //seekbar
        mSeekBar.setOnSeekBarChangeListener(this);
        mTotalLengthTextView = (TextView) view.findViewById( R.id.end_song_textview );
        setupSeekBar();

        //load data into the UI
        this.loadTrackOnUI();

        //button controls
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
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.next( getActivity() );
            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.previous(getActivity());
            }
        });

        //loads the media player
        mMediaPlayer.addListener( "PlayerActivityFragment", this );
        mMediaPlayer.loadMusic(getActivity());
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
        loadTrackOnUI();
        setupSeekBar();
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
     * Setup the UI
     */
    private void loadTrackOnUI() {
        StreamerTrack track = mMediaPlayer.getCurrentTrack();

        mArtistTextView.setText( track.getArtistName() );
        mAlbumTextView.setText( track.getAlbumName() );
        mTrackTextView.setText( track.getName() );
        Picasso.with(getActivity()).load(track.getAlbumFullImageUrl()).into( mImageView );
    }

    /**
     * Setup the seek bar
     */
    private void setupSeekBar() {
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

        String strTotalLength = "--:--";
        int durationInSeconds = mMediaPlayer.getDurationInSeconds();

        if (durationInSeconds > 0) {
            double minutes = Math.floor(durationInSeconds / 60);
            double seconds = Math.floor(durationInSeconds % 60);

            strTotalLength = mFormat.format( minutes ) + ":" + mFormat.format( seconds );
        }

        mTotalLengthTextView.setText( strTotalLength );
    }

}
