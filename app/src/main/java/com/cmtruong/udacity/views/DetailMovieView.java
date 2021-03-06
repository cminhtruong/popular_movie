package com.cmtruong.udacity.views;

import android.content.ContentValues;

import com.cmtruong.udacity.models.Movie;
import com.cmtruong.udacity.models.Review;
import com.cmtruong.udacity.models.Video;

import java.util.List;

/**
 * @author davidetruong
 * @version 1.0
 * @since April 9th, 2018
 */
public interface DetailMovieView {

    void setTrailerItem(List<Video> videos);

    void setReviewItem(List<Review> reviews);

    void addedToFav(ContentValues contentValues);

    Movie setMovieItem();

}
