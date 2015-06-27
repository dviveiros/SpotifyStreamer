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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Custom adapter to handle artist list (search) information
 * Created by dviveiros on 24/06/15.
 */
public class ArtistListViewAdapter extends ArrayAdapter<Artist> {

    private Context context;

    public ArtistListViewAdapter(Context context, int resourceId, List<Artist> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView artistIconView;
        TextView artistNameView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Artist artist = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.artistfilter_listitem, null);
            holder = new ViewHolder();
            holder.artistNameView = (TextView) convertView.findViewById(R.id.artist_name);
            holder.artistIconView = (ImageView) convertView.findViewById(R.id.artist_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artistNameView.setText(artist.name);
        List<Image> images = artist.images;
        if ((images != null) && (images.size() > 0)) {
            Image artistImage = images.get(0);
            Picasso.with(context).load(artistImage.url).into(holder.artistIconView);
        }

        return convertView;
    }

}
