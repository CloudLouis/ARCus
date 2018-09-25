package com.sgu.student.arcus.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sgu.student.arcus.db.entity.ClassesEntity;

import java.util.List;

@Dao
public interface ClassesDao {
    @Query("SELECT * FROM classes")
    public ClassesEntity[] getClassesEntity();

    @Query("SELECT * FROM classes")
    List<ClassesEntity> getClassesEntityList();

    @Query("UPDATE classes SET name = :new_name WHERE c_id =  :id")
    void updateName(String new_name, int id);

    @Query("SELECT * FROM classes WHERE name= :query_name")
    List<ClassesEntity> retrieveByName(String query_name);

    @Insert
    void insert(ClassesEntity... classes);

    @Update
    void update(ClassesEntity... classes);

    @Delete
    void delete(ClassesEntity... classes);
}
