package com.example.abnrepos.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static final String BASE_URL = "https://api.github.com/";

    private static GitHubAPI gitHubAPI = null;

    public static GitHubAPI getGitHubAPI() {
        if(gitHubAPI == null) {
            synchronized (RetroClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                gitHubAPI = retrofit.create(GitHubAPI.class);
            }
        }
        return gitHubAPI;
    }
}
