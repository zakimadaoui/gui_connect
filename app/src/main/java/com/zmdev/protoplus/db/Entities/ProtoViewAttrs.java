package com.zmdev.protoplus.db.Entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "attrs_table")
public class ProtoViewAttrs implements Cloneable {


    @PrimaryKey(autoGenerate = true)
    int id;
    int projectID;
    int linkedCommandID;

    //attrs
    float x;
    float y;
    int width;
    int height;
    String text;
    int viewType;
    int fieldsNbr;
    double maxProgress;
    double minProgress;
    int orientation; //angle can be {0, 90, 180,270}
    int drawableId;
    int primaryColor;
    int secondaryColor;
    boolean showProgress;
    String stateData;

    @Ignore
    String previewTitle;

    @Ignore
    private boolean isProWidget = false;

    public ProtoViewAttrs() {}

    public ProtoViewAttrs(ProtoViewAttrs other) {
         id = other.id;
         projectID = other.projectID;
         linkedCommandID = other.linkedCommandID;
         x = other.x;
         y = other.y;
         width = other.width;
         height = other.height;
         text = other.text;
         viewType = other.viewType;
         fieldsNbr = other.fieldsNbr;
         maxProgress = other.maxProgress;
         minProgress = other.minProgress;
         orientation = other.orientation;
         drawableId = other.drawableId;
         primaryColor = other.primaryColor;
         secondaryColor = other.secondaryColor;
         showProgress = other.showProgress;
         stateData = other.stateData;
         previewTitle = other.previewTitle;
         isProWidget = other.isProWidget;
    }
    //----------------- Setters and getters -----------------


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getLinkedCommandID() {
        return linkedCommandID;
    }

    public void setLinkedCommandID(int linkedCommandID) {
        this.linkedCommandID = linkedCommandID;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getText() {
        if (text == null) text = "";
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPreviewTitle(String previewTitle) {
        this.previewTitle = previewTitle;
    }

    public String getPreviewTitle() {
        return previewTitle;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public double getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(double max_progress) {
        this.maxProgress = max_progress;
    }

    public double getMinProgress() {
        return minProgress;
    }

    public void setMinProgress(double min_progress) {
        this.minProgress = min_progress;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFieldsNbr() {
        return fieldsNbr;
    }

    public void setFieldsNbr(int fieldsNbr) {
        this.fieldsNbr = fieldsNbr;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getStateData() {
        return stateData;
    }

    public void setStateData(String stateData) {
        this.stateData = stateData;
    }

    @Override
    public String toString() {
        return "ProtoViewAttrs{" +
                "id=" + id +
                ", projectID=" + projectID +
                ", linkedCommandID=" + linkedCommandID +
                ", primaryColor=" + primaryColor +
                ", primaryText='" + text + '\'' +
                '}';
    }

    public boolean isProWidget() {
        return isProWidget;
    }

    public void setProWidget(boolean proWidget) {
        isProWidget = proWidget;
    }
}
