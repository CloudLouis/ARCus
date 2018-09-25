package com.sgu.student.arcus.db;
import android.arch.persistence.room.RoomDatabase;

import com.sgu.student.arcus.db.dao.ClassesDao;
import com.sgu.student.arcus.db.dao.MaterialsDao;
import com.sgu.student.arcus.db.entity.ClassesEntity;
import com.sgu.student.arcus.db.entity.MaterialsEntity;

@android.arch.persistence.room.Database(entities = {ClassesEntity.class, MaterialsEntity.class}, version = 4)
public abstract class Database extends RoomDatabase{
    private static Database INSTANCE;
    public abstract ClassesDao getClassesDao();
    public abstract MaterialsDao getMaterialsDao();
}
