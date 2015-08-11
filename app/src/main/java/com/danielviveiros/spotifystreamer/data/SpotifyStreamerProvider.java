package com.danielviveiros.spotifystreamer.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract.ArtistEntry;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract.TrackEntry;


/**
 * Content provider for Spotify Streamer entities
 *
 * Created by dviveiros on 10/08/15.
 */
public class SpotifyStreamerProvider extends ContentProvider {

    private static final String LOG_TAG = SpotifyStreamerProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // Db Helper
    private SpotifyStreamerDBHelper mDBHelper;

    //StreamerArtist basic entry
    static final int ARTIST = 100;
    //Track basic entry
    static final int TRACK = 101;
    //Find artists by name
    static final int ARTIST_BY_NAME = 102;
    //Find tracks by artist
    static final int TRACKS_BY_ARTIST = 103;


    @Override
    public boolean onCreate() {
        mDBHelper = new SpotifyStreamerDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ARTIST_BY_NAME: {
                retCursor = getArtistByName( uri, projection, sortOrder );
                break;
            }
            case TRACKS_BY_ARTIST: {
                retCursor = getTrackByArtist( uri, projection, sortOrder );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTIST_BY_NAME:
                return ArtistEntry.CONTENT_TYPE;
            case TRACKS_BY_ARTIST:
                return TrackEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARTIST: {
                long _id = db.insert(ArtistEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ArtistEntry.buildArtistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRACK: {
                long _id = db.insert(TrackEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        int rowsAffected = 0;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case ARTIST: {
                rowsAffected = db.delete(ArtistEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case TRACK: {
                rowsAffected = db.delete(TrackEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //notify if something was affected
        if (rowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsAffected = 0;

        switch (match) {
            case ARTIST: {
                rowsAffected = db.update(ArtistEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case TRACK: {
                rowsAffected = db.update(TrackEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //notify observers if something was updated
        if (rowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();
        return rowsAffected;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDBHelper.close();
        super.shutdown();
    }

    /**
     * Builds the URI Matcher for this content provider
     * @return UriMatcher instance
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //artist basic
        uriMatcher.addURI( SpotifyStreamerContract.CONTENT_AUTHORITY,
                SpotifyStreamerContract.PATH_ARTIST,
                SpotifyStreamerProvider.ARTIST);

        //tracks basic
        uriMatcher.addURI( SpotifyStreamerContract.CONTENT_AUTHORITY,
                SpotifyStreamerContract.PATH_TRACK,
                SpotifyStreamerProvider.TRACK);

        //find artists by name
        uriMatcher.addURI(SpotifyStreamerContract.CONTENT_AUTHORITY,
                SpotifyStreamerContract.PATH_ARTIST + "/*",
                SpotifyStreamerProvider.ARTIST_BY_NAME);

        //find the top tracks by artist
        uriMatcher.addURI(SpotifyStreamerContract.CONTENT_AUTHORITY,
                SpotifyStreamerContract.PATH_TRACK + "/*",
                SpotifyStreamerProvider.TRACKS_BY_ARTIST);

        return uriMatcher;
    }

    private Cursor getArtistByName( Uri uri, String[] projection, String sortOrder) {
        String artistName = ArtistEntry.getArtistNameFromUri(uri);
        Log.v(LOG_TAG, "StreamerArtist name to be used in the filter = " + artistName);

        return mDBHelper.getReadableDatabase().query(
                ArtistEntry.TABLE_NAME,
                projection,
                ArtistEntry.TABLE_NAME + "." + ArtistEntry.COLUMN_NAME + " like ?",
                new String[] { artistName + "%" },
                null,
                null,
                sortOrder);
    }

    private Cursor getTrackByArtist( Uri uri, String[] projection, String sortOrder) {
        String artistId = TrackEntry.getArtistIdFromUri(uri);
        Log.v( LOG_TAG, "StreamerArtist ID to be used in the filter = " + artistId );

        return mDBHelper.getReadableDatabase().query(
                TrackEntry.TABLE_NAME,
                projection,
                TrackEntry.TABLE_NAME + "." + TrackEntry.COLUMN_ARTIST_KEY + "= ?",
                new String[] { artistId },
                null,
                null,
                sortOrder);
    }
}
