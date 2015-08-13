package com.danielviveiros.spotifystreamer.data;

import android.content.ContentValues;

import com.danielviveiros.spotifystreamer.artist.StreamerArtist;
import com.danielviveiros.spotifystreamer.util.Constants;

/**
 * Utility class for testing
 *
 * Created by dviveiros on 10/08/15.
 */
public class TestUtilities {

    /**
     * Create fake data for tracks
     * @return fake data for tracks
     */
    public static ContentValues createFakeTrack() {
        ContentValues testValues = new ContentValues();
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY, 123);
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_NAME, "Track Name");
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME, "Album Name");
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_IMAGE_URL, "Image URL");

        return testValues;
    }

    /**
     * Create fake data for artist
     * @return fake data for tracks
     */
    public static StreamerArtist createFakeArtist( String name ) {
        StreamerArtist artist = new StreamerArtist(name, Constants.DEFAULT_IMAGE);
        return artist;
    }
}
