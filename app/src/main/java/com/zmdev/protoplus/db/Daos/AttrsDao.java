package com.zmdev.protoplus.db.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import java.util.List;

@Dao
public interface AttrsDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE)
    void insert(ProtoViewAttrs attrs);

    @Update
    void update(ProtoViewAttrs attrs);

    @Delete
    void delete(ProtoViewAttrs attrs);

    @Query("UPDATE attrs_table SET linkedCommandID = -1 WHERE linkedCommandID LIKE :command_id")
    void relink(int command_id);

    @Transaction
    @Query("SELECT * FROM attrs_table WHERE projectID LIKE :projectID")
    LiveData<List<AttrsAndCommand>> getAttrsAndCommands(int projectID);

    @Transaction
    @Query("SELECT * FROM attrs_table WHERE projectID LIKE :projectID")
    List<AttrsAndCommand> getAttrsAndCommandsList(int projectID);

    @Query("DELETE FROM attrs_table WHERE projectID LIKE :project_id")
    void delete(int project_id);

}
