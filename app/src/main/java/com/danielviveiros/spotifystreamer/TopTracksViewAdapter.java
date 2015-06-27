package com.danielviveiros.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Custom adapter to handle artist list (search) information
 * Created by dviveiros on 24/06/15.
 */
public class TopTracksViewAdapter extends ArrayAdapter<Track> {

    private Context context;

    public TopTracksViewAdapter(Context context, int resourceId, List<Track> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView albumIconView;
        TextView albumNameView;
        TextView trackNameView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Track track = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.toptracks_listitem, null);
            holder = new ViewHolder();
            holder.albumNameView = (TextView) convertView.findViewById(R.id.album_name);
            holder.trackNameView = (TextView) convertView.findViewById(R.id.track_name);
            holder.albumIconView = (ImageView) convertView.findViewById(R.id.album_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trackNameView.setText(track.name);
        if (track.album != null) {
            holder.albumNameView.setText(track.album.name);
            List<Image> images = track.album.images;
            if ((images != null) && (images.size() > 0)) {
                Image albumImage = images.get(0);
                Picasso.with(context).load(albumImage.url).into(holder.albumIconView);
            }
        }

        return convertView;
    }

}
