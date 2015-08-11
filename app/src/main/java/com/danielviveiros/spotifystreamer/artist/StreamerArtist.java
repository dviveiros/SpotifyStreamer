package com.danielviveiros.spotifystreamer.artist;

import android.database.Cursor;

import com.danielviveiros.spotifystreamer.data.SpotifyStreamerContract;

/**
 * StreamerArtist from Spotify
 *
 * Created by dviveiros on 11/08/15.
 */
public class StreamerArtist {

    private Integer id;
    private String name;
    private String imageUrl;

    /** Column indexes on database */
    public static final int COL_INDEX_ID = 0;
    public static final int COL_INDEX_NAME = 1;
    public static final int COL_INDEX_IMAGE_URL = 2;

    /** Table columns */
    public static final String[] ARTIST_COLUMNS = {
            SpotifyStreamerContract.ArtistEntry.TABLE_NAME + "." + SpotifyStreamerContract.ArtistEntry._ID,
            SpotifyStreamerContract.ArtistEntry.COLUMN_NAME,
            SpotifyStreamerContract.ArtistEntry.COLUMN_IMAGE_URL
    };

    /**
     * Constructor
     */
    public StreamerArtist(String name, String imageUrl) {
        this.id = null;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor
     */
    public StreamerArtist(Cursor cursor) {
        this.id = cursor.getInt( StreamerArtist.COL_INDEX_ID );
        this.name = cursor.getString(StreamerArtist.COL_INDEX_NAME);
        this.imageUrl = cursor.getString( StreamerArtist.COL_INDEX_IMAGE_URL );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "StreamerArtist{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
