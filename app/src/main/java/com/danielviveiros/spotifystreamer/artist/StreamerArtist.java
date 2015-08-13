package com.danielviveiros.spotifystreamer.artist;

import android.database.Cursor;

/**
 * StreamerArtist from Spotify
 *
 * Created by dviveiros on 11/08/15.
 */
public class StreamerArtist {

    private Integer id;
    private String name;
    private String imageUrl;

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
        this.id = cursor.getInt( ArtistRepository.COL_INDEX_ID );
        this.name = cursor.getString(ArtistRepository.COL_INDEX_NAME);
        this.imageUrl = cursor.getString( ArtistRepository.COL_INDEX_IMAGE_URL );
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
