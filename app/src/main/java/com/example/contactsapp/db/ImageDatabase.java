package com.example.contactsapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Image.class}, version = 1)
abstract public class ImageDatabase extends RoomDatabase {

    abstract public ImageDao getImageDao();


    private static volatile ImageDatabase instance;
    private static final Object LOCK = new Object();

    public static ImageDatabase newInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null)
                    return createDatabase(context);
                return instance;
            }
        }
            return instance;
    }

    private static ImageDatabase createDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                ImageDatabase.class,
                "image_db.db"
        ).build();
    }

}
