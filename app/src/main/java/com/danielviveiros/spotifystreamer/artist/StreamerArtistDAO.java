package com.danielviveiros.spotifystreamer.artist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerDBHelper;

/**
 * DAO for StreamerArtist
 * Created by dviveiros on 12/08/15.
 */
public class StreamerArtistDAO {

    /** FULL PROJECTION */
    public static final String[] FULL_PROJECTION = {
            SpotifyStreamerContract.ArtistEntry.TABLE_NAME + "." + SpotifyStreamerContract.ArtistEntry._ID,
            SpotifyStreamerContract.ArtistEntry.COLUMN_NAME,
            SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL
    };

    /** Singleton Instance */
    private static StreamerArtistDAO mInstance;

    /** Reference to DBHelper */
    private SpotifyStreamerDBHelper mDBHelper;

    /**
     * Constructor
     */
    private StreamerArtistDAO(Context context) {
        mDBHelper = new SpotifyStreamerDBHelper(context);
    }

    /**
     * Returns the singleton instance of this object
     */
    public static StreamerArtistDAO getInstance( Context context ) {
        if ( mInstance == null ) {
            mInstance = new StreamerArtistDAO( context );
        }
        return mInstance;
    }

    /**
     * Finds a list of artists by its names
     */
    public Cursor findArtistsByNamePrefix( String namePrefix ) {
        String selection = SpotifyStreamerContract.ArtistEntry.COLUMN_NAME + " like ?";;
        String[] selectionArgs = new String[] { namePrefix + "%" };
        String sortOrder = SpotifyStreamerContract.ArtistEntry.COLUMN_NAME + " ASC";;
        return mDBHelper.getReadableDatabase().query(
                SpotifyStreamerContract.ArtistEntry.TABLE_NAME,
                StreamerArtistDAO.FULL_PROJECTION,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    /**
     * Inserts a new artist into the database
     * @return The generated ID
     */
    public Long insert(StreamerArtist artist) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Long id = -1L;

        try {
            ContentValues values = new ContentValues();
            values.put(SpotifyStreamerContract.ArtistEntry.COLUMN_NAME, artist.getName());
            values.put(SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL, artist.getImageUrl());
            id = mDBHelper.getWritableDatabase().insert(
                    SpotifyStreamerContract.ArtistEntry.TABLE_NAME, null, values);
        } finally {
            db.close();
        }

        return id;
    }

}
