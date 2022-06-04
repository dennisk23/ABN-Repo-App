package com.example.abnrepos.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.abnrepos.R;
import com.example.abnrepos.RepositoryActivity;
import com.google.gson.annotations.SerializedName;

@Entity
public class Repository implements Parcelable {

    @PrimaryKey
    private long id;
    private final String name, description, visibility;
    @SerializedName("full_name")
    private final String fullName;
    @SerializedName("html_url")
    private final String htmlUrl;
    @SerializedName("private")
    private final boolean isPrivate;
    @Embedded(prefix = "owner_")
    private final Owner owner;

    public Repository(long id, String name, String fullName, String description, String visibility, String htmlUrl, boolean isPrivate, Owner owner) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.visibility = visibility;
        this.htmlUrl = htmlUrl;
        this.isPrivate = isPrivate;
        this.owner = owner;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public String getDescription() { return description; }
    public String getVisibility() { return visibility; }
    public boolean isPrivate() { return isPrivate; }
    public String getHtmlUrl() { return htmlUrl; }
    public Owner getOwner() { return owner; }

    /**
     *
     * @param context Current context, will be used to access resources
     * @return Textual value for the private property
     */
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
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Repository)) return false;

        Repository other = (Repository) obj;
        if(other.getId() != getId()) return false;
        if(!other.getName().equals(getName())) return false;
        if(!other.getFullName().equals(getFullName())) return false;
        if(!other.getDescription().equals(getDescription())) return false;
        if(!other.getVisibility().equals(getVisibility())) return false;
        if(!other.getHtmlUrl().equals(getHtmlUrl())) return false;
        if(other.isPrivate() != isPrivate()) return false;
        if(other.getOwner() != getOwner() && (other.getOwner() == null || getOwner() == null)) return false;
        if(other.getOwner() == getOwner()) return true;
        return other.getOwner().equals(getOwner());
    }

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(description);
        dest.writeString(visibility);
        dest.writeString(String.valueOf(isPrivate));
        dest.writeString(htmlUrl);
        dest.writeParcelable(owner, flags);
    }

    public Repository(Parcel in) {
        id = in.readLong();
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
