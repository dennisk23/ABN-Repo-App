package com.example.abnrepos.api;

import com.example.abnrepos.data.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubAPI {
    @GET("users/{user}/repos")
    Call<List<Repository>> userRepositories(
            @Path("user") String user,
            @Query("page") int page,
            @Query("per_page") int perPage
    );
}
