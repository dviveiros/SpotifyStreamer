package com.danielviveiros.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for entities created for Spotify Streamer
 *
 * Created by dviveiros on 10/08/15.
 */
public class SpotifyStreamerContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.danielviveiros.spotifystreamer";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Root for artists
    public static final String PATH_ARTIST = "artist";
    // Root for tracks
    public static final String PATH_TRACK = "track";

    /*
        Inner class that defines the table contents of the artist table
     */
    public static final class ArtistEntry implements BaseColumns {

        public static final String TABLE_NAME = "artist";

        //Artist ID
        public static final String COLUMN_ID = "id";
        //Artist Key
        public static final String COLUMN_KEY = "key";
        //Artist name
        public static final String COLUMN_NAME = "name";
        //Artist image URL
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArtistByName(String artistName) {
            return CONTENT_URI.buildUpon().appendPath(artistName).build();
        }

        public static String getArtistNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /*
        Inner class that defines the table contents of the track table
     */
    public static final class TrackEntry implements BaseColumns {

        public static final String TABLE_NAME = "track";

        //Track ID
        public static final String COLUMN_ID = "id";
        //Artist FK
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        //Track key
        public static final String COLUMN_KEY = "key";
        //Track name
        public static final String COLUMN_NAME = "name";
        //Album name
        public static final String COLUMN_ALBUM_NAME = "album_name";
        //Album artwork image URL
        public static final String COLUMN_ALBUM_IMAGE_URL = "album_image_url";
        //Album artwork FULL image URL
        public static final String COLUMN_FULL_ALBUM_IMAGE_URL = "full_album_image_url";
        //Track duration
        public static final String COLUMN_DURATION = "duration";
        //Track preview URL
        public static final String COLUMN_PREVIEW_URL = "preview_url";
        //Artist name
        public static final String COLUMN_ARTIST_NAME = "artist_name";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrackByArtistUri(String artistKey) {
            return CONTENT_URI.buildUpon().appendPath(artistKey).build();
        }

        public static String getArtistIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
