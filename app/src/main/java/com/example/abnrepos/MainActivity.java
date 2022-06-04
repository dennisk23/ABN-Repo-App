package com.example.abnrepos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abnrepos.adapter.RepositoryAdapter;
import com.example.abnrepos.api.GitHubAPI;
import com.example.abnrepos.api.RetroClient;
import com.example.abnrepos.data.Repository;
import com.example.abnrepos.database.AppDatabase;
import com.example.abnrepos.database.RepositoryDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // Todo Splash activity?
    private static final String STATE_LINEAR_LAYOUT_MANAGER = "state_layout_manager";
    private static final String STATE_ADAPTER_ITEMS = "state_adapter_items";
    private static final String USER = "abnamrocoesd";
    private static final int PER_PAGE = 10;
    private static final int THRESHOLD_LOAD_MORE = 3; // When to start loading more items

    private TextView textOffline;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RepositoryAdapter repositoryAdapter;
    private GitHubAPI gitHubAPI;
    private RepositoryDao repositoryDao;

    private int curPage = 0;
    private boolean loadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOffline = findViewById(R.id.main_offline);
        swipeRefreshLayout = findViewById(R.id.main_refresher);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.main_recyclerview);

        recyclerView.addOnScrollListener(scrollDownListener);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DATABASE_NAME).build();
        repositoryDao = db.repositoryDao();

        gitHubAPI = RetroClient.getGitHubAPI();
        linearLayoutManager = new LinearLayoutManager(this);
        repositoryAdapter = new RepositoryAdapter();

        recyclerView.setAdapter(repositoryAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Load items when app opened for the first time, otherwise restore state
        if(savedInstanceState == null) {
            if(hasNetworkConnection()) refreshRepos();
            else loadCache();
        } else {
            linearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_LINEAR_LAYOUT_MANAGER));
            repositoryAdapter.updateList(savedInstanceState.getParcelableArrayList(STATE_ADAPTER_ITEMS));
        }
    }

    /**
     * Reload all Repositories, starting from the first page
     */
    private void refreshRepos() {
        curPage = 0;
        loadMore = true;
        loadNextPage();
    }

    /**
     * Load the next page of Repositories from the Github API
     */
    private void loadNextPage() {
        swipeRefreshLayout.setRefreshing(true);

        curPage += 1;
        Call<List<Repository>> repoCall = gitHubAPI.userRepositories(USER, curPage, PER_PAGE);
        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if(response.isSuccessful() && response.body() != null) {
                    // TODO: maybe in new function?
                    textOffline.setVisibility(View.GONE);
                    List<Repository> repoList = response.body();

                    // When there are no more Repositories, prevent the app from trying to load more
                    if(repoList.size() == 0) loadMore = false;

                    // When the first page is loaded, replace all Repositories in the list with the
                    // loaded Repositories. Otherwise, add the loaded Repositories to the list
                    if(curPage == 1) repositoryAdapter.updateList(repoList);
                    else repositoryAdapter.addToList(repoList);

                    // Store the Repositories in the cache
                    new Thread(() -> {
                        if(curPage == 1) repositoryDao.deleteAll();
                        repositoryDao.insertAll(repoList);
                    }).start();
                } else
                    onPageLoadFailed();
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                onPageLoadFailed();
            }
        });
    }

    /**
     * Load cached Repositories, prevent blocking the UI by doing it in a thread
     */
    private void loadCache() {
        new Thread(() -> {
            textOffline.setVisibility(View.VISIBLE);
            loadMore = false;
            List<Repository> repos = repositoryDao.getAll();
            repositoryAdapter.updateList(repos);
        }).start();
    }

    /**
     * When a page fails to load, show the user the current state is offline, and prevent loading more items
     */
    private void onPageLoadFailed() {
        textOffline.setVisibility(View.VISIBLE);
        loadMore = false;
        Toast.makeText(MainActivity.this, R.string.data_load_fail, Toast.LENGTH_LONG).show();
        if(repositoryAdapter.getItemCount() == 0) loadCache();
    }

    /**
     * Check on whether the user
     * @return connected (true) or not (false)
     */
    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

    /**
     * When the user pulled to refresh (swipeRefreshLayout), refresh the Repositories
     */
    @Override
    public void onRefresh() {
        refreshRepos();
    }

    /**
     * Scroll down listener for the Repository list. Loads next page when reaching the bottom of the listview
     */
    private final RecyclerView.OnScrollListener scrollDownListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            // If more items can be loaded, and the app is currently not refreshing,
            // And there are less than '3' more items than the last visible item, then load more items
            if (loadMore && !swipeRefreshLayout.isRefreshing() && repositoryAdapter.getItemCount()  <= lastVisibleItem + THRESHOLD_LOAD_MORE)
                loadNextPage();
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(STATE_LINEAR_LAYOUT_MANAGER, linearLayoutManager.onSaveInstanceState());
        state.putParcelableArrayList(STATE_ADAPTER_ITEMS, new ArrayList<>(repositoryAdapter.getList()));
    }
}