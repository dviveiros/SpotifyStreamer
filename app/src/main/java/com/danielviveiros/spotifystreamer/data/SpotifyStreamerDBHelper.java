package com.danielviveiros.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract.ArtistEntry;
import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract.TrackEntry;

/**
 * Helper class to deal with database specific tasks such as table creation
 *
 * Created by dviveiros on 10/08/15.
 */
public class SpotifyStreamerDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = SpotifyStreamerDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "spotifystreamer.db";

    /**
     * Constructor
     * @param context Current context
     */
    public SpotifyStreamerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ARTIST_TABLE =
                "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArtistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL);";

        final String SQL_CREATE_TRACK_TABLE =
                "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                        TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TrackEntry.COLUMN_ARTIST_KEY + " INTEGER NOT NULL," +
                        TrackEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        TrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL," +
                        TrackEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL ," +
                        "FOREIGN KEY (" + TrackEntry.COLUMN_ARTIST_KEY + ") REFERENCES " +
                        ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + "));";

        Log.v( LOG_TAG, "StreamerArtist statement = " + SQL_CREATE_ARTIST_TABLE);
        Log.v( LOG_TAG, "Track statement = " + SQL_CREATE_TRACK_TABLE );

        sqLiteDatabase.execSQL(SQL_CREATE_ARTIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
