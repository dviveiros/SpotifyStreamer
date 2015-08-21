package com.danielviveiros.spotifystreamer.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.danielviveiros.spotifystreamer.track.StreamerTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Singleton class for streamer media player
 *
 * Created by dviveiros on 15/08/15.
 */
public class MediaManager {

    private static final String LOG_TAG = MediaManager.class.getSimpleName();

    /** Events for callbacks */
    private static final int EVENT_PLAY = 0;
    private static final int EVENT_PAUSE = 1;
    private static final int EVENT_STOP = 2;
    private static final int EVENT_PREPARED = 3;
    private static final int EVENT_COMPLETION = 4;

    /** Singleton instance */
    private static MediaManager mInstance;

    /** Progress dialog */
    private ProgressDialog mProgressDialog;

    /** Media player state */
    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private String mMusicUri;
    private Map<String,MediaCallback> mMediaListeners;
    private Boolean mIsLoaded;

    /** Queue control */
    private List<StreamerTrack> mTrackQueue;
    private int positionInQueue;
    private String mArtistId;
    private String mArtistName;
    private StreamerTrack mCurrentTrack;
    private String mArtistIdLoaded;

    /**
     * Constructor
     */
    private MediaManager() {
        mMediaPlayer = new MediaPlayer();
        mMediaListeners = new HashMap<String,MediaCallback>();
        mIsLoaded = false;
        mTrackQueue = new ArrayList<>();
        positionInQueue = -1;
        mArtistId = null;
        mArtistName = null;
        mArtistIdLoaded = null;
    }

    /**
     * Returns the singleton instance of this class
     */
    public static MediaManager getInstance() {
        if (mInstance == null) {
            mInstance = new MediaManager();
        }
        return mInstance;
    }

    /**
     * Adds a listener
     */
    public void addListener( String key, MediaCallback mediaCallback ) {
        mMediaListeners.put( key, mediaCallback );
    }

    /**
     * Load a music by its URL
     */
    public void loadMusic( Context context ) {


        if ((positionInQueue < 0) || (getCurrentTrack() == null)) {
            return;
        }

        Log.v( LOG_TAG, "Loading music... Current track = " + mCurrentTrack );
        String strMusicUri = getCurrentTrack().getUrlPreview();

        if ((mMusicUri != null) && strMusicUri.equals(mMusicUri)) {
            // loading the same music that is loaded right now,
        } else {

            try {
                if ( (mProgressDialog == null) || (!mProgressDialog.isShowing()) ) {
                    mProgressDialog = ProgressDialog.show(context, "Please wait ...", "Loading music...", true);
                }
                stop();

                mMusicUri = strMusicUri;
                mMediaPlayer = new MediaPlayer();

                //ensures CPU running and wifi connection
                mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
                mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
                mWifiLock.acquire();

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mIsLoaded = true;
                        MediaManager mediaManager = MediaManager.getInstance();
                        mediaManager.notifyAll(MediaManager.EVENT_PREPARED);

                        dismissProgressDialog();
                    }
                });

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        MediaManager mediaManager = MediaManager.getInstance();
                        mediaManager.notifyAll(MediaManager.EVENT_COMPLETION);
                    }
                });

                Uri musicUri = Uri.parse(mMusicUri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(context, musicUri);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error loading media manager for URL " + strMusicUri, e);
            }
        }
    }

    /**
     * Play the music
     */
    public void play() throws PlayingMusicException {

        if (TextUtils.isEmpty(mMusicUri)) {
            throw new PlayingMusicException(mMusicUri);
        }

        try {
            mMediaPlayer.start();
        } catch ( Exception exc ) {
            throw new PlayingMusicException( mMusicUri, exc );
        }

        this.notifyAll(MediaManager.EVENT_PLAY);
    }

    /**
     * Pause the music
     */
    public void pause() {
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            this.notifyAll(MediaManager.EVENT_PAUSE);
        }
    }

    /**
     * Stop the media manager
     */
    public void stop() {
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();

        }

        mMediaPlayer.release();
        mMediaPlayer = null;
        if (mWifiLock != null) {
            mWifiLock.release();
        }

        this.notifyAll( MediaManager.EVENT_STOP );

        dismissProgressDialog();
    }

    /**
     * Moves to the next music
     */
    public void next( Context context ) {
        positionInQueue++;
        if (positionInQueue > (mTrackQueue.size()-1)) {
            positionInQueue = 0;
        }

        if (mMediaPlayer != null) {
            stop();
        }

        mCurrentTrack = mTrackQueue.get(positionInQueue);
        loadMusic(context);
    }

    /**
     * Moves to the next music
     */
    public void previous( Context context ) {
        positionInQueue--;
        if (positionInQueue < 0) {
            positionInQueue = mTrackQueue.size() - 1;
        }

        if (mMediaPlayer != null) {
            stop();
        }

        mCurrentTrack = mTrackQueue.get(positionInQueue);
        loadMusic( context );
    }

    /**
     * Is playing?
     */
    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }

    /**
     * Duration
     */
    public int getDurationInSeconds() {
        if ((mMediaPlayer == null) || (!mIsLoaded)) {
            return 0;
        } else {
            return mMediaPlayer.getDuration() / 1000;
        }
    }

    /**
     * Current position
     */
    public int getCurrentPositionInSeconds() {
        if (mMediaPlayer == null) {
            return 0;
        } else {
            return mMediaPlayer.getCurrentPosition() / 1000;
        }
    }

    /**
     * Go to a specific position
     */
    public void goToPosition( int position ) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo( position * 1000 );
        }
    }

    /**
     * Adds a track to this queue
     */
    public void setTrackList( String artistId, List<StreamerTrack> trackList ) {
        mArtistIdLoaded = artistId;
        mTrackQueue = trackList;
        positionInQueue = -1;
    }

    public void setPositionInQueue(int positionInQueue) {
        this.positionInQueue = positionInQueue;
        this.mCurrentTrack = mTrackQueue.get(positionInQueue);
    }

    public StreamerTrack getCurrentTrack() {
        return mCurrentTrack;
    }

    public String getArtistId() {
        return mArtistId;
    }

    public void setArtistId(String mArtistId) {
        this.mArtistId = mArtistId;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }

    public String getArtistIdLoaded() {
        return mArtistIdLoaded;
    }

    public void dismissProgressDialog() {
        if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Notify the observers
     */
    void notifyAll( int event ) {

        Iterator<String> keys = mMediaListeners.keySet().iterator();
        String key = null;
        MediaCallback listener = null;
        while (keys.hasNext()) {
            key = keys.next();
            listener = mMediaListeners.get(key);
            switch (event) {
                case MediaManager.EVENT_PLAY: {
                    listener.onMediaStart();
                    break;
                }
                case MediaManager.EVENT_PAUSE: {
                    listener.onMediaPause();
                    break;
                }
                case MediaManager.EVENT_STOP: {
                    listener.onMediaStop();
                    break;
                }
                case MediaManager.EVENT_PREPARED: {
                    listener.onMediaManagerPrepared();
                    break;
                }
                case MediaManager.EVENT_COMPLETION: {
                    listener.onMediaCompletion();
                    break;
                }
                default: {
                    throw new RuntimeException("Invalid event: " + event);
                }
            }
        }
    }

}
