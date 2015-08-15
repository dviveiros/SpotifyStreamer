package com.danielviveiros.spotifystreamer.media;

/**
 * Interface for media listeners
 *
 * Created by dviveiros on 15/08/15.
 */
public interface MediaCallback {

    /**
     * Music has started
     */
    public void onMediaStart();

    /**
     * Music has paused
     */
    public void onMediaPause();

    /**
     * Music has completed
     */
    public void onMediaCompletion();

    /**
     * Media manager has stopped
     */
    public void onMediaStop();

    /**
     * Media manager is prepared
     */
    public void onMediaManagerPrepared();
}
