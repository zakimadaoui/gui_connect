package com.zmdev.protoplus.db.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "projects_table")
public class Project {

    @PrimaryKey(autoGenerate = true)
    int id;
    String name;

    public Project(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id == project.id &&
                name.equals(project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
