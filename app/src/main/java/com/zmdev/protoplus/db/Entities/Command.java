package com.zmdev.protoplus.db.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "commands_table")
public class Command {

    @PrimaryKey(autoGenerate = true)
    int id;
    String opcode;
    String details;
    int paramsNbr;
    Parameter[] params;
    int projectID;


    public Command(int projectID, String opcode, String details, Parameter[] params) {
        this.opcode = opcode;
        this.details = details;
        this.paramsNbr = params.length;
        this.params = params;
        this.projectID = projectID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getParamsNbr() {
        return paramsNbr;
    }

    public void setParamsNbr(int paramsNbr) {
        this.paramsNbr = paramsNbr;
    }

    public Parameter[] getParams() {
        return params;
    }

    public void setParams(Parameter[] params) {
        this.params = params;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(opcode);
        for (Parameter p : params) {
            builder
                    .append("  ") 
                    .append(p.getDefValue());
        }
        return builder.toString();
    }

    public String csv() {
        StringBuilder builder = new StringBuilder();
        builder.append('>');
        builder.append(opcode);
        for (Parameter p : params) {
            builder
                    .append(",")
                    .append(p.getDefValue());
        }
        builder.append('<');
        return builder.toString();
    }
}
