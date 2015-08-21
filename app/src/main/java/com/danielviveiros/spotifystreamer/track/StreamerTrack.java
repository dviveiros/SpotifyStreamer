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
    private String albumFullImageUrl;
    private Long duration;
    private String urlPreview;
    private String artistName;

    public StreamerTrack( String artistKey, String key, String name, String albumName,
                          String albumImageUrl, String albumFullImageUrl, Long duration, String urlPreview,
                          String artistName) {
        this.artistKey = artistKey;
        this.key = key;
        this.name = name;
        this.albumName = albumName;
        this.albumImageUrl = albumImageUrl;
        this.albumFullImageUrl = albumFullImageUrl;
        this.duration = duration;
        this.urlPreview = urlPreview;
        this.artistName = artistName;
    }

    public StreamerTrack( Cursor cursor ) {
        this.id = cursor.getInt( TrackRepository.COL_INDEX_ID );
        this.artistKey = cursor.getString(TrackRepository.COL_INDEX_ARTIST_KEY);
        this.key = cursor.getString( TrackRepository.COL_INDEX_KEY );
        this.name = cursor.getString( TrackRepository.COL_INDEX_NAME );
        this.albumName = cursor.getString( TrackRepository.COL_INDEX_ALBUM_NAME );
        this.albumImageUrl = cursor.getString( TrackRepository.COL_INDEX_ALBUM_IMAGE_URL );
        this.albumFullImageUrl = cursor.getString( TrackRepository.COL_INDEX_FULL_ALBUM_IMAGE_URL );
        this.duration = cursor.getLong(TrackRepository.COL_INDEX_DURATION);
        this.urlPreview = cursor.getString(TrackRepository.COL_INDEX_URL_PREVIEW);
        this.artistName = cursor.getString( TrackRepository.COL_INDEX_ARTIST_NAME);
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
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

    public String getKey() {
        return key;
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

    public String getAlbumFullImageUrl() {
        return albumFullImageUrl;
    }

    public Long getDuration() {
        return duration;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public String getArtistName() {
        return artistName;
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
                ", albumFullImageUrl='" + albumFullImageUrl + '\'' +
                ", duration=" + duration +
                ", urlPreview='" + urlPreview + '\'' +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}
