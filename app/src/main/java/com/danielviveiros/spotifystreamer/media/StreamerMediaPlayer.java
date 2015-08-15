package com.danielviveiros.spotifystreamer.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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

    /** Singleton instance */
    private static StreamerMediaPlayer mInstance;


    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private String mMusicUri;
    private Map<String,MediaCallback> mMediaListeners;

    /**
     * Constructor
     */
    private StreamerMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaListeners = new HashMap<String,MediaCallback>();
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
    public void loadMusic( String strMusicUri, Context context ) {

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
                        StreamerMediaPlayer streamerMediaPlayer = StreamerMediaPlayer.getInstance();
                        streamerMediaPlayer.notifyAll( StreamerMediaPlayer.EVENT_PREPARED );
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
            mMediaPlayer.release();
            mMediaPlayer = null;
            mWifiLock.release();
        }

        this.notifyAll( StreamerMediaPlayer.EVENT_STOP );
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
                default: {
                    throw new RuntimeException("Invalid event: " + event);
                }
            }
        }
    }

}
