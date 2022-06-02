package com.example.abnrepos.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abnrepos.R;
import com.example.abnrepos.data.Repository;
import com.squareup.picasso.Picasso;

public class RepositoryViewHolder extends RecyclerView.ViewHolder {
    public final ImageView avatar;
    public final TextView name, visibility, isPrivate;
    public RepositoryViewHolder(@NonNull View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.list_repo_avatar);
        name = itemView.findViewById(R.id.list_repo_name);
        visibility = itemView.findViewById(R.id.list_repo_visibility);
        isPrivate = itemView.findViewById(R.id.list_repo_private);
    }

    public void bind(Repository repo) {
        Context context = itemView.getContext();
        name.setText(repo.getName());
        visibility.setText(context.getString(R.string.list_repo_visibility, repo.getVisibility()));
        isPrivate.setText(context.getString(R.string.list_repo_private, repo.getPrivate(context)));
        Picasso.with(context).load(repo.getOwner().getAvatarUrl()).into(avatar);
    }
}
