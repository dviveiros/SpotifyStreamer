package com.danielviveiros.spotifystreamer.track;

import android.database.Cursor;

/**
 * Represents a track from Spotify
 *
 * Created by dviveiros on 13/08/15.
 */
public class StreamerTrack {

    private Integer id;
    private String artistKey;
    private String key;
    private String name;
    private String albumName;
    private String albumImageUrl;

    public StreamerTrack( String artistKey, String key, String name, String albumName, String albumImageUrl ) {
        this.artistKey = artistKey;
        this.key = key;
        this.name = name;
        this.albumName = albumName;
        this.albumImageUrl = albumImageUrl;
    }

    public StreamerTrack( Cursor cursor ) {
        this.id = cursor.getInt( TrackRepository.COL_INDEX_ID );
        this.artistKey = cursor.getString(TrackRepository.COL_INDEX_ARTIST_KEY);
        this.key = cursor.getString( TrackRepository.COL_INDEX_KEY );
        this.name = cursor.getString( TrackRepository.COL_INDEX_NAME );
        this.albumName = cursor.getString( TrackRepository.COL_INDEX_ALBUM_NAME );
        this.albumImageUrl = cursor.getString( TrackRepository.COL_INDEX_ALBUM_IMAGE_URL );
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public String toString() {
        return "StreamerTrack{" +
                "id=" + id +
                ", artistKey='" + artistKey + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumImageUrl='" + albumImageUrl + '\'' +
                '}';
    }
}
