package com.cmtruong.udacity.adapters;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cmtruong.udacity.api.MovieServices;
import com.cmtruong.udacity.configs.Config;
import com.cmtruong.udacity.models.Movie;
import com.cmtruong.udacity.models.Page;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidetruong on 13/03/2018.
 *
 * @author davidetruong
 * @version 1.0
 */

public class FetchItemInteractorImpl implements FetchItemInteractor {
    private static final String TAG = FetchItemInteractorImpl.class.getSimpleName();

    private List<Movie> movies = new ArrayList<>();

    private String sortType;

    @Override
    public void fetchItem(final onFinishedListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //listener.onFinished(fetchDataPopular());
                Log.i(TAG, "fetchData: begin ...");
                Log.i(TAG, "run: " + listener.getSortType());
                sortType = listener.getSortType();
                if (sortType.equals("favorite")) {
                    Log.d(TAG, "run: " + sortType);
                    listener.onFinished(movies);
                } else {
                    MovieServices movieServices = MovieServices.retrofit.create(MovieServices.class);
                    Call<Page> myPage = movieServices.requestMovies(listener.getSortType(), Config.API_KEY);
                    myPage.enqueue(new Callback<Page>() {
                        @Override
                        public void onResponse(Call<Page> call, Response<Page> response) {
                            int statusCode = response.code();
                            Log.i(TAG, "onResponse: " + response.code());
                            if (response.isSuccessful() && statusCode == 200) {
                                movies = response.body().getMovies();
                                listener.onFinished(movies);
                                Log.i(TAG, "onResponse: " + movies.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<Page> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
                }

            }
        }, 1000);
    }
}
