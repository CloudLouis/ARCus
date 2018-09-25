package com.sgu.student.arcus.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sgu.student.arcus.db.entity.MaterialsEntity;

import java.util.List;

@Dao
public interface MaterialsDao {
    @Query("SELECT * FROM materials")
    public MaterialsEntity[] getMaterialsEntity();

    @Query("UPDATE materials SET title = :new_name WHERE m_id =  :id")
    void updateTitle(String new_name, int id);

    @Query("SELECT * FROM materials WHERE title= :query_name")
    List<MaterialsEntity> retrieveByTitle(String query_name);

    @Insert
    void insert(MaterialsEntity... materials);
    @Delete
    void delete(MaterialsEntity... classes);
}
