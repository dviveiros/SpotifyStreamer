package com.danielviveiros.spotifystreamer.artist;

import android.database.Cursor;

/**
 * StreamerArtist from Spotify
 *
 * Created by dviveiros on 11/08/15.
 */
public class StreamerArtist {

    private Integer id;
    private String key;
    private String name;
    private String imageUrl;

    /**
     * Constructor
     */
    public StreamerArtist(String key, String name, String imageUrl) {
        this.id = null;
        this.key = key;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor
     */
    public StreamerArtist(Cursor cursor) {
        this.id = cursor.getInt(ArtistRepository.COL_INDEX_ID);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "StreamerArtist{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
