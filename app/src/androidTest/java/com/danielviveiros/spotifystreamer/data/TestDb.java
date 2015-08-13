package com.danielviveiros.spotifystreamer.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.danielviveiros.spotifystreamer.artist.StreamerArtist;
import com.danielviveiros.spotifystreamer.artist.StreamerArtistDAO;

import java.util.HashSet;

/**
 * Tests the database
 *
 * Created by dviveiros on 10/08/15.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(SpotifyStreamerDBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * Tests database creation
     * @throws Throwable
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(SpotifyStreamerContract.ArtistEntry.TABLE_NAME);
        tableNameHashSet.add(SpotifyStreamerContract.TrackEntry.TABLE_NAME);

        mContext.deleteDatabase(SpotifyStreamerDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new SpotifyStreamerDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the artist entry and tracks entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + SpotifyStreamerContract.TrackEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(SpotifyStreamerContract.TrackEntry._ID);
        locationColumnHashSet.add(SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_KEY);
        locationColumnHashSet.add(SpotifyStreamerContract.TrackEntry.COLUMN_NAME);
        locationColumnHashSet.add(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME);
        locationColumnHashSet.add(SpotifyStreamerContract.TrackEntry.COLUMN_IMAGE_URL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required tracks entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /**
     * Test tracks table
     */
    public void testTracksTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new SpotifyStreamerDBHelper(
                this.mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues contentValues = TestUtilities.createFakeTrack();

        // Insert ContentValues into database and get a row ID back
        Long id = db.insert(SpotifyStreamerContract.TrackEntry.TABLE_NAME, null, contentValues);
        assertNotNull(id);
        assertTrue(id > 0);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(SpotifyStreamerContract.TrackEntry.TABLE_NAME, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        assertTrue(cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        assertEquals("Track Name", cursor.getString(2));

        // Finally, close the cursor and database
        cursor.close();
        db.close();

    }

    /**
     * Test artists table
     */
    public void testArtistsTable() {

        StreamerArtistDAO artistDAO = StreamerArtistDAO.getInstance( getContext() );

        StreamerArtist artist = TestUtilities.createFakeArtist( "Madonna" );
        Long id = artistDAO.insert( artist );
        assertNotNull(id);
        assertTrue(id > 0);

        artist = TestUtilities.createFakeArtist( "Madonna Dan" );
        id = artistDAO.insert( artist );
        assertNotNull(id);
        assertTrue(id > 0);

        artist = TestUtilities.createFakeArtist( "Led Zepelin" );
        id = artistDAO.insert( artist );
        assertNotNull(id);
        assertTrue(id > 0);

        Cursor cursor = artistDAO.findArtistsByNamePrefix("Madonna");
        assertEquals(2, cursor.getCount());

        // Move the cursor to a valid database row
        assertTrue(cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        assertEquals("Madonna", cursor.getString(1));

        artistDAO.deleteAll();
        cursor = artistDAO.findArtistsByNamePrefix( "Madonna" );
        assertEquals(0, cursor.getCount());
    }

}
