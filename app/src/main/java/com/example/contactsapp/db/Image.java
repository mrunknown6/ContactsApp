package com.example.contactsapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class Image {
    @PrimaryKey
    public int id;

    public String contactId;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] byteArray;

    public Image(String contactId, byte[] byteArray) {
        this.contactId = contactId;
        this.byteArray = byteArray;
    }
}
