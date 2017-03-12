/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.cinetoday.app.movie.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jraf.android.cinetoday.R;
import org.jraf.android.cinetoday.databinding.MovieListItemBinding;
import org.jraf.android.cinetoday.glide.GlideHelper;
import org.jraf.android.cinetoday.provider.movie.MovieCursor;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private final Context mContext;
    private MovieListCallbacks mMovieListCallbacks;
    private PaletteListener mPaletteListener;
    private final LayoutInflater mLayoutInflater;
    private MovieCursor mCursor;

    public MovieListAdapter(Context context, MovieListCallbacks movieListCallbacks, PaletteListener paletteListener) {
        mContext = context;
        mMovieListCallbacks = movieListCallbacks;
        mPaletteListener = paletteListener;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final MovieListItemBinding itemBinding;

        public ViewHolder(MovieListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieListItemBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.movie_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MovieListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mCursor.moveToPosition(position);
        holder.itemBinding.setMovie(mCursor);
        holder.itemBinding.setMovieId(mCursor.getId());
        holder.itemBinding.setCallbacks(mMovieListCallbacks);
        long id = mCursor.getId();
        boolean hasColor = mCursor.getColor() != null;
        if (hasColor) mPaletteListener.onPaletteAvailable(position, mCursor.getColor(), true, id);

        holder.itemBinding.executePendingBindings();

        GlideHelper.load(mCursor.getPosterUri(), holder.itemBinding.imgPoster, new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                // Generate the color
                if (!hasColor) {
                    GlideBitmapDrawable glideBitmapDrawable = (GlideBitmapDrawable) resource;
                    Palette.from(glideBitmapDrawable.getBitmap()).generate(palette -> {
                        int color = palette.getDarkVibrantColor(mContext.getColor(R.color.movie_list_bg));
                        mPaletteListener.onPaletteAvailable(position, color, false, id);
                    });
                }

                // Preload the next image
                if (mCursor.moveToPosition(position + 1)) {
                    Glide.with(mContext).load(mCursor.getPosterUri()).centerCrop()
                            .preload(holder.itemBinding.imgPoster.getWidth(), holder.itemBinding.imgPoster.getHeight());
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor == null) return RecyclerView.NO_ID;
        if (!mCursor.moveToPosition(position)) return RecyclerView.NO_ID;
        return mCursor.getId();
    }

    public void swapCursor(MovieCursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
