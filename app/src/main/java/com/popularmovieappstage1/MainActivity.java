package com.popularmovieappstage1;


import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import java.util.ArrayList;
import java.util.Collections;

import com.popularmovieappstage1.Model.Movies;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_MOVIE = "MOVIE";
    private static final String KEY_MOVIE_LIST = "MOVIE_LIST";
    private ArrayList<Movies> moviesArrayList;
    private MoviesAdapter moviesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_MOVIE_LIST)) {
            moviesArrayList = new ArrayList<>();
        } else {
            moviesArrayList = savedInstanceState.getParcelableArrayList(KEY_MOVIE_LIST);
        }
               setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        moviesAdapter = new MoviesAdapter(this, moviesArrayList);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movies movie = moviesAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                intent.putExtra(KEY_MOVIE, movie);
                startActivity(intent);
            }
        });

            downloadMovies();
    }

    private void downloadMovies() {
            MovieTaskAPI moviesTask = new MovieTaskAPI(new CallbackInterface() {
                @Override
                public void updateAdapter(Movies[] movies) {

                    if (movies != null) {
                        moviesAdapter.clear();
                        Collections.addAll(moviesArrayList, movies);
                        moviesAdapter.notifyDataSetChanged();
                    }

                }
                });
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String sortingOrder = preferences.getString(getString(R.string.settings_sort_key),
                    getString(R.string.settings_sort_popular_value));
            moviesTask.execute(sortingOrder);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIE_LIST, moviesArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
