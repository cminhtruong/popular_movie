package com.cmtruong.udacity.views;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmtruong.udacity.R;
import com.cmtruong.udacity.adapters.FetchDetailInteractorImpl;
import com.cmtruong.udacity.adapters.ReviewAdapter;
import com.cmtruong.udacity.adapters.TrailerAdapter;
import com.cmtruong.udacity.configs.Config;
import com.cmtruong.udacity.data.MovieContract;
import com.cmtruong.udacity.models.Movie;
import com.cmtruong.udacity.models.Review;
import com.cmtruong.udacity.models.Video;
import com.cmtruong.udacity.presenter.DetailMoviePresenter;
import com.cmtruong.udacity.presenter.DetailMoviePresenterImpl;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author davidetruong
 * @version 1.0
 */
public class DetailMovieActivity extends Activity implements DetailMovieView {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();


    @BindView(R.id.iv_movie)
    ImageView imageView;
    @BindView(R.id.sv_movie)
    ScrollView scrollView;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.tv_date)
    TextView date;
    @BindView(R.id.tv_vote_average)
    TextView voteAverage;
    @BindView(R.id.tv_overview)
    TextView overview;
    @BindView(R.id.bt_add_favorite)
    Button bt_fav;

    @BindView(R.id.rv_reviews)
    RecyclerView rv_reviews;
    @BindView(R.id.rv_trailer)
    RecyclerView rv_trailer;

    DetailMoviePresenter mPresenter;

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Log.d(TAG, "onCreate: ");
        ButterKnife.bind(this);

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(Config.MOVIE_INTENT_KEY);
        loadData(movie);
        Log.d(TAG, "onCreate: " + movie.toString());

        mPresenter = new DetailMoviePresenterImpl(new FetchDetailInteractorImpl(), this);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setLayoutManager();


    }

    public void setLayoutManager() {
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        trailerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_trailer.setLayoutManager(trailerLayoutManager);
        rv_trailer.setHasFixedSize(false);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        reviewLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_reviews.setLayoutManager(reviewLayoutManager);
        rv_reviews.setHasFixedSize(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: checked ");
        mPresenter.onResume();
    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Load data to display
     *
     * @param movie
     */
    public void loadData(Movie movie) {
        title.setText(movie.getTitle());

        // Convert string to date format
        DateFormat format1 = new SimpleDateFormat("yyyy-mm-dd", Locale.US);

        // Convert date to new string
        DateFormat format2 = new SimpleDateFormat("MMMM d, yyyy");

        try {
            Date dateFormat = format1.parse(movie.getRelease_date());
            String newDateFormat = format2.format(dateFormat);
            Log.i(TAG, "loadData: " + newDateFormat);
            date.setText(newDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        voteAverage.setText(String.valueOf(movie.getVote_average()));
        overview.setText(movie.getOverview());

        Picasso.get()
                .load(Config.IMAGE_URL + movie.getPoster_path())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void setTrailerItem(List<Video> videos) {
        rv_trailer.setAdapter(new TrailerAdapter(videos, new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Video video) {
                navigateToYoutubeTrailer(video);
            }
        }));
        Log.d(TAG, "setTrailerItem: " + videos.toString());
    }

    @Override
    public void setReviewItem(List<Review> reviews) {
        rv_reviews.setAdapter(new ReviewAdapter(reviews));
        Log.d(TAG, "setReviewItem: " + reviews.toString());
    }

    @Override
    public void addedToFav(ContentValues values) {
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        if (uri != null)
            Log.d(TAG, "addedToFav: " + uri.toString());
        bt_fav.setBackgroundColor(Color.BLUE);
    }

    @Override
    public Movie setMovieItem() {
        return movie;
    }


    public void navigateToYoutubeTrailer(Video video) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.YOUTUBE_PLAY_URL + video.getKey())));
    }

    public void addFavorite(View view) {
        mPresenter.insertToFavoriteList(movie);
    }

}
