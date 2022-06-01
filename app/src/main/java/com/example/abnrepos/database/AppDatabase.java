package com.example.abnrepos.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.abnrepos.data.Repository;

@Database(entities = {Repository.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "database";
    public abstract RepositoryDao repositoryDao();
}
