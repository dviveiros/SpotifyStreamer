package com.danielviveiros.spotifystreamer.data;

import android.content.ContentValues;

/**
 * Created by dviveiros on 10/08/15.
 */
public class TestUtilities {

    /**
     * Create fake data for tracks
     * @return fake data for tracks
     */
    public static ContentValues createFakeTrack() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY, 123);
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_NAME, "Track Name");
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME, "Album Name");
        testValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_IMAGE_URL, "Image URL");

        return testValues;
    }
}
