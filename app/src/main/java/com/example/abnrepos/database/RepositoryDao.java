package com.example.abnrepos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.abnrepos.data.Repository;

import java.util.List;

@Dao
public interface RepositoryDao {
    @Query("SELECT * FROM repository ORDER BY name ASC")
    List<Repository> getAll();

    @Insert
    void insertAll(List<Repository> repositories);

    @Query("DELETE FROM repository")
    void deleteAll();
}
