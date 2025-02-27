package com.zmdev.protoplus.db.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zmdev.protoplus.db.Entities.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE)
    void insert(Project project);

    @Update
    void update(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM projects_table")
    List<Project> getProjects();

    @Query("SELECT * FROM projects_table")
    LiveData<List<Project>> getProjectsLiveData();

}
