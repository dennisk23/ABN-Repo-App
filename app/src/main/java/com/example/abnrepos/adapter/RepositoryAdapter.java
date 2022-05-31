package com.example.abnrepos.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abnrepos.R;
import com.example.abnrepos.data.Repository;
import com.example.abnrepos.viewholder.RepositoryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryViewHolder> {
    private final List<Repository> repos;

    public RepositoryAdapter() {
        repos = new ArrayList<>();
    }

    public void updateList(List<Repository> newRepos) {
        repos.clear();
        repos.addAll(newRepos);
        notifyDataSetChanged();
    }

    public void addToList(List<Repository> addRepos) {
        int start = repos.size();
        int end = addRepos.size();
        repos.addAll(addRepos);
        notifyItemRangeInserted(start, end);
    }

    @Override @NonNull
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_repository, parent, false);
        RepositoryViewHolder viewHolder = new RepositoryViewHolder(view);
        view.setOnClickListener(clickedView -> {
            repos.get(viewHolder.getAbsoluteAdapterPosition()).openDetailsActivity(clickedView.getContext());
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RepositoryViewHolder holder, int position) {
        Repository repo = repos.get(position);
        holder.bind(repo);
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }
}
