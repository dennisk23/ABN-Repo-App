package com.example.abnrepos.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.abnrepos.R;
import com.example.abnrepos.RepositoryActivity;
import com.google.gson.annotations.SerializedName;

public class Repository implements Parcelable {

    private String name, visibility, description;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("html_url")
    private String htmlUrl;
    @SerializedName("private")
    private boolean isPrivate;
    private Owner owner;

    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public String getDescription() { return description; }
    public String getVisibility() { return visibility; }
    public boolean isPrivate() { return isPrivate; }
    public String getHtmlUrl() { return htmlUrl; }
    public Owner getOwner() { return owner; }

    public String getPrivate(Context context) {
        if(isPrivate) return context.getString(R.string.repo_private_true);
        return context.getString(R.string.repo_private_false);
    }

    public void openDetailsActivity(Context context) {
        RepositoryActivity.start(context, this);
    }

    public void openInBrowser(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(htmlUrl));
        context.startActivity(intent);
    }

    @Override
    @NonNull
    public String toString() {
        return "Repo (" + name + ", " + fullName + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(description);
        dest.writeString(visibility);
        dest.writeString(String.valueOf(isPrivate));
        dest.writeString(htmlUrl);
        dest.writeParcelable(owner, flags);
    }

    public Repository(Parcel in) {
        name = in.readString();
        fullName = in.readString();
        description = in.readString();
        visibility = in.readString();
        isPrivate = Boolean.parseBoolean(in.readString());
        htmlUrl = in.readString();
        owner = in.readParcelable(Owner.class.getClassLoader());
    }

    public static final Creator<Repository> CREATOR = new Creator<Repository>() {
        @Override
        public Repository createFromParcel(Parcel in) {
            return new Repository(in);
        }

        @Override
        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };
}
