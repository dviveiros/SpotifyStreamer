package com.danielviveiros.spotifystreamer.media;

/**
 * Exception for music streaming
 *
 * Created by dviveiros on 15/08/15.
 */
public class PlayingMusicException extends Exception {

    /**
     * Constructor
     */
    public PlayingMusicException(String uri) {
        super( "Unable to play music with URI = " + uri );
    }

    /**
     * Constructor
     */
    public PlayingMusicException(String uri, Exception exc) {
        super( "Unable to play music with URI = " + uri, exc );
    }
}
