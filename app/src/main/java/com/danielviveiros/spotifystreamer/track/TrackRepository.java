package com.danielviveiros.spotifystreamer.track;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerDBHelper;

import java.util.List;

/**
 * Repository class for tracks
 *
 * Created by dviveiros on 13/08/15.
 */
public class TrackRepository {

    /** Column indexes on database */
    public static final int COL_INDEX_ID = 0;
    public static final int COL_INDEX_ARTIST_KEY = 1;
    public static final int COL_INDEX_KEY = 2;
    public static final int COL_INDEX_NAME = 3;
    public static final int COL_INDEX_ALBUM_NAME = 4;
    public static final int COL_INDEX_ALBUM_IMAGE_URL = 5;

    /** FULL PROJECTION */
    public static final String[] FULL_PROJECTION = {
            SpotifyStreamerContract.TrackEntry.TABLE_NAME + "." + SpotifyStreamerContract.TrackEntry._ID,
            SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY,
            SpotifyStreamerContract.TrackEntry.COLUMN_KEY,
            SpotifyStreamerContract.TrackEntry.COLUMN_NAME,
            SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME,
            SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_IMAGE_URL
    };

    /** Singleton Instance */
    private static TrackRepository mInstance;

    /** Reference to DBHelper */
    private SpotifyStreamerDBHelper mDBHelper;

    /**
     * Constructor
     */
    private TrackRepository(Context context) {
        mDBHelper = new SpotifyStreamerDBHelper(context);
    }

    /**
     * Returns the singleton instance of this object
     */
    public static TrackRepository getInstance( Context context ) {
        if ( mInstance == null ) {
            mInstance = new TrackRepository( context );
        }
        return mInstance;
    }

    /**
     * Bulk insert a list of tracks into the database
     * @return Number of insertions
     */
    public int bulkInsert(List<StreamerTrack> trackList) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;

        try {
            for ( StreamerTrack track: trackList ) {
                ContentValues values = new ContentValues();
                values.put(SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY, track.getArtistKey());
                values.put(SpotifyStreamerContract.TrackEntry.COLUMN_KEY, track.getKey());
                values.put(SpotifyStreamerContract.TrackEntry.COLUMN_NAME, track.getName());
                values.put(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME, track.getAlbumName());
                values.put(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_IMAGE_URL, track.getAlbumImageUrl());
                long id = db.insert(SpotifyStreamerContract.TrackEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        return returnCount;
    }
}
