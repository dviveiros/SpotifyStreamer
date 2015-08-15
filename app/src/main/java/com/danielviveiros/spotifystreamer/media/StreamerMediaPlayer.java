package com.danielviveiros.spotifystreamer.media;

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
public class StreamerMediaPlayer {

    private static final String LOG_TAG = StreamerMediaPlayer.class.getSimpleName();

    /** Events for callbacks */
    private static final int EVENT_PLAY = 0;
    private static final int EVENT_PAUSE = 1;
    private static final int EVENT_STOP = 2;
    private static final int EVENT_PREPARED = 3;
    private static final int EVENT_COMPLETION = 4;

    /** Singleton instance */
    private static StreamerMediaPlayer mInstance;


    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private String mMusicUri;
    private Map<String,MediaCallback> mMediaListeners;
    private Boolean mIsLoaded;
    private List<StreamerTrack> mTrackQueue;
    private int positionInQueue;

    /**
     * Constructor
     */
    private StreamerMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaListeners = new HashMap<String,MediaCallback>();
        mIsLoaded = false;
        mTrackQueue = new ArrayList<>();
        positionInQueue = -1;
    }

    /**
     * Returns the singleton instance of this class
     */
    public static StreamerMediaPlayer getInstance() {
        if (mInstance == null) {
            mInstance = new StreamerMediaPlayer();
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
        String strMusicUri = getCurrentTrack().getUrlPreview();

        if ((mMusicUri != null) && strMusicUri.equals(mMusicUri)) {
            // loading the same music that is loaded right now,
        } else {

            try {
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
                        StreamerMediaPlayer streamerMediaPlayer = StreamerMediaPlayer.getInstance();
                        streamerMediaPlayer.notifyAll(StreamerMediaPlayer.EVENT_PREPARED);
                    }
                });

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        StreamerMediaPlayer streamerMediaPlayer = StreamerMediaPlayer.getInstance();
                        streamerMediaPlayer.notifyAll(StreamerMediaPlayer.EVENT_COMPLETION);
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

        this.notifyAll(StreamerMediaPlayer.EVENT_PLAY);
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
            this.notifyAll(StreamerMediaPlayer.EVENT_PAUSE);
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

        this.notifyAll( StreamerMediaPlayer.EVENT_STOP );
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
     * Resets the queue
     */
    public void resetQueue() {
        positionInQueue = -1;
        mTrackQueue.clear();
    }

    /**
     * Adds a track to this queue
     */
    public void addTrackToQueue( StreamerTrack track ) {
        mTrackQueue.add(track);
    }

    public int getPositionInQueue() {
        return positionInQueue;
    }

    public void setPositionInQueue(int positionInQueue) {
        this.positionInQueue = positionInQueue;
    }

    public StreamerTrack getCurrentTrack() {
        if (positionInQueue == -1) {
            return null;
        }
        return mTrackQueue.get( positionInQueue );
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
                case StreamerMediaPlayer.EVENT_PLAY: {
                    listener.onMediaStart();
                    break;
                }
                case StreamerMediaPlayer.EVENT_PAUSE: {
                    listener.onMediaPause();
                    break;
                }
                case StreamerMediaPlayer.EVENT_STOP: {
                    listener.onMediaStop();
                    break;
                }
                case StreamerMediaPlayer.EVENT_PREPARED: {
                    listener.onMediaManagerPrepared();
                    break;
                }
                case StreamerMediaPlayer.EVENT_COMPLETION: {
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
