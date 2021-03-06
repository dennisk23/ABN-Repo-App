package com.example.abnrepos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abnrepos.data.Repository;
import com.squareup.picasso.Picasso;

public class RepositoryActivity extends AppCompatActivity {

    private static final String INTENT_EXTRA_REPO = "intent_repo";

    private ImageView avatar;
    private TextView name, fullName, description, visibility, isPrivate;

    private Repository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        avatar = findViewById(R.id.repo_avatar);
        name = findViewById(R.id.repo_name);
        fullName = findViewById(R.id.repo_full_name);
        description = findViewById(R.id.repo_description);
        visibility = findViewById(R.id.repo_visibility);
        isPrivate = findViewById(R.id.repo_private);
        Button openUrl = findViewById(R.id.repo_open);

        repo = getIntent().getParcelableExtra(INTENT_EXTRA_REPO);
        if(repo != null) {
            updateViews();
            openUrl.setOnClickListener(v -> repo.openInBrowser(RepositoryActivity.this));
        }
    }

    public void updateViews() {
        name.setText(repo.getName());
        fullName.setText(repo.getFullName());
        description.setText(repo.getDescription());
        visibility.setText(getString(R.string.list_repo_visibility, repo.getVisibility())); // TODO: Change or change list_repo_visibility name
        isPrivate.setText(getString(R.string.list_repo_private, repo.getPrivate(this))); // TODO: Change or change list_repo_private name
        Picasso.with(this).load(repo.getOwner().getAvatarUrl()).into(avatar);
    }

    /**
     * Start a new activity with the provided Repository
     * @param context Current context, used to start the activity
     * @param repo The Repository for which to start the activity
     */
    public static void start(Context context, Repository repo) {
        Intent intent = new Intent(context, RepositoryActivity.class);
        intent.putExtra(INTENT_EXTRA_REPO, repo);
        context.startActivity(intent);
    }
}