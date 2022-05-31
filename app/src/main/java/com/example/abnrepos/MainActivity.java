package com.example.abnrepos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.abnrepos.adapter.RepositoryAdapter;
import com.example.abnrepos.api.GitHubAPI;
import com.example.abnrepos.api.RetroClient;
import com.example.abnrepos.data.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // Todo Splash activity?

    private static final String USER = "abnamrocoesd";
    private static final int PER_PAGE = 10;
    private static final int THRESHOLD_LOAD_MORE = 3; // When to start loading more items

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RepositoryAdapter repositoryAdapter;
    private GitHubAPI gitHubAPI;

    private int curPage = 0;
    private boolean loadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.main_refresher);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.main_recyclerview);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(scrollDownListener);

        //if(repositoryAdapter == null) repositoryAdapter = new RepositoryAdapter();


        if(savedInstanceState == null) {
            repositoryAdapter = new RepositoryAdapter();
            recyclerView.setAdapter(repositoryAdapter);

            gitHubAPI = RetroClient.getGitHubAPI();

            refreshRepos();
        }
    }

    private void refreshRepos() {
        curPage = 0;
        loadMore = true;
        loadNextPage();
    }

    private void loadNextPage() {
        swipeRefreshLayout.setRefreshing(true);

        curPage += 1;
        Call<List<Repository>> repoCall = gitHubAPI.userRepositories(USER, curPage, PER_PAGE);
        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if(response.isSuccessful() && response.body() != null) {
                    Log.e("RESULT", "Page " + curPage + " loaded: " + response.body());
                    List<Repository> repoList = response.body();

                    if(repoList.size() == 0) loadMore = false;

                    if(curPage == 1) repositoryAdapter.updateList(repoList);
                    else repositoryAdapter.addToList(repoList);
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

    private void onPageLoadFailed() {
        loadMore = false;
        Toast.makeText(MainActivity.this, R.string.data_load_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        refreshRepos();
    }

    private final RecyclerView.OnScrollListener scrollDownListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            if (loadMore && !swipeRefreshLayout.isRefreshing() && repositoryAdapter.getItemCount() <= lastVisibleItem + THRESHOLD_LOAD_MORE)
                loadNextPage();
        }
    };
}