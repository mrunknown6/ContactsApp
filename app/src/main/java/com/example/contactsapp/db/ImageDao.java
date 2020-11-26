package com.example.contactsapp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Image image);

    @Delete
    void delete(Image image);

    @Query("SELECT * FROM images WHERE contactId = :contactId")
    Image getImage(String contactId);
}
