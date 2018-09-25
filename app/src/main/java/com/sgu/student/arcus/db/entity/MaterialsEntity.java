package com.sgu.student.arcus.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName="materials",
        foreignKeys=@ForeignKey(entity = ClassesEntity.class,
                                parentColumns = "c_id",
                                childColumns = "class_id"),
        indices = {@Index(value="class_id")}
        )
public class MaterialsEntity {

    @PrimaryKey(autoGenerate = true)
    private int m_id;

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="path")
    private String path;

    @ColumnInfo(name="class_id")
    private int class_id;

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }
}
