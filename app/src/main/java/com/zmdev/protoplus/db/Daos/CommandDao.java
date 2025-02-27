package com.zmdev.protoplus.db.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.zmdev.protoplus.db.Entities.Command;
import java.util.List;

@Dao
public interface CommandDao {

    @Query("SELECT * FROM commands_table WHERE projectID LIKE :project_id")
    LiveData<List<Command>> getCommandsLive(int project_id);

    @Query("SELECT * FROM commands_table WHERE projectID LIKE :project_id")
    List<Command> getCommands(int project_id);

    @Insert( onConflict = OnConflictStrategy.REPLACE)
    void insert(Command command);

    @Update
    void update(Command command);

    @Delete
    void delete(Command command);

    @Query("DELETE FROM commands_table WHERE projectID LIKE :project_id")
    void delete(int project_id);


}
