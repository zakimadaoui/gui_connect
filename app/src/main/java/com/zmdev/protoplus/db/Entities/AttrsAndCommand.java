package com.zmdev.protoplus.db.Entities;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class AttrsAndCommand {

    @Embedded
    ProtoViewAttrs attrs;

    @Relation(
            parentColumn = "linkedCommandID",
            entityColumn = "id"
    )
    Command command;


    public AttrsAndCommand() {
        //empty constructor
    }

    @Ignore
    public AttrsAndCommand(ProtoViewAttrs attrs) {
        this.attrs = attrs;
    }
     @Ignore
    public AttrsAndCommand(ProtoViewAttrs attrs, Command command) {
        this.attrs = attrs;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public ProtoViewAttrs getAttrs() {
        return attrs;
    }

    public void setAttrs(ProtoViewAttrs attrs) {
        this.attrs = attrs;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
