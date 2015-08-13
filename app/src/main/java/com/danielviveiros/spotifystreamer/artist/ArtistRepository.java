package com.danielviveiros.spotifystreamer.artist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerDBHelper;

import java.util.List;

/**
 * DAO for StreamerArtist
 * Created by dviveiros on 12/08/15.
 */
public class ArtistRepository {

    /** FULL PROJECTION */
    public static final String[] FULL_PROJECTION = {
            SpotifyStreamerContract.ArtistEntry.TABLE_NAME + "." + SpotifyStreamerContract.ArtistEntry._ID,
            SpotifyStreamerContract.ArtistEntry.COLUMN_NAME,
            SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL
    };

    /** Singleton Instance */
    private static ArtistRepository mInstance;

    /** Reference to DBHelper */
    private SpotifyStreamerDBHelper mDBHelper;

    /**
     * Constructor
     */
    private ArtistRepository(Context context) {
        mDBHelper = new SpotifyStreamerDBHelper(context);
    }

    /**
     * Returns the singleton instance of this object
     */
    public static ArtistRepository getInstance( Context context ) {
        if ( mInstance == null ) {
            mInstance = new ArtistRepository( context );
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
                ArtistRepository.FULL_PROJECTION,
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
            id = db.insert(SpotifyStreamerContract.ArtistEntry.TABLE_NAME, null, values);
        } finally {
            db.close();
        }

        return id;
    }

    /**
     * Bulk insert a list of artists into the database
     * @return Number of insertions
     */
    public int bulkInsert(List<StreamerArtist> artistList) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;

        try {
            for ( StreamerArtist artist: artistList ) {
                ContentValues values = new ContentValues();
                values.put(SpotifyStreamerContract.ArtistEntry.COLUMN_NAME, artist.getName());
                values.put(SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL, artist.getImageUrl());
                long id = db.insert(SpotifyStreamerContract.ArtistEntry.TABLE_NAME, null, values);
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

    /**
     * Remove all data from the database
     */
    public void deleteAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            db.delete(SpotifyStreamerContract.ArtistEntry.TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

}
