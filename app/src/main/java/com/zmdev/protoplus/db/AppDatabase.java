package com.zmdev.protoplus.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.zmdev.protoplus.db.Converters.Param2JsonConverter;
import com.zmdev.protoplus.db.Daos.AttrsDao;
import com.zmdev.protoplus.db.Daos.CommandDao;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Project;
import com.zmdev.protoplus.db.Daos.ProjectDao;



@androidx.room.Database(entities = {Project.class, Command.class, ProtoViewAttrs.class},
        version = 2, exportSchema = false)
@TypeConverters({Param2JsonConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    //Daos
    public abstract ProjectDao projectDAO();
    public abstract CommandDao commandDAO();
    public abstract AttrsDao attrsDAO();


    //Db logic
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "proto_database")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }

    static Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'attrs_table' ADD COLUMN 'stateData' TEXT");
        }
    };

//    static Migration MIGRATION_2_3 = new Migration(2,3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE 'attrs_table' MODIFY 'stateData' TEXT");
//        }
//    };

}
